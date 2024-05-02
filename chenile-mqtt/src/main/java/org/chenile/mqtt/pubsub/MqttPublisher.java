package org.chenile.mqtt.pubsub;

import org.chenile.base.exception.ServerException;
import org.chenile.mqtt.MqttInfoProvider;
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
import java.util.List;
import java.util.Map;

/**
 * Used to publish MQTT messages. Defaults are picked up from application properties.
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

    public void publishToOperation(String service, String operationName,String payload,Map<String,Object> properties)
     throws Exception {
        ChenileMqtt m = mqttInfoProvider.obtainChenileMqtt(service);
        if (m == null) {
            throw new ServerException(905, new Object[] { service});
        }
        String topic = m.topic();
        topic = topic + "/" + operationName;
        int qos = m.qos();
        logger.info("Publishing message " + payload + " to " + topic + " with qos = "  + qos);
        publish(topic,qos,payload,properties);
    }
    public void publish(String topic,  String payload, Map<String,Object> properties)
            throws MqttPersistenceException, MqttException {
        publish (topic,-1,payload,properties);
    }
    public void publish(String topic, int givenQos, String payload, Map<String,Object> properties)
            throws MqttPersistenceException, MqttException {
        MqttMessage v5Message = new MqttMessage(payload.getBytes());
        MqttProperties props = new MqttProperties();
        List<UserProperty> userProperties = new ArrayList<>();
        for (String key: properties.keySet()){
            userProperties.add(new UserProperty(key,properties.get(key).toString()));
        }
        props.setUserProperties(userProperties);
        v5Message.setProperties(props);
        if (givenQos == -1)
            givenQos = this.qos;
        v5Message.setQos(givenQos);
        v5Message.setRetained(retain);
        IMqttToken deliveryToken = v5Client.publish(topic, v5Message);
        deliveryToken.waitForCompletion(actionTimeout);
    }

    public void sendAck(MqttMessage message) throws Exception{
        logger.info("Sending an ack for message ID = " + message.getId() + " for qos = " + qos);
        v5Client.messageArrivedComplete(message.getId(), qos);
    }
}
