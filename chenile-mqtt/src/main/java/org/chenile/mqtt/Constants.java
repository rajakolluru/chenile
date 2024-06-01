package org.chenile.mqtt;

/**
 * Chenile MQTT constants
 */
public class Constants {
    /**
     * This is the value for the {@link org.chenile.core.context.HeaderUtils#ENTRY_POINT} header
     * in {@link org.chenile.core.context.ChenileExchange} for all requests that came through MQ-TT
     */
    public static final String MQTT_ENTRY_POINT = "MQ-TT";
    public static final String SOURCE = "source"; // the source of the message in MQTT
    public static final String TARGET = "target"; // the target of the message in MQTT

    public static final String TEST_MODE = "test-mode"; // used only for testing. so we ignore the
    // source and current client ID being the same for a test case.
}