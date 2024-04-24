package org.chenile.mqtt.pubsub;

import org.eclipse.paho.mqttv5.client.IMqttToken;
import org.eclipse.paho.mqttv5.client.MqttAsyncClient;
import org.eclipse.paho.mqttv5.common.MqttException;
import org.eclipse.paho.mqttv5.common.MqttMessage;
import org.eclipse.paho.mqttv5.common.MqttPersistenceException;
import org.eclipse.paho.mqttv5.common.packet.MqttProperties;
import org.eclipse.paho.mqttv5.common.packet.UserProperty;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Used to publish MQTT messages. Defaults are picked up from application properties.
 */
public class MqttPublisher {
    @Autowired private MqttAsyncClient v5Client;
    public void setActionTimeout(int actionTimeout) {
        this.actionTimeout = actionTimeout;
    }
    private int actionTimeout = 120;

    public void setQos(int qos) {
        this.qos = qos;
    }

    private int qos = 2;

    public void setRetain(boolean retain) {
        this.retain = retain;
    }

    private boolean retain = true;

    public void publish(String topic, String payload, Map<String,Object> properties)
            throws MqttPersistenceException, MqttException {
        MqttMessage v5Message = new MqttMessage(payload.getBytes());
        MqttProperties props = new MqttProperties();
        List<UserProperty> userProperties = new ArrayList<>();
        for (String key: properties.keySet()){
            userProperties.add(new UserProperty(key,properties.get(key).toString()));
        }
        props.setUserProperties(userProperties);
        v5Message.setProperties(props);
        v5Message.setQos(qos);
        v5Message.setRetained(retain);
        IMqttToken deliveryToken = v5Client.publish(topic, v5Message);
        deliveryToken.waitForCompletion(actionTimeout);
    }
}
