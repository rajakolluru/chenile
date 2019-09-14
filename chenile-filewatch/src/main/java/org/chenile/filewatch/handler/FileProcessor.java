package org.chenile.filewatch.handler;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

import org.chenile.core.context.ChenileExchange;
import org.chenile.core.entrypoint.ChenileEntryPoint;
import org.chenile.core.model.SubscriberVO;
import org.chenile.filewatch.model.FileWatchDefinition;
import org.chenile.filewatch.reader.CsvReader;
import org.chenile.filewatch.reader.JsonReader;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * FileProcessor knows to handle each file that was discovered by the file watcher
 * This would process the file and finally dispose it off from the watched folder to the processed
 * folder.
 * The processor only starts reading with a header file. The header file is read for the following reasons:
 * <ol>
 * <li>To obtain all the header records that are required to call a particular service
 * <li>To obtain the pointer to the actual file and encoding type
 * <li>To determine if the actual file has been up-loaded completely into the watched folder. It is 
 * assumed that the header file will be uploaded only after the actual file has been uploaded. Also,
 * the last property of the header file should be the last line of the header file. Unless that 
 * property is written to, file processor will not process the file.
 * </ol>
 * @author Raja Shankar Kolluru
 *
 */
public class FileProcessor {
	public static final String ACTUAL_FILE_NAME = "actual.file";
	public static final String ACTUAL_FILE_ENCODING = "actual.file.encoding";
	public static final String HEADER_EXTENSION = ".header";
	public static final String LAST_PROPERTY = "last.property";
	
	@Autowired private ChenileEntryPoint chenileEntryPoint;
	private Path processedDir;
	
	public void processFile(FileWatchDefinition fileWatchDefinition, Path fileDiscovered,
			Path processedDir) {
		String filename = fileDiscovered.toString();
		this.processedDir = processedDir;
		if (!filename.endsWith(HEADER_EXTENSION)) {
			return; // don't deal with any file other than a .header file. 
		}
		Properties headers = extractHeaders(fileDiscovered);
		
		if (headers == null) return;
		if (!headers.containsKey(ACTUAL_FILE_NAME) || !headers.containsKey(ACTUAL_FILE_ENCODING)) {
			// TODO log error that it could not find the keys expected
			return;
		}
		Path recordsFile = fileDiscovered.getParent().resolve(headers.getProperty(ACTUAL_FILE_NAME));	
		invokeServicesForFile(fileWatchDefinition,recordsFile,headers);
		moveFilesToProcessed(fileDiscovered,recordsFile);
	}
	
	private void moveFilesToProcessed(Path headerFilename,Path recordsFile) {
		move(headerFilename);
		move(recordsFile);
	}
	
	private void move(Path file) {
		try {
			Path lastPart = file.getFileName();
			Path target = processedDir.resolve(lastPart);
			Files.move(file, target);
		} catch (IOException e) {
			// TODO log
			e.printStackTrace();
		}
	}
	
	private Properties extractHeaders(Path file) {
		try (InputStream input = Files.newInputStream(file)) {
			Properties props = new Properties();
            props.load(input);
            if (props.containsKey(LAST_PROPERTY)) {
            	props.remove(LAST_PROPERTY);
            }else {
            	// TODO - log
            	return null;
            }
            return props;
        } catch (IOException ex) {
        	// TODO - log
        	return null;
        }	
	}
	
	private void invokeServicesForFile(FileWatchDefinition fileWatchDefinition,
			Path file, Properties headers) {
		try {
			for (Object o: fileReader(fileWatchDefinition,file,(String)headers.get(ACTUAL_FILE_ENCODING))) {
				invokeServicesPerRecord(fileWatchDefinition,o,headers);
			}
		} catch (Exception e) {
			// TODO  - log
			e.printStackTrace();
		}		
	}
	
	private Iterable<Object> fileReader(FileWatchDefinition fileWatchDefinition, 
			Path file,String encodingType) throws Exception{
		switch(encodingType.toUpperCase()) {
		case "CSV":
			return new CsvReader(file, fileWatchDefinition.getRecordClass());
		case "JSON":
			return new JsonReader(file,fileWatchDefinition.getRecordClass());
		}
		return null;
	}

	private void invokeServicesPerRecord(FileWatchDefinition fileWatchDefinition,
			Object record,Properties headers){
		// Find all the subscriptions for the given file watch definition and invoke them one by one
		for (SubscriberVO subscriber: fileWatchDefinition.getSubscribers()) {
			// read the file and for each record invoke this combination of service and 
			// operation definitions
			ChenileExchange exchange = new ChenileExchange();
			exchange.setServiceDefinition(subscriber.serviceDefinition);
			exchange.setOperationDefinition(subscriber.operationDefinition);
			copyHeadersToExchange(exchange,headers);
			exchange.setBody(record);
			chenileEntryPoint.execute(exchange);
		}
	}

	private void copyHeadersToExchange(ChenileExchange exchange, Properties headers) {
		headers.forEach((key,value) -> exchange.setHeader((String)key, value));	
	}

}
