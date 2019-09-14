package org.chenile.filewatch.handler;

import java.nio.file.Path;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public class FileWatcher implements Runnable {
	protected AtomicBoolean stop = new AtomicBoolean(false);
	private WatchService watcher;
	private FileWatcherExecutorService fileWatcherExecutorService;
	private int pollTimeInSeconds = 30;

	public FileWatcher(FileWatcherExecutorService fileWatcherExecutorService, WatchService watcher,
			int pollTimeInSeconds) {
		this.watcher = watcher;
		this.fileWatcherExecutorService = fileWatcherExecutorService;
		this.pollTimeInSeconds = pollTimeInSeconds;
	}

	public boolean isStopped() {
		return stop.get();
	}

	public void stopThread() {
		stop.set(true);
	}
	
	@Override
	public void run() {
		while (!isStopped()) {
			WatchKey key;
			try {
				key = watcher.poll(pollTimeInSeconds,TimeUnit.SECONDS);
			} catch (InterruptedException e) {
				return;
			}			
			if (key == null ) {
				Thread.yield();
				continue;
			}

			for (WatchEvent<?> event : key.pollEvents()) {
				WatchEvent.Kind<?> kind = event.kind();

				@SuppressWarnings("unchecked")
				WatchEvent<Path> ev = (WatchEvent<Path>) event;
				Path pathOfNewFile = ev.context();
				
				if (kind == StandardWatchEventKinds.OVERFLOW) {
					Thread.yield();
					continue;
				} 
				
				fileWatcherExecutorService.handleFile(key,pathOfNewFile);
				
				boolean valid = key.reset();
				if (!valid) {
					break;
				}
			}
			Thread.yield();
		}

	}
}