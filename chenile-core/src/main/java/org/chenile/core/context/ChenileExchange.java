package org.chenile.core.context;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.chenile.core.model.ChenileServiceDefinition;
import org.chenile.core.model.OperationDefinition;
import org.chenile.owiz.impl.ChainContext;
import org.chenile.owiz.impl.ChainContextContainer;

/**
 * <p>A bidirectional exchange that is supposed to navigate between different {@link ChenileInterceptor}s. 
 * The exchange contains a body which is the incoming request along with headers. </p>
 * <p>Commands will set the response in the backward journey.</p>
 * 
 * <p>There can be transformers that convert the body to the type acceptable by the service. </p>
 * 
 * <p>Chenile end point modules such as chenile-http must be able to convert their end point specific incoming 
 * request (such as HttpServletRequest) into ChenileExchange</p>
 * <p>They must also be able to convert the returned response into their protocol specific response object 
 * (such as HttpServletResponse) </p>
 * 
 * @author meratransport
 *
 */
public class ChenileExchange implements Serializable, ChainContextContainer {
	private static final long serialVersionUID = -8886041051244601433L;
	private Map<String, Object> headers = new HashMap<>();
	
	private Object body = null;
	/**
	 * An object internally used by Chenile to invoke the API. 
	 * Touch this at your own risk!
	 */
	private List<Object> apiInvocation;
	/**
	 * ChenileServiceDefinition & OperationDefinition are expected to be set by the protocol specific end point.
	 * Example: in chenile-http the URL called will be mapped to the ChenileServiceDefinition and OperationDefinition.
	 */ 
	private OperationDefinition operationDefinition;
	private ChenileServiceDefinition serviceDefinition;
	
	/**
	 * This is needed by the Chenile Transformation framework for setting the type of the body.
	 * All incoming JSONs (and other type of strings) are converted into the body type specified here
	 */
	private Class<?> bodyType;
	
	private Object response;
	
	/**
	 * Internal field for the purpose of continuing the interceptor chain. The interceptors must not manipulate this.
	 * Instead, they will just call the {@link ChainContext#doContinue()} method if the request needs to proceed beyond.
	 */
	private ChainContext chainContext;
	private boolean localInvocation;
	
	public ChenileExchange() {}
	
	public ChenileExchange(ChenileExchange that) {
		if (that.serviceDefinition !=  null) {
			this.serviceDefinition = that.serviceDefinition;
		}
		if (that.operationDefinition !=  null) {
			this.operationDefinition = that.operationDefinition;
		}
		if (that.bodyType != null)
			this.bodyType = that.bodyType;
		if (that.body != null)
			this.body = that.body;
		if (that.headers != null) {
			this.headers = new HashMap<String, Object>();
			this.headers.putAll(that.headers);
		}
	}

	public OperationDefinition getOperationDefinition() {
		return operationDefinition;
	}

	public void setOperationDefinition(OperationDefinition operationDefinition) {
		this.operationDefinition = operationDefinition;
	}

	public ChenileServiceDefinition getServiceDefinition() {
		return serviceDefinition;
	}

	public void setServiceDefinition(ChenileServiceDefinition serviceDefinition) {
		this.serviceDefinition = serviceDefinition;
	}
	

	public Map<String, Object> getHeaders() {
		return headers;
	}

	@SuppressWarnings("unchecked")
	public <T> T getHeader(String name, Object defaultValue, Class<T> type) {
		Object value = getHeader(name, defaultValue);
		if (value == null) {
			// lets avoid NullPointerException when converting to boolean for
			// null values
			if (boolean.class.isAssignableFrom(type)) {
				return (T) Boolean.FALSE;
			}
			return null;
		}

		return type.cast(value);

	}

	public Object getHeader(String name, Object defaultValue) {
		Object answer = headers.get(name);
		return answer != null ? answer : defaultValue;
	}

	@SuppressWarnings("unchecked")
	public <T> T getHeader(String name, Class<T> type) {
		Object value = getHeader(name);
		if (value == null) {
			// lets avoid NullPointerException when converting to boolean for
			// null values
			if (boolean.class.isAssignableFrom(type)) {
				return (T) Boolean.FALSE;
			}
			return null;
		}

		return type.cast(value);

	}

	public Object getHeader(String name) {
		return headers.get(name);
	}

	public void setHeaders(Map<String, Object> headers) {
		this.headers = headers;
	}

	public void setHeader(String key, Object value) {
		this.headers.put(key, value);
	}

	public Object getBody() {
		return body;
	}

	public void setBody(Object body) {
		this.body = body;
	}
	
	public void setResponse(Object response) {
		this.response = response;
	}
	
	public Object getResponse() {
		return this.response;
	}

	@Override
	public ChainContext getChainContext() {
		return chainContext;
	}

	@Override
	public void setChainContext(ChainContext chainContext) {
		this.chainContext = chainContext;
	}

	public Class<?> getBodyType() {
		return bodyType;
	}

	public void setBodyType(Class<?> bodyType) {
		this.bodyType = bodyType;
	}

	public List<Object> getApiInvocation() {
		return apiInvocation;
	}

	public void setApiInvocation(List<Object> apiInvocation) {
		this.apiInvocation = apiInvocation;
	}

	public void setLocalInvocation(boolean b) {
		this.localInvocation = b;		
	}
	
	public boolean isLocalInvocation() {
		return localInvocation;
	}


}
