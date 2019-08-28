package org.chenile.core.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.chenile.owiz.Command;
import org.springframework.context.ApplicationContext;

public class ChenileConfiguration {

	ApplicationContext applicationContext;
	private String moduleName;
	private List<Command<?>> preProcessorCommands;
	private List<Command<?>> postProcessorCommands;
	private String preProcessorNames;
	private String postProcessorNames;
	private Map<String, ChenileServiceDefinition> services = new HashMap<String, ChenileServiceDefinition>();
	private Map<String, ChenileEventDefinition> events = new HashMap<String, ChenileEventDefinition>();
	
	public ChenileConfiguration(String moduleName,ApplicationContext applicationContext) {
		this.moduleName = moduleName;
		this.applicationContext = applicationContext;
	}
		
    public Map<String, ChenileServiceDefinition> getServices() {
        return services;
    }

    public void setService(String serviceName, ChenileServiceDefinition service) {
        this.services.put(serviceName,service);
    }

	public String getModuleName() {
		return moduleName;
	}

	public void addPreProcessors(String preProcessors) {
		this.preProcessorNames = preProcessors;
	}

	public void addPostProcessors(String postProcessors) {
		this.postProcessorNames = postProcessors;
	}
	
	public List<Command<?>> getPreProcessorCommands(){
		if (preProcessorCommands != null) return preProcessorCommands;
		if (preProcessorNames == null || preProcessorNames.length() == 0) return null;
		preProcessorCommands = new ArrayList<Command<?>>();
		for(String processor: preProcessorNames.split(",")) {
			preProcessorCommands.add((Command<?>)applicationContext.getBean(processor));
		}
		return preProcessorCommands;
	}
	
	public List<Command<?>> getPostProcessorCommands(){
		if (postProcessorCommands != null) return postProcessorCommands;
		if(postProcessorNames == null|| postProcessorNames.length() == 0) return null;
		postProcessorCommands = new ArrayList<Command<?>>();
		for(String processor: postProcessorNames.split(",")) {
			postProcessorCommands.add((Command<?>)applicationContext.getBean(processor));
		}
		return postProcessorCommands;
	}

	public Map<String, ChenileEventDefinition> getEvents() {
		return events;
	}

	public void addEvent(ChenileEventDefinition event) {
		this.events.put(event.getId(),event);
	}
}
