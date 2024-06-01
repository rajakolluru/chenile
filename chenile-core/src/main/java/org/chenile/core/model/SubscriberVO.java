package org.chenile.core.model;

/**
 * The combination of service and operation that are mapped to a Chenile event.
 */
public class SubscriberVO {
	public ChenileServiceDefinition serviceDefinition;
	public OperationDefinition operationDefinition;
	public SubscriberVO(ChenileServiceDefinition serviceDefinition, OperationDefinition operationDefinition) {
		this.operationDefinition = operationDefinition;
		this.serviceDefinition = serviceDefinition;
	}
}
