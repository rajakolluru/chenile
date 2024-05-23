package org.chenile.mqtt.pubsub;

import org.chenile.base.exception.ServerException;
import org.chenile.mqtt.Constants;
import org.chenile.mqtt.MqttInfoProvider;
import org.chenile.mqtt.errorcodes.ErrorCodes;
import org.chenile.mqtt.model.ChenileMqtt;
import org.eclipse.paho.mqttv5.client.IMqttToken;
import org.eclipse.paho.mqttv5.client.MqttAsyncClient;
import org.eclipse.paho.mqttv5.common.MqttException;
import org.eclipse.paho.mqttv5.common.MqttMessage;
import org.eclipse.paho.mqttv5.common.MqttPersistenceException;
import org.eclipse.paho.mqttv5.common.packet.MqttProperties;
import org.eclipse.paho.mqttv5.common.packet.UserProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Used to publish MQTT messages. Defaults are picked up from application properties.
 *
 */
public class MqttPublisher {
    Logger logger = LoggerFactory.getLogger(MqttPublisher.class);
    @Autowired private MqttAsyncClient v5Client;
    public void setActionTimeout(int actionTimeout) {
        this.actionTimeout = actionTimeout;
    }
    private int actionTimeout = 12000;

    public void setQos(int qos) {
        this.qos = qos;
    }

    private int qos = 2;

    public void setRetain(boolean retain) {
        this.retain = retain;
    }
    @Autowired
    MqttInfoProvider mqttInfoProvider;
    private boolean retain = true;

    /**
     * Send a message to a service and operation.
     * Topic and Qos are computed from MQTTConfig for the service and operation name.
     * Topics might have placeholders that will be substituted from the properties.
     * @param service the service that will receive this message
     * @param operationName the operation within the service that will receive this message
     * @param payload the payload that needs to be sent
     * @param properties the headers properties that need to be sent
     * @throws Exception if there is an error in dispatching the message
     */
    public void publishToOperation(String service, String operationName,String payload,Map<String,Object> properties)
     throws Exception {
        ChenileMqtt m = mqttInfoProvider.obtainChenileMqtt(service);
        if (m == null) {
            throw new ServerException(ErrorCodes.CANNOT_FIND_TOPIC.getSubError(),
                    new Object[] { service});
        }
        String topic = substituteProperties(m.publishTopic(),properties);
        topic = topic + "/" + operationName;
        int qos = m.qos();
        publish(topic,qos,payload,properties);
    }

    /**
     * Substitute the property in the place holder.
     * @param s the string with placeholders
     * @param properties the properties that need to be substituted
     * @return the s
     */
    private static String substituteProperties(String s, Map<String,Object> properties){
        for(String prop: properties.keySet()){
            s = s.replaceAll("\\{"+ prop +"}",properties.get(prop).toString());
        }
        return s;
    }

    /**
     * Publish to the topic with the default Qos of the system (as configured in application.yml)
     * @param topic the topic to publish to
     * @param payload the payload
     * @param properties the user properties that need to be put into the message
     * @throws MqttPersistenceException if there is a problem in persisting the message that needs to be sent
     * @throws MqttException if there is any other exception
     */
    public void publish(String topic,  String payload, Map<String,Object> properties)
            throws MqttPersistenceException, MqttException {
        publish (topic,-1,payload,properties);
    }

    /**
     * Publish to the topic with the specified Qos of the system (as configured in application.yml)
     * @param topic the topic to publish to
     * @param payload the payload
     * @param givenQos the Qos that needs to be sent. In case this is -1 then system default will be used.
     * @param properties the user properties that need to be put into the message
     * @throws MqttPersistenceException if there is a problem in persisting the message that needs to be sent
     * @throws MqttException if there is any other exception
     */
    public void publish(String topic, int givenQos, String payload, Map<String,Object> properties)
            throws MqttPersistenceException, MqttException {
        MqttMessage v5Message = new MqttMessage(payload.getBytes());
        MqttProperties props = new MqttProperties();
        List<UserProperty> userProperties = new ArrayList<>();
        for (String key: properties.keySet()){
            userProperties.add(new UserProperty(key,properties.get(key).toString()));
        }
        logger.info("At the publish message sending client ID {} as the source of the message with payload = {} " +
                "and qos = {} to topic {} with properties {}", v5Client.getClientId(), payload, qos, topic, properties);
        // always add the source to every MQTT message.
        userProperties.add(new UserProperty(Constants.SOURCE, v5Client.getClientId()));
        props.setUserProperties(userProperties);
        v5Message.setProperties(props);
        if (givenQos == -1)
            givenQos = this.qos;
        v5Message.setQos(givenQos);
        v5Message.setRetained(retain);
        IMqttToken deliveryToken = v5Client.publish(topic, v5Message);
        deliveryToken.waitForCompletion(actionTimeout);
    }

    /**
     *
     * @param message the message that needs to be acknowledged
     * @throws Exception if an exception is thrown
     */
    public void sendAck(MqttMessage message) throws Exception{
        logger.info("Sending an ack for message ID = {} for qos = {}", message.getId(), qos);
        v5Client.messageArrivedComplete(message.getId(), qos);
    }
}
