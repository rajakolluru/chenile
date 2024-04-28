package org.chenile.http.init;

import java.util.HashMap;
import java.util.Map;

import org.chenile.core.entrypoint.ChenileEntryPoint;
import org.chenile.core.model.ChenileConfiguration;
import org.chenile.core.model.ChenileServiceDefinition;
import org.chenile.core.model.OperationDefinition;
import org.chenile.http.handler.HttpEntryPoint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.web.servlet.handler.SimpleUrlHandlerMapping;

public class HttpModuleBuilder extends SimpleUrlHandlerMapping {

	@Autowired
	private ChenileConfiguration serviceConfiguration;
	@Autowired
	private ChenileEntryPoint chenileEntryPoint;

	@EventListener(ApplicationReadyEvent.class)
	@Order(15)
	public void build() throws Exception {
		final Map<String, Object> urlMap = new HashMap<>();
		for (ChenileServiceDefinition s : serviceConfiguration.getServices().values()) {
			// ignore if the service does not belong to the current module.
			if (!s.getModuleName().equals(serviceConfiguration.getModuleName())) continue; 
			for (OperationDefinition operationDefinition : s.getOperations()) {
				if (operationDefinition.getUrl() == null) continue;
				urlMap.put(operationDefinition.getUrl(), new HttpEntryPoint(s, operationDefinition, chenileEntryPoint));
			}
		}
		setUrlMap(urlMap);
		registerHandlers(urlMap);
	}

}
