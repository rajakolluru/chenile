package org.chenile.mqtt.init;

import org.chenile.base.exception.ConfigurationException;
import org.chenile.core.model.ChenileConfiguration;
import org.chenile.core.model.ChenileServiceDefinition;
import org.chenile.http.annotation.ChenileController;
import org.chenile.mqtt.model.ChenileMqtt;
import org.eclipse.paho.mqttv5.client.MqttAsyncClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.event.EventListener;

import java.util.HashMap;
import java.util.Map;

/**
 * Detects the {@link org.chenile.mqtt.model.ChenileMqtt} annotated classes in the Application Context
 * populates the mqttConfig for later use by {@link org.chenile.mqtt.entry.MqttEntryPoint}
 * Topic and Qos can be configured at the service level. All operations under the service will be
 * subscribed automatically.
 */
public class MqttInitializer {
    @Autowired
    ChenileConfiguration chenileConfiguration;
    @Autowired
    MqttAsyncClient mqttV5Client;

    @Autowired
    ApplicationContext applicationContext;
    @Autowired @Qualifier("mqttConfig")
    Map<String,String> mqttConfig;

    public static final String BASE_TOPIC_NAME = "/chenile";
    @EventListener(ApplicationReadyEvent.class)
    public void init() throws Exception {
        Map<String,Object> beans = applicationContext.getBeansWithAnnotation(ChenileMqtt.class);

        // register all of these beans as Mqtt beans
        for(Map.Entry<String, Object> e: beans.entrySet()) {
            Object bean = e.getValue();
            ChenileMqtt chenileMqtt = bean.getClass().getAnnotation(ChenileMqtt.class);
            ChenileController chenileController = bean.getClass().getAnnotation(ChenileController.class);
            if (chenileController == null){
                throw new ConfigurationException(900,new Object[]{e.getKey()});
            }
            String serviceId = chenileController.value();
            String serviceTopic = chenileMqtt.topic();
            if (serviceTopic.isEmpty()){
                serviceTopic = BASE_TOPIC_NAME + "/" + serviceId;
            }
            int qos = chenileMqtt.qos();
            // since we might have changed the definitions, we need to put the updated values back
            // into the data structure
            putBackIntoServiceDefinition(serviceTopic,qos,serviceId);
            mqttConfig.put(serviceTopic,serviceId);
            System.out.println("Subscribing to topic " + serviceTopic + "/+");
            // subscribe to this topic and all the topics underneath it
            // We use a single level filter since all operations are supported under it
            mqttV5Client.subscribe(serviceTopic +"/+" ,qos);
        }
    }
    private void putBackIntoServiceDefinition(String topic, int qos, String serviceId){
        ChenileServiceDefinition csd = chenileConfiguration.getServices().get(serviceId);
        Map<String,Object> map = new HashMap<>();
        map.put("topic", topic);
        map.put("qos",qos);
        csd.putExtension("ChenileMqtt",map);
    }
}
