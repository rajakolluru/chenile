package org.chenile.core.model;

import java.util.HashSet;
import java.util.Set;

public class ChenileEventDefinition {
	
	public class EventSubscriber {
		public ChenileServiceDefinition serviceDefinition;
		public OperationDefinition operationDefinition;
		public EventSubscriber(ChenileServiceDefinition serviceDefinition, OperationDefinition operationDefinition) {
			this.operationDefinition = operationDefinition;
			this.serviceDefinition = serviceDefinition;
		}
	}
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
	private Set<EventSubscriber> eventSubscribers = new HashSet<EventSubscriber>();
	
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
	public Set<EventSubscriber> getEventSubscribers() {
		return eventSubscribers;
	}
	public void addEventSubscriber(ChenileServiceDefinition serviceDefinition,OperationDefinition operationDefinition) {
		this.eventSubscribers.add(new EventSubscriber(serviceDefinition,operationDefinition));
	}
	public String getOriginatingModuleName() {
		return originatingModuleName;
	}
	public void setOriginatingModuleName(String originatingModuleName) {
		this.originatingModuleName = originatingModuleName;
	}

	
}
