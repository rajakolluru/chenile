package org.chenile.mqtt.pubsub;

import org.chenile.mqtt.MqttInfoProvider;
import org.chenile.mqtt.entry.MqttEntryPoint;
import org.chenile.mqtt.model.ChenileMqtt;
import org.eclipse.paho.mqttv5.client.IMqttToken;
import org.eclipse.paho.mqttv5.client.MqttAsyncClient;
import org.eclipse.paho.mqttv5.client.MqttCallback;
import org.eclipse.paho.mqttv5.client.MqttDisconnectResponse;
import org.eclipse.paho.mqttv5.common.MqttException;
import org.eclipse.paho.mqttv5.common.MqttMessage;
import org.eclipse.paho.mqttv5.common.packet.MqttProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.Map;

public class MqttSubscriber implements MqttCallback {
    private final boolean mqttEnabled;
    @Autowired @Qualifier("mqttConfig")
    Map<String,String> mqttConfig;
    @Autowired
    MqttAsyncClient v5Cient;
    Logger logger = LoggerFactory.getLogger(MqttSubscriber.class);
    @Autowired
    MqttEntryPoint mqttEntryPoint;
    @Autowired
    MqttInfoProvider mqttInfoProvider;
    @Autowired
    MqttPublisher publisher;

    public MqttSubscriber(boolean mqttEnabled) {
        this.mqttEnabled = mqttEnabled;
    }

    private void log(String message){
        logger.info(message);
    }
    @Override
    public void disconnected(MqttDisconnectResponse disconnectResponse) {
        String cause = null;
        if (disconnectResponse.getException().getMessage() != null) {
            cause = disconnectResponse.getException().getMessage();
        } else {
            cause = disconnectResponse.getReasonString();
        }
        log("Disconnected: " + cause);
    }

    @Override
    public void mqttErrorOccurred(MqttException exception) {
        log(String.format("An MQTT error occurred: %s", exception.getMessage()));
    }

    @Override
    public void messageArrived(String topic, MqttMessage message) throws Exception {
        String messageContent = new String(message.getPayload());
        log("Received at topic = |" + topic + "| message = ||\n" + messageContent + "||\n"
                + " with ID = " + message.getId());
        if(message.isDuplicate()){
            log("Received duplicate message: " + messageContent);
            publisher.sendAck(message);
            return;
        }
        mqttEntryPoint.process(topic,message);
        logger.info("Done with MQtt entry point. sending ack");
        publisher.sendAck(message);
    }

    @Override
    public void deliveryComplete(IMqttToken token) {
        log(String.format("Message %d was delivered.", token.getMessageId()));
    }

    @Override
    public void connectComplete(boolean reconnect, String serverURI) {
        log(String.format("Connection to %s complete. Reconnect=%b", serverURI, reconnect));
        if (reconnect && mqttConfig != null && !mqttConfig.isEmpty() && v5Cient != null &&
             mqttEnabled ){
            for (String serviceTopic: mqttConfig.keySet()){
                try {
                    ChenileMqtt m = mqttInfoProvider.obtainChenileMqtt(mqttConfig.get(serviceTopic));
                    v5Cient.subscribe(serviceTopic + "/+", m.qos());
                }catch(Exception e){
                    log("Unable to subscribe to topic " + serviceTopic + ". Error = " + e.getMessage());
                }
            }
        }
    }

    @Override
    public void authPacketArrived(int reasonCode, MqttProperties properties) {
        log(String.format("Auth packet received, this client does not currently support them. Reason Code: %d.",
                reasonCode));
    }
}
