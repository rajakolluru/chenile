package org.chenile.filewatch.model;

import java.util.HashSet;
import java.util.Set;

import org.chenile.core.model.ModuleAware;
import org.chenile.core.model.SubscriberVO;

public class FileWatchDefinition implements ModuleAware{
	@Override
	public String toString() {
		return "FileWatchDefinition [fileWatchId=" + fileWatchId + ", originatingModuleName=" + originatingModuleName
				+ ", recordClass=" + recordClass + ", dirToWatch=" + dirToWatch 
				+ "]";
	}
	public static final String EXTENSION = "fileWatch"; // register as a filewatch extension for chenile 
	
	private String fileWatchId;
	private String originatingModuleName;
	/**
	 * The class to which each of the record in the file belongs. This class must match
	 * the parameter arguments for each of the subscribers to this FileWatchDefinition
	 */
	private Class<?> recordClass;
	public String getFileWatchId() {
		return fileWatchId;
	}
	public void setFileWatchId(String fileWatchId) {
		this.fileWatchId = fileWatchId;
	}
	public String getDirToWatch() {
		return dirToWatch;
	}
	public void setDirToWatch(String dirToWatch) {
		this.dirToWatch = dirToWatch;
	}
	
	
	public Set<SubscriberVO> getSubscribers() {
		return subscribers;
	}
	public void addSubscriber(SubscriberVO subscriber) {
		this.subscribers.add(subscriber);
	}
	public String getOriginatingModuleName() {
		return originatingModuleName;
	}
	public void setOriginatingModuleName(String originatingModuleName) {
		this.originatingModuleName = originatingModuleName;
	}
	public Class<?> getRecordClass() {
		return recordClass;
	}
	public void setRecordClass(Class<?> recordClass) {
		this.recordClass = recordClass;
	}
	private String dirToWatch;
	private Set<SubscriberVO> subscribers = new HashSet<>();
}
