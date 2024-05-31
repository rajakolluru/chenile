package org.chenile.core.context;

import java.io.Serial;
import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.*;

import org.chenile.base.exception.ErrorNumException;
import org.chenile.base.exception.ServerException;
import org.chenile.base.response.ResponseMessage;
import org.chenile.base.response.WarningAware;
import org.chenile.core.errorcodes.ErrorCodes;
import org.chenile.core.model.ChenileServiceDefinition;
import org.chenile.core.model.OperationDefinition;
import org.chenile.owiz.impl.ChainContext;
import org.chenile.owiz.impl.ChainContextContainer;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.core.type.TypeReference;

/**
 * A bidirectional exchange that navigates between different Chenile Interceptors.
 * The exchange contains a body which is the incoming request along with headers. </p>
 * <p>Commands will set the response or exception in the return journey.</p>
 * 
 * <p>There can be transformers that convert the body to the type acceptable by the service. </p>
 * 
 * <p>Chenile end point modules such as chenile-http must be able to convert their end point specific incoming 
 * request (such as HttpServletRequest) into ChenileExchange</p>
 * <p>They must also be able to convert the returned response into their protocol specific response object 
 * (such as HttpServletResponse) </p>
 * 
 * @author Raja Shankar Kolluru
 *
 */
public class ChenileExchange implements Serializable, ChainContextContainer<ChenileExchange>,
		WarningAware {
	@Serial
	private static final long serialVersionUID = -8886041051244601433L;
	/**
	 * Incoming request headers. Some headers can be added as we navigate through the interception chain
	 */
	private Map<String, Object> headers = new HashMap<>();
	/**
	 * This is used to handle multipart messages from HTTP.
	 */
	private Map<String, MultipartFile> multiPartMap;
	/**
	 * The converted body of the incoming message. This will be set to the actual payload expected by the service. It
	 * may be a JSON string to begin with before the transformation framework converts it to the desired payload type
	 */
	private Object body = null;
	/**
	 * bodyType is populated by the Chenile Transformation framework. The bodyType might vary depending on the
	 * URL of the incoming request and also some headers. A body type selector is a special
	 * {@link org.chenile.owiz.Command<ChenileExchange>} that determines the body type depending on the
	 * context of the request. (like headers, URL , locale etc.)
	 * All incoming JSONs (and other type of strings) are converted into the body type specified here
	 */
	private TypeReference<?> bodyType;
	/**
	 * An object internally used by {@link org.chenile.core.interceptors.ServiceInvoker} to invoke the API.
	 */
	private List<Object> apiInvocation;
	/**
	 * This is a very important metadata to handle the request. It contains the definition of the Service
	 * and the Operation within it. All operations are performed using these data structures. For example this
	 * metadata will be used to determine the service that needs to be invoked and the method within the
	 * service. This metadata is also used to determine the interceptors that are applicable etc.
	 * ChenileServiceDefinition and OperationDefinition are expected to be set by the protocol specific end point.
	 * Example: in chenile-http the URL called will be mapped to the ChenileServiceDefinition and OperationDefinition.
	 */ 
	private OperationDefinition operationDefinition;
	private ChenileServiceDefinition serviceDefinition;
	

	/**
	 * used (by chenile-proxy) to convert the response JSON to a response body.
	 */
	private ParameterizedTypeReference<?>  responseBodyType;
	/**
	 * The actual response returned by the service or any interceptor. (for example a caching interceptor
	 * might set the response and return)
	 */
	private Object response;
	/**
	 * The exception thrown by the service or any of the interceptors
	 */
	private ErrorNumException exception;
	/**
	 * contains the http response status code
	 */
	private int httpResponseStatusCode;
	/**
	 * contains all errors and warnings
 	 */
	private List<ResponseMessage> responseMessages;
	
	/**
	 * Internal field for the purpose of continuing the interceptor chain. The interceptors can use this
	 * to pass control to the next in the line by calling the {@link ChainContext#doContinue()} method.
	 * Interceptors can also use the {@link ChainContext#savePoint()} to save the current point and
	 * using {@link ChainContext#resumeFromSavedPoint(ChainContext.SavePoint)}
	 * to continue the chain from the saved point. This is useful if the rest of the interceptor chain
	 * needs to be called multiple times as part of some retry logic.
	 */
	private ChainContext<ChenileExchange> chainContext;
	private boolean localInvocation;
	/**
	 * Calculated locale. This should be populated from the incoming request as per the headers passed
	 * to it.
	 */
	private Locale locale;
	/**
	 * This is useful if this request is determined to be a mock request. Hence a mock service
	 * may have to be invoked instead of the actual service. In case of HTTP a special header (x-chenile-mock)
	 * will be passed for mock requests.
	 */
	private boolean invokeMock;
	/**
	 * the service which will be invoked. This would be
	 * populated by the {@link org.chenile.core.interceptors.ConstructServiceReference} interceptor
	 * and invoked by {@link org.chenile.core.interceptors.ServiceInvoker}.
	 * By switching the serviceReference, any interceptor can change the target service that will be
	 * invoked. But please be sure to recompute the method by calling {@link org.chenile.core.util.MethodUtils}
	 */
	private Object serviceReference;
	/**
	 * This field just uses a cached method object for optimization purposes. This needs to be
	 * recalculated if the serviceReference is changed by an interceptor.
	 */
	private Method method;
	/**
	 * the spring bean ID of the service being invoked.
	 */
	private String serviceReferenceId;


	
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
		if (response == null){
			this.response = null;
			this.responseMessages = new ArrayList<>();
			return;
		}
		this.response = response;
		// empty the warnings from the response and copy them into ChenileExchange
		List<ResponseMessage> x = WarningAware.obtainWarnings(response);
		if (x != null && !x.isEmpty()) {
			if (this.responseMessages == null)this.responseMessages = x;
			else this.responseMessages.addAll(x);
		}
		WarningAware.removeAllWarnings(response);
	}
	
	public Object getResponse() {
		return this.response;
	}

	@Override
	public ChainContext<ChenileExchange> getChainContext() {
		return chainContext;
	}

	@Override
	public void setChainContext(ChainContext<ChenileExchange> chainContext) {
		this.chainContext = chainContext;
	}

	public TypeReference<?> getBodyType() {
		return bodyType;
	}

	public void setBodyType(TypeReference<?> bodyType) {
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

	/**
	 * Call this to set exception in the exchange. This method has side effects! It modifies the
	 * {@link #responseMessages} in the exchange. <br/>
	 * If the exception is null then it removes all traces of errors so far! It empties the response messages
	 * in the exchange. <br/>
	 * If the exception is set for the first time, then all the errors in the exception get copied as
	 * response messages in the exchange. <br/>
	 * If the exception is already present, then the exception is not over-ridden. Instead, the response
	 * messages are added from this exception.
	 *
	 * @param e the exception that needs to be set in Chenile Exchange
	 */
	public void setException(Throwable e) {
		if (e == null) {
			this.exception = null;
			this.responseMessages = new ArrayList<>();
			return;
		}
		ErrorNumException ene = createErrorNumExceptionIfRequired(e);
		if (this.responseMessages == null){
			this.responseMessages = new ArrayList<>();
		}
		// use this as the exception only if an existing exception does not exist
		// do not replace the existing exception with this
		if (this.exception == null) this.exception = ene;
		// However, copy the errors from this exception to the current collection of errors
		this.responseMessages.addAll(ene.getErrors());
	}

	private ErrorNumException createErrorNumExceptionIfRequired(Throwable e){
		if ( e instanceof ErrorNumException errorNumException)  return errorNumException;
		return new ServerException(ErrorCodes.SERVICE_EXCEPTION.getSubError(),
				new Object[]{ e.getMessage()},e);
	}
	
	public ErrorNumException getException() {
		return this.exception;
	}

	public Locale getLocale() {
		if (this.locale == null) return Locale.US;
		return this.locale;
	}

	public void setLocale(Locale locale) {
		this.locale = locale;		
	}

	public boolean isInvokeMock() {
		return this.invokeMock;
	}
	public void setInvokeMock(boolean invokeMock) {
		this.invokeMock = invokeMock;
	}

	public Object getServiceReference() {
		return serviceReference;
	}

	public void setServiceReference(Object serviceReference) {
		this.serviceReference = serviceReference;
	}

	public String getServiceReferenceId() {
		return serviceReferenceId;
	}

	public void setServiceReferenceId(String serviceReferenceId) {
		this.serviceReferenceId = serviceReferenceId;
	}

	public void setMultiPartMap(Map<String,MultipartFile>  multiPartMap) {
		this.multiPartMap = multiPartMap;		
	}
	
	public Map<String,MultipartFile> getMultiPartMap() {
		return this.multiPartMap;		
	}

	public ParameterizedTypeReference<?> getResponseBodyType() {
		return responseBodyType;
	}

	public void setResponseBodyType(ParameterizedTypeReference<?> responseBodyType) {
		this.responseBodyType = responseBodyType;
	}
	
	public Method getMethod() {
		return this.method;
	}
		
	public void setMethod(Method method) {
		this.method = method;
	}

	public int getHttpResponseStatusCode() {
		return httpResponseStatusCode;
	}

	public void setHttpResponseStatusCode(int httpResponseStatusCode) {
		this.httpResponseStatusCode = httpResponseStatusCode;
	}

	public List<ResponseMessage> getResponseMessages() {
		return responseMessages;
	}

	public void setResponseMessages(List<ResponseMessage> responseMessages) {
		this.responseMessages = responseMessages;
	}

	@Override
	public List<ResponseMessage> getWarningMessages() {
		return responseMessages;
	}

	@Override
	public void addWarningMessage(ResponseMessage m) {
		if (responseMessages == null){
			responseMessages = new ArrayList<>();
		}
		responseMessages.add(m);
	}

	@Override
	public void removeAllWarnings() {
		responseMessages = new ArrayList<>();
	}
	public <T extends Annotation> T getExtensionByAnnotation(Class<T> klass) {
		T ret = this.getOperationDefinition().getExtensionAsAnnotation(klass);
		if (ret == null) {
			ret = this.getServiceDefinition().getExtensionAsAnnotation(klass);
		}
		return ret;
	}
}
