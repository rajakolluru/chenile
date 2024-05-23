package org.chenile.mqtt.entry;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.chenile.base.exception.ServerException;
import org.chenile.core.context.ChenileExchange;
import org.chenile.core.context.HeaderUtils;
import org.chenile.core.entrypoint.ChenileEntryPoint;
import org.chenile.core.model.ChenileConfiguration;
import org.chenile.core.model.ChenileServiceDefinition;
import org.chenile.core.model.OperationDefinition;
import org.chenile.mqtt.Constants;
import org.chenile.mqtt.errorcodes.ErrorCodes;
import org.eclipse.paho.mqttv5.common.MqttMessage;
import org.eclipse.paho.mqttv5.common.packet.MqttProperties;
import org.eclipse.paho.mqttv5.common.packet.UserProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import static org.chenile.mqtt.errorcodes.ErrorCodes.*;

import java.util.List;
import java.util.Map;

/**
 * The entry point that is invoked by the Mqtt subscriber when a message arrives at a topic.
 * This translates the message into a service invocation.
 * The return value from the service is either published back to a reply queue (if one is specified)
 * or just discarded if none is specified. However, errors will be logged.
 * @author Raja Shankar Kolluru
 *
 */
public class MqttEntryPoint {
	private static final Logger logger = LoggerFactory.getLogger(MqttEntryPoint.class);
	@Autowired
	private ChenileConfiguration chenileConfiguration;
	@Autowired @Qualifier("mqttConfig")
	Map<String,String> mqttConfig;
	@Autowired
	ChenileEntryPoint chenileEntryPoint;
	private final ObjectMapper objectMapper = new ObjectMapper();

	/**
	 * The entry point for MQ-TT. It puts the message into the system and extracts the response which is logged.
	 * We support asynchronous messages at this point in time.
	 * @param topic the topic where the message was received
	 * @param message the message that was received
	 * @throws Exception if there is a problem in processing the message
	 */
	 public void process(String topic, MqttMessage message) throws Exception{
		 ChenileExchange exchange = makeExchange(topic);
		 String messageContent = new String(message.getPayload());
		 exchange.setHeader(HeaderUtils.ENTRY_POINT, Constants.MQTT_ENTRY_POINT);
		 exchange.setBody(messageContent);
		 populateHeaders(message,exchange);
		 chenileEntryPoint.execute(exchange);
		 Object response = exchange.getResponse();
		 logger.info("Received message " + messageContent + " and handled it. Response = "
					 + objectMapper.writeValueAsString(response));

	}

	private void populateHeaders(MqttMessage message, ChenileExchange exchange){
		MqttProperties props = message.getProperties();
		if (props != null ) {
			if (props.getUserProperties() != null) {
				for (UserProperty prop : props.getUserProperties()) {
					exchange.setHeader(prop.getKey(),prop.getValue());
				}
			}
		}
	}

	/**
	 * topic will be in the format /some/stuff/serviceName/operationName
	 * extract the service name and operation name from the topic
	 * @param topic the topic that received this message. Used to compute service and op name
	 * @return the exchange
	 */
	private ChenileExchange makeExchange(String topic) {
		ChenileExchange exchange = new ChenileExchange();
		 int index = topic.lastIndexOf("/");
		 if (index == -1){
			 throw new ServerException(UNSUPPORTED_TOPIC_FORMAT_FOR_OPERATION.getSubError(),
					 new Object[]{topic});
		 }
		 String opName = topic.substring(index+1);
		 String t = topic.substring(0,index);
		 index = t.lastIndexOf("/");
		if (index == -1){
			throw new ServerException(UNSUPPORTED_TOPIC_FORMAT_FOR_SERVICE.getSubError(),
					new Object[]{topic });
		}

		String serviceId = t.substring(index+1);

		ChenileServiceDefinition serviceDefinition = chenileConfiguration.getServices().get(serviceId);
		 if (serviceDefinition == null){
			 throw new ServerException(MISSING_SERVICE.getSubError(), new Object[] { topic, serviceId});
		 }
		 exchange.setServiceDefinition(serviceDefinition);
		List<OperationDefinition> operations = serviceDefinition.getOperations();
	    for(OperationDefinition od: operations){
			if (od.getName().equals(opName)) {
				exchange.setOperationDefinition(od);
				return exchange;
			}
		}
		throw new ServerException(MISSING_SERVICE_OPERATION.getSubError(),
				new Object[]{ topic, serviceId, opName});
	}
}