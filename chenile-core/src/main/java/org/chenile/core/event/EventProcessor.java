package org.chenile.core.event;

import java.util.Set;

import org.chenile.base.exception.ServerException;
import org.chenile.core.context.ChenileExchange;
import org.chenile.core.context.EventLog;
import org.chenile.core.entrypoint.ChenileEntryPoint;
import org.chenile.core.errorcodes.ErrorCodes;
import org.chenile.core.model.ChenileConfiguration;
import org.chenile.core.model.ChenileEventDefinition;
import org.chenile.core.model.SubscriberVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

/**
 * Processes an event. Sends the event payload to each of the event subscribers.
 * Handles event logging.
 * 
 * @author Raja Shankar Kolluru
 *
 */
public class EventProcessor {
	@Autowired  @Qualifier("chenileServiceConfiguration") ChenileConfiguration chenileServiceConfiguration;
	@Autowired  ChenileEntryPoint chenileEntryPoint;

	
	public EventProcessor() {
	}
	
	public void handleEvent(String eventId, Object eventPayload) {
		ChenileEventDefinition ced = chenileServiceConfiguration.getEvents().get(eventId);
		if (ced == null) {
			throw new ServerException(ErrorCodes.UNKNOWN_EVENT.getSubError(), "Unknown event " + eventId);
		}
		Set<SubscriberVO> subscribers = ced.getEventSubscribers();
		if(subscribers == null || subscribers.size() == 0) return;
		for(SubscriberVO subscriber: subscribers) {
			ChenileExchange chenileExchange = new ChenileExchange();
			chenileExchange.setServiceDefinition(subscriber.serviceDefinition);
			chenileExchange.setOperationDefinition(subscriber.operationDefinition);
			
			setHeaders(chenileExchange);
			chenileExchange.setBody(eventPayload);
			chenileEntryPoint.execute(chenileExchange);
		}
	}

	public void handleEvent(ChenileEventDefinition ced, ChenileExchange chenileExchange) {
		Set<SubscriberVO> subscribers = ced.getEventSubscribers();
		if(subscribers == null || subscribers.size() == 0) return;
		for(SubscriberVO subscriber: subscribers) {
			ChenileExchange exchange = new ChenileExchange(chenileExchange);
			exchange.setServiceDefinition(subscriber.serviceDefinition);
			exchange.setOperationDefinition(subscriber.operationDefinition);
			
			chenileEntryPoint.execute(exchange);
			
			// TODO - must implement CompletableFuture here optionally
			EventLog eventLog = (EventLog)exchange.getResponse();
			chenileServiceConfiguration.getEventLogger().log(eventLog);
		}
	}

	/**
	 * An Extension point to allow sub classes to set the headers in the Chenile Exchange prior to
	 * invoking it. These headers would be app specific and hence this class needs to be subclassed 
	 * by the actual application for the purpose of setting headers.
	 * @param chenileExchange
	 */
	protected void setHeaders(ChenileExchange chenileExchange) {
		
		
	}
}
