package org.chenile.mqtt.test;

import org.junit.ClassRule;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.hivemq.HiveMQContainer;
import org.testcontainers.utility.DockerImageName;

public class MqttBaseTest {

    @ClassRule
    public static HiveMQContainer hivemq
            = new HiveMQContainer(DockerImageName.parse("hivemq/hivemq-ce:latest"));

    static class HostProvider {
        public String getServerURI() {
            return "tcp://" + hivemq.getHost() + ":" + hivemq.getMqttPort();
        }
    }
    static HostProvider hostProvider = new HostProvider();

    @DynamicPropertySource
    static void mqttProperties(DynamicPropertyRegistry registry){
        registry.add("mqtt.connection.ServerURIs",hostProvider::getServerURI);
    }

}
