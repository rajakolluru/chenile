package org.chenile.configuration.mqtt;

import org.chenile.mqtt.entry.MqttEntryPoint;
import org.chenile.mqtt.init.MqttInitializer;
import org.chenile.mqtt.pubsub.MqttPublisher;
import org.chenile.mqtt.pubsub.MqttSubscriber;
import org.eclipse.paho.mqttv5.client.IMqttToken;
import org.eclipse.paho.mqttv5.client.MqttAsyncClient;
import org.eclipse.paho.mqttv5.client.MqttConnectionOptions;
import org.eclipse.paho.mqttv5.client.persist.MemoryPersistence;
import org.eclipse.paho.mqttv5.common.MqttException;
import org.eclipse.paho.mqttv5.common.MqttMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConfigurationPropertiesBinding;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.convert.converter.Converter;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class MqttConfiguration {
    @Value("${mqtt.connection.ServerURIs}") private String hostURI;
    @Value("${mqtt.will.payload}") private String willPayload;
    @Value("${mqtt.will.qos}") private int willQos;
    @Value("${mqtt.will.retained}") private boolean willRetained;
    @Value("${mqtt.will.topic}") private String willTopic;
    @Value("${mqtt.clientID}") private String clientID;
    @Value("${mqtt.actionTimeout}") private int actionTimeout;
    @Value("${mqtt.enabled:true}") private boolean mqttEnabled = true;

    /**
     * This converts a string to a byte array. This is required to convert the password which is
     * given in the properties file as a string to a byte array that can be set in conn opts
     * @return
     */
    @Bean
    @ConfigurationPropertiesBinding
    Converter<String,byte[]> convertStringToBytes(){
        return new Converter<String,byte[]>(){
            @Override
            public byte[] convert(String source) {
                return source.getBytes();
            }
        };
    }
    @Bean
    @ConfigurationProperties(prefix = "mqtt.connection")
    MqttConnectionOptions mqttConnectionOpts(){
        return new MqttConnectionOptions();
    }
    @Bean
    MqttMessage willMessage(@Autowired MqttConnectionOptions options){
        MqttMessage willMessage = new MqttMessage(willPayload.getBytes(), willQos, willRetained, null);
        options.setWill(willTopic,willMessage);
        return willMessage;
    }

    @Bean
    MemoryPersistence memoryPersistence(){
        return new MemoryPersistence();
    }

    @Bean
    MqttAsyncClient mqttV5Client(@Autowired MqttConnectionOptions connOpts,
                                 @Autowired MemoryPersistence persistence) throws MqttException {
        MqttAsyncClient v5Client = new MqttAsyncClient(hostURI, clientID, persistence);
        IMqttToken token = v5Client.connect(connOpts);
        token.waitForCompletion(actionTimeout);
        return v5Client;
    }

    @Bean
    MqttSubscriber mqttSubscriber(@Autowired MqttAsyncClient v5Client) {
        MqttSubscriber subscriber = new MqttSubscriber();
        v5Client.setCallback(subscriber);
        return subscriber;
    }
    @Bean @ConfigurationProperties(prefix = "mqtt.publish")
    MqttPublisher mqttPublisher(){
        return new MqttPublisher();
    }
    @Bean
    MqttEntryPoint mqttEntryPoint(){
        return new MqttEntryPoint();
    }
    @Bean
    MqttInitializer mqttInitializer(){
        return new MqttInitializer(mqttEnabled);
    }

    /**
     * A topic to service map.<br/>
     * This map is internally used to route a message that arrives at a topic to a service.<br/>
     * This map is populated by the MqttInitializer during the initialization phase.<br/>
     * It is used by the MqttEntryPoint during runtime to invoke the appropriate operation in a service<br/>
     * @return a configuration that maps a route to a service.
     *
     */
    @Bean
    Map<String,String> mqttConfig(){
        return new HashMap<>();
    }
}
