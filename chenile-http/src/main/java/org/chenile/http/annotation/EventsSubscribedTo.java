package org.chenile.http.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * This annotation specifies that this particular method subscribes to an event.
 * This can be a local or global event. Local events are executed in the same JVM and are
 * directly invoked using {@link org.chenile.core.event.EventProcessor}
 * <p>Remote events are triggered by Kafka or similar. This requires a dependency on
 * chenile-kafka or other modules. </p>
 */
@Retention(RUNTIME)
@Target(METHOD)
public @interface EventsSubscribedTo {
	public String[] value();
}
