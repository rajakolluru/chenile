package org.chenile.core.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.chenile.base.exception.ConfigurationException;
import org.chenile.core.context.ChenileExchange;
import org.chenile.core.errorcodes.ErrorCodes;
import org.chenile.core.event.EventLogger;
import org.chenile.core.interceptors.ChenileExceptionHandler;
import org.chenile.owiz.Command;
import org.springframework.context.ApplicationContext;


/**
 * The core class that contains the entire ChenileConfiguration. 
 * This contains the configuration information from chenile.properties as well as 
 * a reference to all the configured services and events in Chenile
 * @author Raja Shankar Kolluru
 *
 */
public class ChenileConfiguration {

	ApplicationContext applicationContext;
	private String moduleName;
	private List<Command<ChenileExchange>> preProcessorCommands;
	private List<Command<ChenileExchange>> postProcessorCommands;
	private String preProcessorNames;
	private String postProcessorNames;
	private String version;
	private Map<String, ChenileServiceDefinition> services = new HashMap<String, ChenileServiceDefinition>();
	private Map<String, ChenileEventDefinition> events = new HashMap<String, ChenileEventDefinition>();
	/**
	 * Leaving room for other extensions here 
	 */
	private Map<String,Map<String,?>> otherExtensions = new HashMap<String,Map<String,?>>();
	private String chenileExceptionHandlerName;
	private String eventLoggerName;
	private final Map<String, TrajectoryDefinition> trajectoryDefinitions = new HashMap<>();
	
	public String getEventLoggerName() {
		return eventLoggerName;
	}

	public void setEventLoggerName(String eventLoggerName) {
		this.eventLoggerName = eventLoggerName;
	}

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
	
	@SuppressWarnings("unchecked")
	public List<Command<ChenileExchange>> getPreProcessorCommands(){
		if (preProcessorCommands != null) return preProcessorCommands;
		if (preProcessorNames == null || preProcessorNames.length() == 0) return null;
		preProcessorCommands = new ArrayList<Command<ChenileExchange>>();
		for(String processor: preProcessorNames.split(",")) {
			preProcessorCommands.add((Command<ChenileExchange>)applicationContext.getBean(processor));
		}
		return preProcessorCommands;
	}
	
	@SuppressWarnings("unchecked")
	public List<Command<ChenileExchange>> getPostProcessorCommands(){
		if (postProcessorCommands != null) return postProcessorCommands;
		if(postProcessorNames == null|| postProcessorNames.length() == 0) return null;
		postProcessorCommands = new ArrayList<Command<ChenileExchange>>();
		for(String processor: postProcessorNames.split(",")) {
			postProcessorCommands.add((Command<ChenileExchange>)applicationContext.getBean(processor));
		}
		return postProcessorCommands;
	}

	public Map<String, ChenileEventDefinition> getEvents() {
		return events;
	}

	public void addEvent(ChenileEventDefinition event) {
		this.events.put(event.getId(),event);
	}

	public Map<String,Map<String,?>> getOtherExtensions() {
		return otherExtensions;
	}

	public void setOtherExtensions(Map<String,Map<String,?>> otherExtensions) {
		this.otherExtensions = otherExtensions;
	}

	public void setChenileExceptionHandlerName(String exceptionHandlerName) {
		this.chenileExceptionHandlerName = exceptionHandlerName;		
	}
	
	
	public ChenileExceptionHandler getChenileExceptionHandler() {
		return (ChenileExceptionHandler) applicationContext.getBean(this.chenileExceptionHandlerName);
	}

	public String getVersion() {
		return this.version;
	}
	
	public void setVersion(String version) {
		this.version = version;
	}

	public EventLogger getEventLogger() {
		return (EventLogger) applicationContext.getBean(this.eventLoggerName);
	}
	
	public Map<String,TrajectoryDefinition> getTrajectories(){
		return this.trajectoryDefinitions ;
	}
	
	public void addTrajectory(TrajectoryDefinition trajectoryDefinition){
		String id = trajectoryDefinition.getId();
		if (id == null)
			throw new ConfigurationException(ErrorCodes.MISSING_TRAJECTORY_ID.getSubError(), new Object[] {});
		if(trajectoryDefinitions.containsKey(trajectoryDefinition.getId())){
			TrajectoryDefinition orig = trajectoryDefinitions.get(trajectoryDefinition.getId());
			orig.merge(trajectoryDefinition);
		}else {
			this.trajectoryDefinitions.put(trajectoryDefinition.getId(),trajectoryDefinition) ;
		}
		// update the ChenileServiceDefinition with the trajectory overrides as well
		// this allows us to navigate from service definition to trajectory definition directly
		for (TrajectoryOverride to : trajectoryDefinition.getTrajectoryOverrides().values()) {
			ChenileServiceDefinition csd = getServices().get(to.getServiceId());
			if (csd == null) continue;
			csd.getTrajectoryOverrides().put(to.getTrajectoryId(), to);
		}
	}
}
