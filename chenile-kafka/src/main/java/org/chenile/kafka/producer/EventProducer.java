package org.chenile.kafka.producer;

import org.apache.kafka.clients.producer.ProducerRecord;
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
	/**
	 * If anyone overrides {@link #produceEvent(String, ProducerRecord)}, they need kafkaTemplate to send the message.
	 * They can also autowire kafkaTemplate and use.
	 */
	@Autowired protected KafkaTemplate<String, Object> kafkaTemplate;
	
	public void produceEvent(String eventId, Object eventPayload) {
		ChenileEventDefinition ced = getChenileDefinition(eventId);
		ProducerRecord<String, Object> producerRecord = new ProducerRecord<>(ced.getTopic(), eventPayload);
		this.produceEvent(eventId, producerRecord);
	}
	
	public void produceEvent(String eventId, ProducerRecord<String, Object> producerRecord) {
		Object eventPayload = producerRecord.value();
		ChenileEventDefinition ced = getChenileDefinition(eventId);
		
		if (!ced.getType().isAssignableFrom(eventPayload.getClass()))
			throw new ServerException(ErrorCodes.SERVICE_EXCEPTION.getSubError(), 
				"ProduceEvent: eventPayload is not of type " + ced.getClass() + "for event ID " + eventId);

		kafkaTemplate.send(producerRecord);
	}
	
	protected ChenileEventDefinition getChenileDefinition(String eventId) {
		ChenileEventDefinition ced = chenileConfiguration.getEvents().get(eventId);
		if (ced == null) {
			throw new ServerException(ErrorCodes.SERVICE_EXCEPTION.getSubError(), "ProduceEvent: Unknown event Id" + eventId);
		}
		return ced;
	}
}
