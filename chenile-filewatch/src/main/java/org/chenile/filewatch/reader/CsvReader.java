package org.chenile.filewatch.reader;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Iterator;

import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;

/**
 * Read a CSV file with the first line as the header. 
 * Parse it and return an iterator
 * @author Raja Shankar Kolluru
 *
 */
public class CsvReader implements Iterable<Object>{
	
	private MappingIterator<Object> mappingIterator;
	private final CsvMapper csvMapper = new CsvMapper();
	
	public CsvReader(Path path, Class<?> clazz) throws Exception{  
		// first line is the header and has the schema in it
		CsvSchema schema = CsvSchema.emptySchema().withHeader();
		ObjectReader objectReader = csvMapper.readerFor(clazz).with(schema);
		mappingIterator = objectReader.readValues(Files.newInputStream(path));
	}
	
	@Override
	public Iterator<Object> iterator() {
		return mappingIterator;
	}
}
