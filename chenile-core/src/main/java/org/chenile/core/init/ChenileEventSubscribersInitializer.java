package org.chenile.core.init;

import java.lang.reflect.Method;
import java.util.Set;

import org.chenile.base.exception.ServerException;
import org.chenile.core.context.EventLog;
import org.chenile.core.errorcodes.ErrorCodes;
import org.chenile.core.model.ChenileConfiguration;
import org.chenile.core.model.ChenileEventDefinition;
import org.chenile.core.model.ChenileServiceDefinition;
import org.chenile.core.model.OperationDefinition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;

/**
 * Runs after all the events and services are registered in Chenile.
 * It goes through all services and registers the operations to the subscribed events. 
 * This will enable Chenile to trigger all the subscribers upon the receipt of an event.
 * @author Raja Shankar Kolluru
 *
 */
public class ChenileEventSubscribersInitializer {
	@Autowired ChenileConfiguration chenileConfiguration;

	@EventListener(ApplicationReadyEvent.class)
	@Order(20)
	public void init() {
		for (ChenileServiceDefinition s : chenileConfiguration.getServices().values()) {
			// ignore if the service does not belong to the current module.
			if (!s.getModuleName().equals(chenileConfiguration.getModuleName())) continue; 
			for (OperationDefinition operationDefinition : s.getOperations()) {
				Set<String> subscriptions = operationDefinition.getEventSubscribedTo();
				if (subscriptions == null || subscriptions.isEmpty()) continue;
				for (String eventId: subscriptions) {
					registerSubscriber(s,operationDefinition,eventId);
				}
			}
		}
	}

	private void registerSubscriber(ChenileServiceDefinition s, OperationDefinition operationDefinition, String eventId) {
		ChenileEventDefinition ced = chenileConfiguration.getEvents().get(eventId);
		if (ced == null) {
			ced = new ChenileEventDefinition();
			ced.setId(eventId);
			ced.setType(operationDefinition.getInput());
			chenileConfiguration.addEvent(ced);
		}
		if (!ced.getType().equals(operationDefinition.getInput())) {
			throw new ServerException(ErrorCodes.EVENT_TYPE_MISMATCH.getSubError(),
					new Object[]{s.getId(),
					operationDefinition.getName(),eventId, ced.getType(),
					operationDefinition.getInput()});
		}

		ced.addEventSubscriber(s,operationDefinition);
	}

}
