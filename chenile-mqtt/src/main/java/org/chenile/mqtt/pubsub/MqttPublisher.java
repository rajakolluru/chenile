package org.chenile.mqtt.pubsub;

import org.chenile.base.exception.ServerException;
import org.chenile.core.model.ChenileConfiguration;
import org.chenile.core.model.ChenileServiceDefinition;
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
    @Autowired private ChenileConfiguration chenileConfiguration;
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

    private boolean retain = true;
    @SuppressWarnings("unchecked")
    public void publishToOperation(String service, String operationName,String payload,Map<String,Object> properties)
     throws Exception{
        ChenileServiceDefinition csd = chenileConfiguration.getServices().get(service);
        Map<String,Object> m = (Map<String,Object>)csd.getExtension("ChenileMqtt");
        if (m == null){
            throw new ServerException(905,new Object[]{service,operationName });
        }
        String topic = (String)m.get("topic");
        topic = topic + "/" + operationName;
        int qos = (int)m.get("qos");
        System.out.println("Publishing message " + payload + " to " + topic);
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
        v5Client.messageArrivedComplete(message.getId(),qos);
    }
}
