package org.chenile.kafka.producer;

import org.chenile.base.exception.ServerException;
import org.chenile.core.errorcodes.ErrorCodes;
import org.chenile.core.model.ChenileConfiguration;
import org.chenile.core.model.ChenileEventDefinition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;

/**
 * Produces Kafka Events for a ChenileEventDefinition
 * @author Raja Shankar Kolluru
 *
 */
public class EventProducer {
	@Autowired ChenileConfiguration chenileConfiguration;
	@Autowired KafkaTemplate<String, Object> kafkaTemplate;
	
	public void produceEvent(String eventId, Object eventPayload) {
		ChenileEventDefinition ced = chenileConfiguration.getEvents().get(eventId);
		if (ced == null) {
			throw new ServerException(ErrorCodes.SERVICE_EXCEPTION.getSubError(), "ProduceEvent: Unknown event Id" + eventId);
		}
		if (!ced.getType().isAssignableFrom(eventPayload.getClass()))
			throw new ServerException(ErrorCodes.SERVICE_EXCEPTION.getSubError(), 
				"ProduceEvent: eventPayload is not of type " + ced.getClass() + "for event ID " + eventId);
		
		kafkaTemplate.send(ced.getTopic(), eventPayload);
	}
}
