package org.chenile.mqtt.test;

import org.chenile.mqtt.pubsub.MqttSubscriber;
import org.eclipse.paho.mqttv5.client.IMqttToken;
import org.eclipse.paho.mqttv5.client.MqttAsyncClient;
import org.eclipse.paho.mqttv5.client.MqttConnectionOptions;
import org.eclipse.paho.mqttv5.client.persist.MemoryPersistence;
import org.eclipse.paho.mqttv5.common.MqttMessage;

public class MainMqtt {

    public static void main(String[] args) throws Exception{
        MqttConnectionOptions connectionOptions = new MqttConnectionOptions();
        connectionOptions.setServerURIs(new String[]{"tcp://localhost:1883"});
        connectionOptions.setKeepAliveInterval(120000);
        connectionOptions.setUserName("emqx_test");
        connectionOptions.setPassword("emqx_test_password".getBytes());
        connectionOptions.setCleanStart(true);
        connectionOptions.setReceiveMaximum(1000);
        connectionOptions.setAutomaticReconnect(true);

        MqttMessage willMessage = new MqttMessage("I am dead".getBytes(), 2, true, null);
        connectionOptions.setWill("willTopic",willMessage);
        MemoryPersistence memoryPersistence = new MemoryPersistence();
        MqttAsyncClient v5Client = new MqttAsyncClient("tcp://localhost:1883","client123",memoryPersistence);
        IMqttToken token = v5Client.connect(connectionOptions);
        token.waitForCompletion(12000);

        MqttSubscriber subscriber = new MqttSubscriber(true);
        v5Client.setCallback(subscriber);
        v5Client.subscribe("/chenile/mqtt", 2);
        for(;;){
            Thread.sleep(1000);
        }
    }
}
