package org.chenile.core.context;

import org.chenile.core.model.ChenileConfiguration;
import org.chenile.core.model.ChenileServiceDefinition;
import org.chenile.core.model.OperationDefinition;
import org.chenile.core.model.SubscriberVO;
import org.springframework.beans.factory.annotation.Autowired;

public class ChenileExchangeBuilder {
	@Autowired ChenileConfiguration chenileConfiguration;
	private ChenileServiceDefinition findService(String serviceName) {
		return chenileConfiguration.getServices().get(serviceName);
	}
	
	private OperationDefinition findOperationInService(ChenileServiceDefinition serviceDefinition, String opName) {
		for (OperationDefinition od: serviceDefinition.getOperations()) {
			if (od.getName().equals(opName)){
				return od;
			}
		}
		return null;
	}
	
	public ChenileExchange makeExchange(String serviceName, String opName,HeaderCopier headerCopier) {
		ChenileServiceDefinition serviceDefinition = findService(serviceName);
		OperationDefinition operationDefinition = findOperationInService(serviceDefinition, opName);
		ChenileExchange exchange = new ChenileExchange();
		exchange.setServiceDefinition(serviceDefinition);
		exchange.setOperationDefinition(operationDefinition);
		if (headerCopier != null) headerCopier.copy(exchange);
		return exchange;
	}

	public SubscriberVO makeSubscriberVO(String serviceName, String operationName) {
		ChenileServiceDefinition serviceDefinition = findService(serviceName);
		OperationDefinition operationDefinition = findOperationInService(serviceDefinition, operationName);
		return new SubscriberVO(serviceDefinition,operationDefinition);
	}
}
