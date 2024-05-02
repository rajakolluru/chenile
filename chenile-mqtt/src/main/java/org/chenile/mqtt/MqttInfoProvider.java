package org.chenile.mqtt;

import org.chenile.core.model.ChenileConfiguration;
import org.chenile.core.model.ChenileServiceDefinition;
import org.chenile.mqtt.model.ChenileMqtt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;

public class MqttInfoProvider {
    @Autowired
    ChenileConfiguration chenileConfiguration;

    public ChenileMqtt obtainChenileMqtt(String serviceId){
        ChenileServiceDefinition csd = chenileConfiguration.getServices().get(serviceId);
        return csd.getExtensionAsAnnotation(ChenileMqtt.class);
    }
}
