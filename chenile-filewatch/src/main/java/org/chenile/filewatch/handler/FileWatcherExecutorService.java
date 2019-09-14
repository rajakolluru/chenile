package org.chenile.filewatch.handler;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;

import org.chenile.filewatch.model.FileWatchDefinition;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * The main file for file watch
 * This allows the handler to register {@link FileWatchDefinition}. It also starts the 
 * file watch by executing the {@link FileWatcher} in a separate thread.
 * Once a file is discovered by the file watcher, this class picks it up and hands it over to
 * the fileProcessor for further processing.
 * @author Raja Shankar Kolluru
 *
 */
public class FileWatcherExecutorService {
	protected String baseWatchFolder;
	protected String baseProcessedFolder;
	protected int pollTimeInSeconds;
	@Autowired protected ExecutorService executorService;
	private WatchService watcher;
	@Autowired private FileProcessor fileProcessor;
	/**
	 * Ability to inject a file system facilitates mocking
	 */
	private FileSystem fileSystem;
	
	public FileWatcherExecutorService(String baseWatchFolder, String baseProcessedFolder,
			int pollTimeInSeconds,FileSystem fileSystem) {
		this.baseWatchFolder = baseWatchFolder + File.separator;
		this.baseProcessedFolder = baseProcessedFolder + File.separator;
		this.pollTimeInSeconds = pollTimeInSeconds;
		this.fileSystem = fileSystem;
		try {
			this.watcher = fileSystem.newWatchService();
		} catch (IOException e) {			
		}
	}
		
	private class WatchInfo {
		public Path watchDir;
		public Path processedDir;
		public FileWatchDefinition fileWatchDefinition;
		public WatchInfo(Path watchDir,Path processedDir,FileWatchDefinition fileWatchDefinition) {
			this.watchDir = watchDir; 
			this.processedDir = processedDir;
			this.fileWatchDefinition = fileWatchDefinition;
		}
	}
	
	private Map<WatchKey,WatchInfo> watchKeyToInfoMap = new HashMap<>();
	
	public void registerWatch(FileWatchDefinition fileWatchDefinition) {	
		Path watchDir = fileSystem.getPath(baseWatchFolder + fileWatchDefinition.getDirToWatch());
		Path processedDir = fileSystem.getPath(
				this.baseProcessedFolder + fileWatchDefinition.getDirToWatch());
		try {
			WatchKey key = watchDir.register(this.watcher, StandardWatchEventKinds.ENTRY_MODIFY,
					StandardWatchEventKinds.ENTRY_CREATE);
			watchKeyToInfoMap.put(key,new WatchInfo(watchDir,processedDir,fileWatchDefinition));
			processExisting(fileWatchDefinition,watchDir,processedDir);
			
		} catch (IOException e) {
			// TODO log errors
			e.printStackTrace();
		}
	}
	
	private void processExisting(FileWatchDefinition fileWatchDefinition,Path watchDir,
					Path processedDir) {
		try {
			Files.list(watchDir)
			.forEach(path -> {
				fileProcessor.processFile(fileWatchDefinition,path, processedDir); 
			});
		} catch (IOException e) {
			// log the trace
		}
	}
	
	public void startWatch() {
		FileWatcher fileWatcher = new FileWatcher(this, watcher,pollTimeInSeconds);
		executorService.submit(fileWatcher);
	}
	
	/**
	 * This will be the callback for every file that has been discovered by the {@link FileWatcher}
	 * @param key
	 * @param fileSeen
	 */
	public void handleFile(WatchKey key, Path fileSeen) {
		WatchInfo watchInfo = watchKeyToInfoMap.get(key);
		Path fullyQualifiedPath = watchInfo.watchDir.resolve(fileSeen);
		fileProcessor.processFile(watchInfo.fileWatchDefinition,fullyQualifiedPath, watchInfo.processedDir);
	}
	

}
