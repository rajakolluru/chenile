package org.chenile.mqtt.entry;

import org.chenile.base.exception.ServerException;
import org.chenile.core.context.ChenileExchange;
import org.chenile.core.entrypoint.ChenileEntryPoint;
import org.chenile.core.model.ChenileConfiguration;
import org.chenile.core.model.ChenileServiceDefinition;
import org.chenile.core.model.OperationDefinition;
import org.eclipse.paho.mqttv5.common.MqttMessage;
import org.eclipse.paho.mqttv5.common.packet.MqttProperties;
import org.eclipse.paho.mqttv5.common.packet.UserProperty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

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
	@Autowired
	private ChenileConfiguration chenileConfiguration;
	@Autowired @Qualifier("mqttConfig")
	Map<String,String> mqttConfig;
	@Autowired
	ChenileEntryPoint chenileEntryPoint;


	 public void process(String topic, MqttMessage message) throws Exception{
		 ChenileExchange exchange = makeExchange(topic);
		 String messageContent = new String(message.getPayload());
		 exchange.setBody(messageContent);
		 populateHeaders(message,exchange);
		 chenileEntryPoint.execute(exchange);
		 System.out.println("Received message " + messageContent + " and handled it. Response = "
		 + exchange.getResponse());
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
	 * @param topic
	 * @return
	 */
	private ChenileExchange makeExchange(String topic) {
		ChenileExchange exchange = new ChenileExchange();
		 int index = topic.lastIndexOf("/");
		 if (index == -1){
			 throw new ServerException(901,new Object[]{topic});
		 }
		 String opName = topic.substring(index+1);
		 String serviceTopic = topic.substring(0,index);

		 String serviceId = mqttConfig.get(serviceTopic);
		 if (serviceId == null){
			 throw new ServerException(902,new Object[]{topic, serviceTopic });
		 }
		ChenileServiceDefinition serviceDefinition = chenileConfiguration.getServices().get(serviceId);
		 if (serviceDefinition == null){
			 throw new ServerException(903, new Object[] { topic, serviceId});
		 }
		 exchange.setServiceDefinition(serviceDefinition);
		List<OperationDefinition> operations = serviceDefinition.getOperations();
	    for(OperationDefinition od: operations){
			if (od.getName().equals(opName)) {
				exchange.setOperationDefinition(od);
				return exchange;
			}
		}
		throw new ServerException(904, new Object[]{ topic, serviceId, opName});
	}
}