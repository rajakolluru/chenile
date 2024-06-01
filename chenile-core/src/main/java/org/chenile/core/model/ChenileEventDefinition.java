package org.chenile.core.model;

import java.util.HashSet;
import java.util.Set;

/**
 * Chenile keeps track of events. It maps multiple combinations of Service/Operations to
 * an event ID.
 */
public class ChenileEventDefinition implements ModuleAware{
	
	/**
	 * Event ID. All chenile services listen for an event specified by this ID.
	 */
	private String id;
	/**
	 * The Kafka topic at which Chenile listens for this event
	 */
	private String topic;
	private Class<?> type;
	private String originatingModuleName;
	private final Set<SubscriberVO> eventSubscribers = new HashSet<SubscriberVO>();
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}	
	public Class<?> getType() {
		return type;
	}
	public void setType(Class<?> eventType) {
		this.type = eventType;
	}
	public String getTopic() {
		return topic;
	}
	public void setTopic(String eventTopic) {
		this.topic = eventTopic;
	}
	public Set<SubscriberVO> getEventSubscribers() {
		return eventSubscribers;
	}
	public void addEventSubscriber(ChenileServiceDefinition serviceDefinition,OperationDefinition operationDefinition) {
		this.eventSubscribers.add(new SubscriberVO(serviceDefinition,operationDefinition));
	}
	public String getOriginatingModuleName() {
		return originatingModuleName;
	}
	public void setOriginatingModuleName(String originatingModuleName) {
		this.originatingModuleName = originatingModuleName;
	}

	
}
