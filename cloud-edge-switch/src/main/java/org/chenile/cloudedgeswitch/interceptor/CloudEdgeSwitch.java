package org.chenile.cloudedgeswitch.interceptor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import org.chenile.base.exception.ErrorNumException;
import org.chenile.base.exception.ServerException;
import org.chenile.base.response.ErrorType;
import org.chenile.base.response.ResponseMessage;
import org.chenile.cloudedgeswitch.CloudEdgeSwitchConfig;
import org.chenile.core.context.ChenileExchange;
import org.chenile.core.context.HeaderUtils;
import org.chenile.core.interceptors.BaseChenileInterceptor;
import org.chenile.core.model.HttpBindingType;
import org.chenile.core.model.OperationDefinition;
import org.chenile.core.model.ParamDefinition;
import org.chenile.core.util.MethodUtils;
import org.chenile.mqtt.Constants;
import org.chenile.mqtt.pubsub.MqttPublisher;
import org.chenile.owiz.impl.ChainContext;
import org.chenile.proxy.builder.ProxyBuilder;
import org.chenile.proxy.errorcodes.ErrorCodes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * <p>This interceptor, if configured, acts like an edge switch for a service i.e. it turns a service from a
 * cloud service into an edge service.</p>
 * <p>The edge service logic is as follows: <br/>
 * If the cloud service is detected and can be successfully invoked, then the edge service is a "forward only"
 * service. Else it is a "store and forward service" </p>
 * <p>This switch looks for a property called remoteUrl. If the remote Url is not configured then it assumes that
 * the service is running in the cloud. For the cloud it merely passes control to the service and publishes a message
 * upon successful execution so that all edges can update themselves</p>
 * <p>If the remoteUrl is configured then it switches the functionality to the store-forward or forward-store modes
 * specified above.</p>
 * <p>The local service call is bypassed completely at the edge. The Switch attempts to call the same service but
 * hosted in the cloud. The remote url is the cloud location of the service. If the cloud call is successful, then this
 * effectively acts as if the edge service is a traffic cop merely directing all the incoming traffic to the cloud
 * and returning the result of the cloud service call. This is the 'forward only' functionality</p>
 * <p>If the call fails then the "store and forward" part of the logic kicks in. In this case the local service is
 * called. The result of the local service call is returned with a warning stating that the transaction succeeded
 * but it is done locally due to network failure.</p>
 */
public class CloudEdgeSwitch extends BaseChenileInterceptor {
	Logger logger = LoggerFactory.getLogger(CloudEdgeSwitch.class);
	private String remoteUrl;
	@Autowired private MqttPublisher mqttPublisher;
	@Autowired private ProxyBuilder proxyBuilder;
	public void setRemoteUrl(String remoteUrl){
		this.remoteUrl = remoteUrl;
	}
	@Override
	public void execute(ChenileExchange exchange) throws Exception {
		// Bypass this interceptor?
		if (bypassInterception(exchange)) {
			super.doContinue(exchange);
			return;
		}
		if (remoteUrl == null || remoteUrl.isEmpty()) {
			handleCloud(exchange);
			return;
		}
		handleEdge(exchange);
	}

	/**
	 * In the edge, we will delegate the control to the cloud.
	 * @param exchange the chenile exchange
	 *
	 */
	private void handleEdge(ChenileExchange exchange) throws Exception{
		String initialBody = toJson(exchange.getBody());
		// First save your position
		Object serviceReference = exchange.getServiceReference();
		ChainContext.SavePoint savePoint = savePoint(exchange);

		if (callCloud(exchange,initialBody)) return ;
		// Cloud call failed due to connectivity issues.
		// call local service and then move on. Save the connectivity exception.
		ErrorNumException exception = exchange.getException();
		if(!callEdge(exchange,serviceReference,savePoint,initialBody)){
			return ; // call failed. So the correct message will already be there in
			// ChenileExchange. just return without propagating this elsewhere
		}
		// edge call is successful, so give out warnings to indicate that invocation
		// though successful, happened locally only.
		enhanceWarnings(exception,exchange);
		// finally publish the message so the cloud gets it when connectivity is re-established
		publishMessage(exchange,initialBody);
	}

	private boolean callEdge(ChenileExchange exchange, Object serviceReference,
							 ChainContext.SavePoint savePoint,String initialBody){
		// Switch back to the service reference and repeat it for local storage
		switchServiceReference(serviceReference, exchange);
		exchange.setException(null);
		exchange.setResponse(null);
		try {
			resumeFromSavedPoint(savePoint, exchange);
		}catch(Exception e){
			exchange.setException(new ServerException(8002,new Object[]{e.getMessage()},e));
		}
        return exchange.getException() == null;
    }


	/**
	 * Switch to cloud proxy and invoke it so that invocation happens via http in the cloud
	 * @param exchange - the chenile exchange
	 * @return true if cloud invocation could be done. false if connectivity is a problem
	 */
	private boolean callCloud(ChenileExchange exchange, String initialBody){
		switchServiceReference(getRemoteProxy(exchange), exchange);
		try {
			super.doContinue(exchange);
			if (exchange.getException() != null){
				ErrorNumException e = exchange.getException();
                return e.getSubErrorNum() != ErrorCodes.CANNOT_CONNECT.getSubError();
			}
		}catch(Exception e){
			ServerException exception = new ServerException(8002,new Object[]{e.getMessage()}, e);
			exchange.setException(exception);
			return false;
		}
		// finally publish the message
		publishMessage(exchange,initialBody);
		return true;
	}

	/**
	 * In the cloud, execute the actual service
	 * Upon successful return, send a message to MQTT topic
	 * @param exchange the Chenile exchange
	 */
	private void handleCloud(ChenileExchange exchange) throws Exception{
		String initialBody = toJson(exchange.getBody());
		doContinue(exchange);
		if (exchange.getException() != null) return;
		publishMessage(exchange,initialBody);
	}

	private void publishMessage(ChenileExchange exchange, String s){
		try {
			logger.debug("publishing message = " + s);
			Map<String,Object> headers = new HashMap<>();
			OperationDefinition od = exchange.getOperationDefinition();
			for (ParamDefinition pd: od.getParams()){
				if(pd.getType().equals(HttpBindingType.HEADER)) {
					headers.put(pd.getName(), exchange.getHeader(pd.getName()));
				}
			}
			logger.debug("publishing message = " + s + " with headers " +
					headers);
			mqttPublisher.publishToOperation(exchange.getServiceDefinition().getId(),
					exchange.getOperationDefinition().getName(),s, headers);
		}catch(Exception e){
			logger.info("Unable to send a message. Error = " +e.getMessage());
		}
	}
	ObjectWriter objectWriter = new ObjectMapper().writer().withDefaultPrettyPrinter();
	private String toJson(Object payload) throws Exception{
		if (payload == null) return null;
		return objectWriter.writeValueAsString(payload);
	}

	private void enhanceWarnings(ErrorNumException exception, ChenileExchange exchange){
		ResponseMessage responseMessage = new ResponseMessage();
		responseMessage.setCode(HttpStatus.ACCEPTED);
		responseMessage.setSubErrorCode(8000);
		responseMessage.setDescription(null);
		responseMessage.setSeverity(ErrorType.WARN);
		exchange.addWarningMessage(responseMessage);
		responseMessage = new ResponseMessage();
		responseMessage.setCode(exception.getErrorNum());
		responseMessage.setSubErrorCode(8001);
		responseMessage.setDescription(null);
		responseMessage.setParams(new Object[] {exception.getMessage()});
		responseMessage.setSeverity(ErrorType.WARN);
		exchange.addWarningMessage(responseMessage);
	}

	private void switchServiceReference(Object ref, ChenileExchange exchange){
		exchange.setServiceReference(ref);
		Method method = MethodUtils.computeMethod(ref.getClass(), exchange.getOperationDefinition());
		exchange.setMethod(method);
	}

	private Object getRemoteProxy(ChenileExchange exchange){
		// make sure that you pass the ProxyMode as REMOTE.
		// This ensures that remote proxy is always returned. Otherwise the default would be LOCAL since
		// this service is running locally.
		Class<?> interfaceClass = exchange.getServiceDefinition().getInterfaceClass();
		return proxyBuilder.buildProxy(interfaceClass,exchange.getServiceDefinition().getId(),
				null,ProxyBuilder.ProxyMode.REMOTE,remoteUrl);
	}

	// Bypass the interceptor if there exists no configuration or if the request is from MQTT entry point
	@Override
	protected boolean bypassInterception(ChenileExchange exchange) {
		// bypass all MQTT requests
		String entryPoint = exchange.getHeader(HeaderUtils.ENTRY_POINT,String.class);
		if (entryPoint != null && entryPoint.equals(Constants.MQTT_ENTRY_POINT))
			return true;
		// if this is not configured for this service/operation bypass it
		CloudEdgeSwitchConfig config = getExtensionByAnnotation(CloudEdgeSwitchConfig.class, exchange);
        return config == null;
    }
}
