package org.chenile.core.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.chenile.core.context.ChenileExchange;
import org.chenile.core.service.HealthChecker;
import org.chenile.owiz.Command;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class ChenileServiceDefinition  { 
	@JsonIgnore
	private Object serviceReference;
	private String name;
	private String mockName;
	@JsonIgnore
	private Object mockServiceReference;
	private String healthCheckerName;
	@JsonIgnore
	private HealthChecker healthChecker;
	

	private String id;
    private String moduleName;
    private String version;
    private List<OperationDefinition> operations;
    private List<String> interceptorComponentNames;
    @JsonIgnore
    private List<Command<ChenileExchange>> interceptorCommands = new ArrayList<Command<ChenileExchange>>();
    private List<String> clientInterceptorComponentNames;
    @JsonIgnore
    private List<Command<ChenileExchange>> clientInterceptorCommands = new ArrayList<Command<ChenileExchange>>();
	protected Command<ChenileExchange> bodyTypeSelector;
	protected String bodyTypeSelectorComponentName;
	private Map<String,Object> extensions = new HashMap<>();
	// a trajectory ID  to TrajectoryOverride mapping if this service is over-ridden for a particular trajectory
	// the entry does not exist for trajectories that are not over-riding this particular service
	private Map<String,TrajectoryOverride> trajectoryOverrides = new HashMap<>();
	

	public String getBodyTypeSelectorComponentName() {
		return bodyTypeSelectorComponentName;
	}
	
	public Object getMockServiceReference() {
		return mockServiceReference;
	}

	public void setBodyTypeSelectorComponentName(String bodyTypeSelectorComponentName) {
		this.bodyTypeSelectorComponentName = bodyTypeSelectorComponentName;
	}

	public void setBodyTypeSelector(Command<ChenileExchange> bodyTypeSelector) {
		this.bodyTypeSelector = bodyTypeSelector;
	}

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<OperationDefinition> getOperations() {
        return operations;
    }

    public void setOperations(List<OperationDefinition> operations) {
        this.operations = operations;
        // set this service name into every one of the operation definition
        for (OperationDefinition od: operations) {
        	od.setServiceName(name);
        	
        }
    }

	public List<Command<ChenileExchange>> getInterceptorCommands() {
		return this.interceptorCommands;
	}

	public List<String> getInterceptorComponentNames() {
		return interceptorComponentNames;
	}

	public void setInterceptorComponentNames(List<String> interceptorComponentNames) {
		this.interceptorComponentNames = interceptorComponentNames;
	}

	public void setInterceptorCommands(List<Command<ChenileExchange>> interceptorCommands) {
		this.interceptorCommands = interceptorCommands;
	}
	@Override
	public String toString() {
		return "ChenileServiceDefinition [name=" + name + ", id=" + id + "]";
	}

	public Object getServiceReference() {
		return serviceReference;
	}

	public void setServiceReference(Object serviceReference) {
		this.serviceReference = serviceReference;
	}

	public Command<ChenileExchange> getBodyTypeSelector() {
		return bodyTypeSelector;
	}

	public String getModuleName() {
		return moduleName;
	}

	public void setModuleName(String moduleName) {
		this.moduleName = moduleName;
	}

	public List<String> getClientInterceptorComponentNames() {
		return clientInterceptorComponentNames;
	}

	public void setClientInterceptorComponentNames(List<String> clientInterceptorComponentNames) {
		this.clientInterceptorComponentNames = clientInterceptorComponentNames;
	}

	public List<Command<ChenileExchange>> getClientInterceptorCommands() {
		return clientInterceptorCommands;
	}

	public void setClientInterceptorCommands(List<Command<ChenileExchange>> clientInterceptorCommands) {
		this.clientInterceptorCommands = clientInterceptorCommands;
	}

	public String getMockName() {
		return this.mockName;
	}
	
	public void setMockName(String name) {
		this.mockName = name;
	}

	public void setMockServiceReference(Object bean) {
		this.mockServiceReference = bean;		
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public HealthChecker getHealthChecker() {
		return this.healthChecker;
	}
	
	public void  setHealthChecker(HealthChecker healthChecker) {
		this.healthChecker = healthChecker;
	}

	public String getHealthCheckerName() {
		return healthCheckerName;
	}

	public void setHealthCheckerName(String healthCheckerName) {
		this.healthCheckerName = healthCheckerName;
	}

	public void putExtension(String key, Object value) {
		extensions.put(key,value);		
	}
	
	public Object getExtension(String key) {
		return extensions.get(key);		
	}
	
	public Map<String,Object> getExtensions() {
		return this.extensions;
	}

	public Map<String,TrajectoryOverride> getTrajectoryOverrides() {
		return trajectoryOverrides;
	}

	public void setTrajectoryOverrides(Map<String,TrajectoryOverride> trajectoryOverrides) {
		this.trajectoryOverrides = trajectoryOverrides;
	}

	
}