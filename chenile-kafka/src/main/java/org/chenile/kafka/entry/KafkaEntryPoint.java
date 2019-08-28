package org.chenile.kafka.entry;

import java.util.Iterator;
import java.util.Map;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.header.Header;
import org.apache.kafka.common.header.Headers;
import org.chenile.base.exception.ServerException;
import org.chenile.core.context.ChenileExchange;
import org.chenile.core.errorcodes.ErrorCodes;
import org.chenile.core.event.EventProcessor;
import org.chenile.core.model.ChenileConfiguration;
import org.chenile.core.model.ChenileEventDefinition;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Scope;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.KafkaMessageListenerContainer;
import org.springframework.kafka.listener.MessageListener;

/**
 * The listener for Kafka Messages. This would be tied to a particular {@link ChenileEventDefinition}
 * Hence there would be as many instances of this class as there are {@link ChenileEventDefinition}s to process
 * @author Raja Shankar Kolluru
 *
 */
public class KafkaEntryPoint implements MessageListener<String,Object>, DisposableBean{	
	@Autowired
	private ChenileConfiguration chenileConfiguration;
	
	private EventProcessor eventProcessor;
	
	private ChenileEventDefinition chenileEventDefinition;
	
	@Autowired private Map<String, Object> consumerProps;
	private KafkaMessageListenerContainer<String, Object> kafkaMessageListenerContainer;

	public KafkaEntryPoint(EventProcessor eventProcessor) {
		this.eventProcessor = eventProcessor;
	}

	@Override
	 public void onMessage(ConsumerRecord<String, Object> message) {
		Headers headers = message.headers();
		Object eventPayload = message.value();
		ChenileExchange exchange = new ChenileExchange();
		setHeaders(headers, exchange);
		if (!chenileEventDefinition.getType().isAssignableFrom(eventPayload.getClass()))
			throw new ServerException(ErrorCodes.SERVICE_EXCEPTION.getSubError(), 
				"ProduceEvent: eventPayload is not of type " + chenileEventDefinition.getClass() + "for event ID " + chenileEventDefinition.getId());
		eventProcessor.handleEvent(chenileEventDefinition, exchange);
	}
	
	public void listen(ChenileEventDefinition ced) {
		String beanName = chenileConfiguration.getModuleName() + "_" + ced.getId();
		this.chenileEventDefinition = ced;
		ContainerProperties containerProperties = new ContainerProperties(ced.getTopic());
		containerProperties.setGroupId(beanName);
		containerProperties.setMessageListener(this);
		kafkaMessageListenerContainer = createContainer(containerProperties);
		kafkaMessageListenerContainer.setBeanName(beanName);
		kafkaMessageListenerContainer.start();
	}

	private void setHeaders(Headers messageHeaders, ChenileExchange exchange) {
		 Iterator<Header> iterator = messageHeaders.iterator();
		 while(iterator.hasNext()) {
			 Header header = iterator.next();
			 String key = header.key();
			 byte[] value = header.value();
			 exchange.setHeader(key, new String(value));
		 }
	}
	
    public KafkaMessageListenerContainer<String, Object> createContainer(ContainerProperties containerProps) {
		 DefaultKafkaConsumerFactory<String, Object> cf = new DefaultKafkaConsumerFactory<>(consumerProps);
		 KafkaMessageListenerContainer<String, Object> container = 
				 new KafkaMessageListenerContainer<>(cf, containerProps);
		 return container;
    }

	@Override
	public void destroy() throws Exception {
		if (kafkaMessageListenerContainer != null)
			kafkaMessageListenerContainer.stop();		
	}

}