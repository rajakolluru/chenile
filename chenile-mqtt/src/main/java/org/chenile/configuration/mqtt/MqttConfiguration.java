package org.chenile.configuration.mqtt;

import org.chenile.mqtt.MqttInfoProvider;
import org.chenile.mqtt.entry.MqttEntryPoint;
import org.chenile.mqtt.init.MqttInitializer;
import org.chenile.mqtt.pubsub.MqttPublisher;
import org.chenile.mqtt.pubsub.MqttSubscriber;
import org.eclipse.paho.mqttv5.client.DisconnectedBufferOptions;
import org.eclipse.paho.mqttv5.client.IMqttToken;
import org.eclipse.paho.mqttv5.client.MqttAsyncClient;
import org.eclipse.paho.mqttv5.client.MqttConnectionOptions;
import org.eclipse.paho.mqttv5.client.persist.MemoryPersistence;
import org.eclipse.paho.mqttv5.common.MqttException;
import org.eclipse.paho.mqttv5.common.MqttMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

/**
 * Sets up Eclipse Paho for communicating with MQ-TT broker using configurations
 */
@Configuration
public class MqttConfiguration {
    Logger logger = LoggerFactory.getLogger(MqttConfiguration.class);
    @Value("${mqtt.connection.ServerURIs}") private String hostURI;
    /**
     * This is the base publish topic that will be pre-pended to publish  messages.
     * This can contain specific expressions such as {tenantId} for example which
     * will be substituted by the actual tenantId at the time of publishing a message.
     * This value may not be the same as the base subscribe topic because publishing
     * happens at runtime while subscription happens during startup. Hence, subscription can contain
     * wild cards (such as +) whereas publishing may include expressions that will be
     * substituted from the headers (such as tenant Id etc.)
     * But if it is a constant expression they both can be the same. (default: chenile)
     */
    @Value("${mqtt.publish.base.topic:chenile}") String basePublishTopic;
    /**
     * This is the base topic name that will pre-pended for all subscriptions. It can contain
     * wild cards such as + in accordance with the MQ-TT subscription rules . (default: chenile)
     */
    @Value("${mqtt.subscribe.base.topic:chenile}") private String baseSubscribeTopic;
    @Value("${mqtt.will.payload}") private String willPayload;
    @Value("${mqtt.will.qos}") private int willQos;
    @Value("${mqtt.will.retained}") private boolean willRetained;
    @Value("${mqtt.will.topic}") private String willTopic;
    @Value("${mqtt.clientID}") private String clientID;
    @Value("${mqtt.actionTimeout}") private int actionTimeout;
    @Value("${mqtt.enabled:true}") private boolean mqttEnabled;
    @Value("${mqtt.connection.session.expiry}") private Long sessionExpiry;


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
    @ConfigurationProperties(prefix = "mqtt.disconnected.buffer")
    DisconnectedBufferOptions disconnectedBufferOptions(){
        return new DisconnectedBufferOptions();
    }

    @Bean
    MqttAsyncClient mqttV5Client(@Autowired MqttConnectionOptions connOpts,
                                 @Autowired MemoryPersistence persistence,
                                 @Autowired DisconnectedBufferOptions disconnectedBufferOptions) throws MqttException {
        MqttAsyncClient v5Client = new MqttAsyncClient(hostURI, clientID, persistence);
        v5Client.setBufferOpts(disconnectedBufferOptions);
        // Combination of clean start and session, broker will wait for subscriber for given time,
        // + publisher will store message in memory and publish when connection came back
        connOpts.setCleanStart(false);
        connOpts.setAutomaticReconnect(true);
        connOpts.setKeepAliveInterval(sessionExpiry.intValue());
        connOpts.setSessionExpiryInterval(sessionExpiry);
        IMqttToken token = v5Client.connect(connOpts);
        token.waitForCompletion(actionTimeout);
        logger.info("Connected to the MQTT broker with client ID = {}", clientID);
        return v5Client;
    }

    @Bean
    MqttSubscriber mqttSubscriber(@Autowired MqttAsyncClient v5Client) {
        MqttSubscriber subscriber = new MqttSubscriber(mqttEnabled);
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
        return new MqttInitializer(mqttEnabled,basePublishTopic,baseSubscribeTopic);
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

    @Bean
    MqttInfoProvider mqttInfoProvider(){
        return new MqttInfoProvider();
    }
}
