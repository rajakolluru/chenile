package org.chenile.configuration.kafka;


import java.util.HashMap;
import java.util.Map;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.chenile.core.event.EventProcessor;
import org.chenile.kafka.entry.KafkaEntryPoint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.Scope;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;

@Configuration
@EnableKafka
@PropertySource("classpath:${chenile.properties:chenile.properties}")
public class ChenileKafkaConfiguration {

	@Value("${chenile.event.processor:chenileEventProcessor}") private String eventProcessorName;
	@Autowired private ApplicationContext applicationContext;
	
	/**
	 * The factory method employs a trick to allow particular projects to override the default event processor.
	 * Event Processor is defaulted to chenileEventProcessor. However, if a particular project 
	 * configured a specific event processor then the chenile.properties file must provide a value for
	 * chenile.event.processor property. Then that event processor will be picked up instead.
	 * This bean is also defined as a prototype bean since there can be several instances of this bean
	 * required.
	 * @return
	 */
    @Bean
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    public KafkaEntryPoint kafkaEntryPoint(){
    	EventProcessor eventProcessor = applicationContext.getBean(eventProcessorName,EventProcessor.class);
        return new KafkaEntryPoint(eventProcessor);
    }
    
    @Bean
    public KafkaTemplate<String, Object> kafkaTemplate() {
    	 Map<String, Object> senderProps = senderProps();
    	 ProducerFactory<String, Object> pf =
    	 new DefaultKafkaProducerFactory<String, Object>(senderProps);
    	 KafkaTemplate<String,Object> template = new KafkaTemplate<>(pf);
    	 return template;
    }
    
    private Map<String, Object> senderProps() {
    	 Map<String, Object> props = new HashMap<>();
    	 props.putAll(genericProperties());
    	 props.putAll(senderProperties());
    	 props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
    	 props.put(ProducerConfig.RETRIES_CONFIG, 0);
    	 props.put(ProducerConfig.BATCH_SIZE_CONFIG, 16384);
    	 props.put(ProducerConfig.LINGER_MS_CONFIG, 1);
    	 props.put(ProducerConfig.BUFFER_MEMORY_CONFIG, 33554432);
    	 props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
    	 props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
    	 return props;
    }
    
    @Bean
    public Map<String, Object> consumerProps() {
    	 Map<String, Object> props = new HashMap<>();
    	 props.putAll(genericProperties());
    	 props.putAll(consumerProperties());
    	 props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
    	 // props.put(ConsumerConfig.GROUP_ID_CONFIG, group);
    	 props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, true);
    	 props.put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, "100");
    	 props.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, "15000");
    	 props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
    	 props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
    	 return props;
    }
    
    @Bean
	@ConfigurationProperties(prefix = "chenile.kafka.generic")
	public Map<String,Object> genericProperties() {
		return new HashMap<String, Object>(); 
	}
    
    @Bean
	@ConfigurationProperties(prefix = "chenile.kafka.sender")
	public Map<String,Object> senderProperties() {
		return new HashMap<String, Object>();
	}
    
    @Bean
	@ConfigurationProperties(prefix = "chenile.kafka.consumer")
	public Map<String,Object> consumerProperties() {
		return new HashMap<String, Object>();
	}
}
