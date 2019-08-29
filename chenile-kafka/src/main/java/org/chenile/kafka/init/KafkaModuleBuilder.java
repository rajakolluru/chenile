package org.chenile.kafka.init;

import org.chenile.core.model.ChenileConfiguration;
import org.chenile.core.model.ChenileEventDefinition;
import org.chenile.kafka.entry.KafkaEntryPoint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.event.EventListener;

public class KafkaModuleBuilder {
	@Autowired
	private ChenileConfiguration chenileConfiguration;
	
	private String kafkaEntryPoint = "kafkaEntryPoint";
	@Autowired private ApplicationContext applicationContext;
	
	public KafkaModuleBuilder(String kafkaEntryPoint) {
		this.kafkaEntryPoint = kafkaEntryPoint;
	}

	@EventListener(ApplicationReadyEvent.class)
	public void build() throws Exception {
		System.err.println("Here");
		for (ChenileEventDefinition ced : chenileConfiguration.getEvents().values()) {
			if(!ced.getOriginatingModuleName().equals(chenileConfiguration.getModuleName())) continue;
			configureEntryPoint(ced);
		}
	}

	private void configureEntryPoint(ChenileEventDefinition ced) {
		KafkaEntryPoint kep = applicationContext.getBean(kafkaEntryPoint,KafkaEntryPoint.class);
		kep.listen(ced);		
	}

}
