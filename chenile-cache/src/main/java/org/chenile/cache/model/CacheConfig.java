package org.chenile.cache.model;

public class CacheConfig {
	public static final String EXTENSION = "cacheConfigs";
	public int getTimeToLiveInSeconds() {
		return timeToLiveInSeconds;
	}
	public void setTimeToLiveInSeconds(int timeToLiveInSeconds) {
		this.timeToLiveInSeconds = timeToLiveInSeconds;
	}
	public int getBackupCount() {
		return backupCount;
	}
	public void setBackupCount(int backupCount) {
		this.backupCount = backupCount;
	}
	public int getMaxSize() {
		return maxSize;
	}
	public void setMaxSize(int maxSize) {
		this.maxSize = maxSize;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	
	private int timeToLiveInSeconds = 3600; // cache for an hour by default
	private int backupCount = 2;
	private int maxSize = 10000;
	private String id ;
}
