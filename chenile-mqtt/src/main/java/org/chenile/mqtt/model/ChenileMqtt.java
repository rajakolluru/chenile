package org.chenile.mqtt.model;

import org.chenile.core.annotation.ChenileAnnotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Annotate a class in a Chenile service controller to make it listen to MQ-TT topics.<br/>
 * This will bind the topic to an invocation of this service/operation. A good convention to follow
 * is to use the form /chenile/serviceName/operationName as the name of the topic. This will
 * be the default topic name if a topic is not explicitly specified.<br/>
 * When messages come to the topic, the service/operation is invoked.<br/>
 * Payload is the content of the message.<br/>
 * Headers are from UserProperty objects that are attached to the message.<br/>
 */
@Retention(RUNTIME)
@Target(ElementType.TYPE)
@ChenileAnnotation
public @interface ChenileMqtt {
    public String subscribeTopic() default "";
    public String publishTopic() default "";
    public int qos() default 2;
}
