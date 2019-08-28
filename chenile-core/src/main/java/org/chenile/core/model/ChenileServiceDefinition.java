package org.chenile.core.model;

import java.util.ArrayList;
import java.util.List;

import org.chenile.core.context.ChenileExchange;
import org.chenile.owiz.Command;

public class ChenileServiceDefinition  {  
	private Object serviceReference;
	private String name;
    private String id;
    private String moduleName;
    private List<OperationDefinition> operations;
    private List<String> interceptorComponentNames;
    private List<Command<?>> interceptorCommands = new ArrayList<Command<?>>();
	protected Command<ChenileExchange> bodyTypeSelector;
	protected String bodyTypeSelectorComponentName;

	public String getBodyTypeSelectorComponentName() {
		return bodyTypeSelectorComponentName;
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

	public List<Command<?>> getInterceptorCommands() {
		return this.interceptorCommands;
	}

	public List<String> getInterceptorComponentNames() {
		return interceptorComponentNames;
	}

	public void setInterceptorComponentNames(List<String> interceptorComponentNames) {
		this.interceptorComponentNames = interceptorComponentNames;
	}

	public void setInterceptorCommands(List<Command<?>> interceptorCommands) {
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

}