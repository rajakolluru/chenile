package org.chenile.core.model;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.chenile.core.context.ChenileExchange;
import org.chenile.core.context.ContextContainer;
import org.chenile.owiz.Command;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * Contains details about the particular operation of a service. Operations are methods in a service
 */
public class OperationDefinition {
	
	private HttpStatus successHttpStatus = HttpStatus.OK;
	private HttpStatus warningHttpStatus = HttpStatus.OK;
	/**
	 * Operation Name needs to be unique within a service. The name of the method is specified in 
	 * {@link #methodName} below.
	 * We would default methodName to name to begin with. But it can be over-ridden. By keeping name and methodName
	 * separate, we would support the invocation of overloaded methods as well within the service.
	 */
    protected String name;
    /**
     * The actual method in the service that needs to be invoked. 
     */
    protected String methodName;
    /**
     * If this service needs to be cached then this is the cache settings ID
     */
    protected String cacheId;
	public String getCacheId() {
		return cacheId;
	}

	public void setCacheId(String cacheId) {
		this.cacheId = cacheId;
	}

	protected String description;
    protected String componentName;
    protected HTTPMethod httpMethod;
    /**
     * The URL to which this operation listens. It is handled at the level of chenile-http
     */
    protected String url;
    /**
     * Attach this operation to a file watch. The file watch can be associated with file watchers
     * in a designated folder. Whenever a file appears in the folder, it is automatically processed
     * line by line. Each line is then translated into headers and body. The operation is invoked
     * for each line.
     */
    protected String fileWatchId;
    
	private List<ParamDefinition> params = new ArrayList<>();
    /**
     * The service to which this operation is attached. This would be set at the time this operation is attached to a service
     * by {@link ChenileServiceDefinition#setOperations(List)}
     */
    protected String serviceName;
    private boolean auditable = false;
    /**
     * Acls of the incoming request. These are expected to be handled by a security interceptor and will not be directly
     * handled by Chenile itself
     */
    private String[] acls;
    /**
     * The input class expected within the body of the incoming ChenileExchange
     */
    private Class<?> input = String.class;
    /**
     * The output class expected to be passed to the actual service class that will be invoked.
     */
    private Class<?> output;
    
    /**
     * The event to which this operation subscribes. Whenever the event occurs, this operation will be
     * automatically invoked by Chenile. Event must be a valid event in the event registry
     */
    private Set<String> eventSubscribedTo;
    /**
     * This operation is expected to process this event within the time out period. In case, it does not process it
     * then it would be timed out and considered an error.
     */
    private int timeOutInMilliSeconds;
    /**
     * What is the mime type of the output produced?
     * For all text based mime types it is assumed that the response object will contain the
     * information that needs to be rendered.
     * For non text mime types, a file needs to be returned by the method. This file will be
     * returned as an inline file
     */
    protected MimeType produces = MimeType.JSON;
    /**
     * The incoming mime type determines the format of the input class. If it is a {@link String} then this determines what 
     * kind of string it is. (Example: JSON / XML / CSV etc.) This will enable Chenile to use the correct transformer to transform input 
     * to output
     */
    protected MimeType consumes;
    
    protected boolean secure = true;
    @JsonIgnore
	protected List<Command<ChenileExchange>> interceptorCommands;
	protected List<String> interceptorComponentNames;
	protected Map<String,Object> extensions = new HashMap<>();
	protected Map<Class<? extends Annotation>, Annotation> extensionsAsAnnotation = new HashMap<>();
	
	public List<Command<ChenileExchange>> getClientInterceptorCommands() {
		return clientInterceptorCommands;
	}

	public void setClientInterceptorCommands(List<Command<ChenileExchange>> clientInterceptorCommands) {
		this.clientInterceptorCommands = clientInterceptorCommands;
	}

	public List<String> getClientInterceptorComponentNames() {
		return clientInterceptorComponentNames;
	}

	public void setClientInterceptorComponentNames(List<String> clientInterceptorComponentNames) {
		this.clientInterceptorComponentNames = clientInterceptorComponentNames;
	}

	@JsonIgnore
	protected List<Command<ChenileExchange>> clientInterceptorCommands;
	protected List<String> clientInterceptorComponentNames;
	@JsonIgnore
	protected Method method;
	protected ParameterizedTypeReference<?> outputAsParameterizedReference;
	/**
	 * Sometimes it is possible that the output is not specifiable since it can vary depending on the value of some headers. In that case,
	 * this class would be invoked to select the correct body type (i.e. output) class. 
	 */
	protected Command<ChenileExchange> bodyTypeSelector;
	protected String[] bodyTypeSelectorComponentNames;
	
	public String[] getBodyTypeSelectorComponentNames() {
		return bodyTypeSelectorComponentNames;
	}

	public void setBodyTypeSelectorComponentNames(String[] componentName) {
		this.bodyTypeSelectorComponentNames = componentName;
	}
	
	public void setBodyTypeSelector(Command<ChenileExchange> command) {
		this.bodyTypeSelector = command;
	}

    public Method getMethod() {
		return method;
	}

	public void setMethod(Method method) {
		this.method = method;
	}

	public List<String> getInterceptorComponentNames() {
		return interceptorComponentNames;
	}

	public void setInterceptorComponentNames(List<String> interceptorComponentNames) {
		this.interceptorComponentNames = interceptorComponentNames;
	}

	public boolean isSecure() {
        return secure;
    }

    public void setSecure(boolean secure) {
        this.secure = secure;
    }

    public MimeType getProduces() {
        return produces;
    }

    public void setProduces(MimeType produces) {
        this.produces = produces;
    }

    public MimeType getConsumes() {
        return consumes;
    }

    public void setConsumes(MimeType consumes) {
        this.consumes = consumes;
    }

    public HTTPMethod getHttpMethod() {
        return httpMethod;
    }

    public void setHttpMethod(HTTPMethod httpMethod) {
        this.httpMethod = httpMethod;
    }

    public boolean isAuditable() {
        return auditable;
    }

    public void setAuditable(boolean auditable) {
        this.auditable = auditable;
    }

    public String[] getAcls() {
        return acls;
    }

    public void setAcls(String[] acls) {
        this.acls = acls;
    }

    public Class<?> getInput() {
        return input;
    }

    public void setInput(Class<?> input) {
        this.input = input;
    }

    public Class<?> getOutput() {
        return output;
    }

    public void setOutput(Class<?> output) {
        this.output = output;

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
        if(methodName == null)
        	this.methodName = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getComponentName() {
        return componentName;
    }

    public void setComponentName(String componentName) {
        this.componentName = componentName;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public List<ParamDefinition> getParams() {
        return params;
    }

    public void setParams(List<ParamDefinition> params) {
        this.params = params;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

	public List<Command<ChenileExchange>> getInterceptorCommands() {
		return this.interceptorCommands;
	}
	
	public void setInterceptorCommands(List<Command<ChenileExchange>> commands) {
		this.interceptorCommands = commands;
	}

	public Command<ChenileExchange> getBodyTypeSelector() {
		return bodyTypeSelector;
	}
	
	public String getMethodName() {
		return methodName;
	}

	public void setMethodName(String methodName) {
		this.methodName = methodName;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		OperationDefinition other = (OperationDefinition) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}

	public Set<String> getEventSubscribedTo() {
		return eventSubscribedTo;
	}

	public void setEventSubscribedTo(Set<String> eventSubscribedTo) {
		this.eventSubscribedTo = eventSubscribedTo;
	}

	public int getTimeOutInMilliSeconds() {
		return timeOutInMilliSeconds;
	}

	public void setTimeOutInMilliSeconds(int timeOutInMilliSeconds) {
		this.timeOutInMilliSeconds = timeOutInMilliSeconds;
	}
	
	public String getFileWatchId() {
		return fileWatchId;
	}

	public void setFileWatchId(String fileWatchId) {
		this.fileWatchId = fileWatchId;
	}

	public int getSuccessHttpStatus() {
		return successHttpStatus.value();
	}

	public void setSuccessHttpStatus(int successHttpStatus) {
		this.successHttpStatus = HttpStatus.valueOf(successHttpStatus);
	}

	public int getWarningHttpStatus() {
		return warningHttpStatus.value();
	}

	public void setWarningHttpStatus(int warningHttpStatus) {
		this.warningHttpStatus = HttpStatus.valueOf(warningHttpStatus);
	}
	@Deprecated
	public Object getExtension(String key) {
		return extensions.get(key);
	}
	
	public void putExtension(String key, Object value) {
		extensions.put(key, value);
	}
	@Deprecated
	public Map<String,Object> getExtensions() {
		return this.extensions;
	}

    public ParameterizedTypeReference<?> getOutputAsParameterizedReference() {
		return this.outputAsParameterizedReference;
    }
	public void setOutputAsParameterizedReference(ParameterizedTypeReference<?> ref) {
		this.outputAsParameterizedReference = ref;
	}
	public void putExtensionAsAnnotation(Class<? extends Annotation> klass, Annotation annotation) {
		extensionsAsAnnotation.put(klass,annotation);
	}

	@SuppressWarnings("unchecked")
	public <T extends Annotation> T getExtensionAsAnnotation(Class<T> klass) {
		return (T)extensionsAsAnnotation.get(klass);
	}

}
