package org.chenile.kafka.producer;

import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.internals.RecordHeaders;
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
	@Autowired protected KafkaTemplate<String, Object> kafkaTemplate;
	
	public void produceEvent(String eventId, Object eventPayload) {
		ChenileEventDefinition ced = getChenileEventDefinition(eventId);
		RecordHeaders headers = new RecordHeaders();
		populateHeaders(headers);
		ProducerRecord<String, Object> producerRecord = new ProducerRecord<>(
				ced.getTopic(), null, null, eventPayload, headers);
		if (!ced.getType().isAssignableFrom(eventPayload.getClass()))
			throw new ServerException(ErrorCodes.SERVICE_EXCEPTION.getSubError(), 
				"ProduceEvent: eventPayload is not of type " + ced.getClass() + "for event ID " + eventId);
		kafkaTemplate.send(producerRecord);
	}
	
	/**
	 * An extension point for sub classes to implement custom headers
	 */	
	protected void populateHeaders(RecordHeaders headers) {}
	
	protected ChenileEventDefinition getChenileEventDefinition(String eventId) {
		ChenileEventDefinition ced = chenileConfiguration.getEvents().get(eventId);
		if (ced == null) {
			throw new ServerException(ErrorCodes.SERVICE_EXCEPTION.getSubError(), "ProduceEvent: Unknown event Id" + eventId);
		}
		return ced;
	}
}
