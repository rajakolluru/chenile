package org.chenile.mqtt.init;

import org.chenile.base.exception.ConfigurationException;
import org.chenile.core.model.ChenileConfiguration;
import org.chenile.core.model.ChenileServiceDefinition;
import org.chenile.http.annotation.ChenileController;
import org.chenile.mqtt.model.ChenileMqtt;
import org.eclipse.paho.mqttv5.client.IMqttToken;
import org.eclipse.paho.mqttv5.client.MqttAsyncClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;

import java.util.HashMap;
import java.util.Map;

/**
 * Detects the {@link org.chenile.mqtt.model.ChenileMqtt} annotated classes in the Application Context and
 * populates the mqttConfig for later use by {@link org.chenile.mqtt.entry.MqttEntryPoint}
 * Topic and Qos can be configured at the service level. (not at the operation level)<br/>
 * When a service is subscribed, all operations under the service will be subscribed automatically.
 */
public class MqttInitializer {
    Logger logger = LoggerFactory.getLogger(MqttInitializer.class);
    @Autowired
    ChenileConfiguration chenileConfiguration;
    @Autowired
    MqttAsyncClient mqttV5Client;

    @Autowired
    ApplicationContext applicationContext;
    @Autowired @Qualifier("mqttConfig")
    Map<String,String> mqttConfig;
    boolean mqttEnabled;
    public MqttInitializer(boolean enabled){
        this.mqttEnabled = enabled;
    }
    public static final String BASE_TOPIC_NAME = "/chenile";
    @EventListener(ApplicationReadyEvent.class)
    @Order(900) // ensure that it is called after core/http got initialized first
    public void init() throws Exception {
        if(!mqttEnabled) return; // don't initialize if mqtt is not enabled.
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

            putBackIntoServiceDefinition(serviceTopic,qos,serviceId);
            mqttConfig.put(serviceTopic,serviceId);
            logger.info("Subscribing to topic " + serviceTopic + "/+");
            // subscribe to this topic and all the topics underneath it
            // We use a single level filter since all operations are supported under it
            IMqttToken token = mqttV5Client.subscribe(serviceTopic + "/+", qos);
            token.waitForCompletion();
        }
    }

    /**
     * Put the details of the data structure back into the service definition. <br/>
     * This is needed since the init method takes default values that are configured in the
     * annotation and mutates them.
     * @param topic - the topic to subscribe for the service
     * @param qos - the qos level to subscribe to
     * @param serviceId - the service Id of the service that gets mapped to the topic and qos
     */
    private void putBackIntoServiceDefinition(String topic, int qos, String serviceId){
        ChenileServiceDefinition csd = chenileConfiguration.getServices().get(serviceId);
        Map<String,Object> map = new HashMap<>();
        map.put("topic", topic);
        map.put("qos",qos);
        csd.putExtension("ChenileMqtt",map);
    }
}
