package org.chenile.mqtt;

import org.chenile.core.model.ChenileConfiguration;
import org.chenile.core.model.ChenileServiceDefinition;
import org.chenile.mqtt.model.ChenileMqtt;
import org.eclipse.paho.mqttv5.common.MqttMessage;
import org.eclipse.paho.mqttv5.common.packet.MqttProperties;
import org.eclipse.paho.mqttv5.common.packet.UserProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;

/**
 * A collection of general purpose utilities for MQ-TT
 */
public class MqttInfoProvider {
    @Autowired
    ChenileConfiguration chenileConfiguration;

    public ChenileMqtt obtainChenileMqtt(String serviceId){
        ChenileServiceDefinition csd = chenileConfiguration.getServices().get(serviceId);
        return csd.getExtensionAsAnnotation(ChenileMqtt.class);
    }

    public String getUserPropertyValue(MqttMessage message, String key){
        MqttProperties props = message.getProperties();
        if (props == null) return null;
        for (UserProperty up: props.getUserProperties()){
            if (up.getKey() != null && up.getKey().equals(key)){
                return up.getValue();
            }
        }
        return null;
    }

    public String getSource(MqttMessage message){
        return getUserPropertyValue(message,Constants.SOURCE);
    }

    public String getTarget(MqttMessage message){
        return getUserPropertyValue(message,Constants.TARGET);
    }
    public boolean getTestMode(MqttMessage message){
        String testMode = getUserPropertyValue(message,Constants.TEST_MODE);
        return Boolean.parseBoolean(testMode);
    }
}
