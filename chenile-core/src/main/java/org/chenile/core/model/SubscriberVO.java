package org.chenile.core.model;

/**
 * Contains the service definition and operation definition. Most chenile subscribers will need it
 * @author Raja Shankar Kolluru
 *
 */
public class SubscriberVO {
	public ChenileServiceDefinition serviceDefinition;
	public OperationDefinition operationDefinition;
	public SubscriberVO(ChenileServiceDefinition serviceDefinition, OperationDefinition operationDefinition) {
		this.operationDefinition = operationDefinition;
		this.serviceDefinition = serviceDefinition;
	}
}
