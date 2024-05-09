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
 * the service is running in the cloud. For the cloud it merely passes control to the service i.e. does nothing. </p>
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
		if(bypassInterception(exchange)){
			super.doContinue(exchange);
			return;
		}
		// First save your position
		Object serviceReference = exchange.getServiceReference();
		// Save the current save point. We may have to repeat this if the execution fails.
		ChainContext.SavePoint savePoint = savePoint(exchange);

		// Cloud invocation. Switch to cloud proxy and invoke it
		ErrorNumException exception = null;
		switchServiceReference(getRemoteProxy(exchange), exchange);
		try {
			super.doContinue(exchange);
			if (exchange.getException() == null) return;
		}catch(Exception e){
			exception = new ServerException("Error occurred in cloud invocation",e);
		}
		// Implement the store and forward functionality. first save any exceptions
		exception = exchange.getException();
		// Switch back to the service reference and repeat it for local storage
		switchServiceReference(serviceReference, exchange);
		exchange.setException(null);
		exchange.setResponse(null);
		ErrorNumException exception1 = null;
		try {
			resumeFromSavedPoint(savePoint, exchange);
			exception1 = exchange.getException();
		}catch(Exception e){
			exception1 = new ServerException("Error occurred in local invocation",e);
		}

		enhanceWarnings(exception,exchange);
		if (exception1 != null){
			enhanceWarnings(exception1,exchange);
		}
		// finally publish the message
		publishMessage(exchange);
	}

	private void publishMessage(ChenileExchange exchange){
		try {
			String s = toJson(exchange.getBody());
			logger.debug("publishing message = " + s);
			Map<String,Object> headers = new HashMap<>();
			OperationDefinition od = exchange.getOperationDefinition();
			for (ParamDefinition pd: od.getParams()){
				if(pd.getType().equals(HttpBindingType.HEADER)) {
					headers.put(pd.getName(), exchange.getHeader(pd.getName()));
				}
			}
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
		// bypass this if running in the cloud.
		if(remoteUrl == null || remoteUrl.isEmpty()) return true;
		// if this is not configured for this service/operation bypass it
		CloudEdgeSwitchConfig config = getExtensionByAnnotation(CloudEdgeSwitchConfig.class, exchange);
        return config == null;
    }
}
