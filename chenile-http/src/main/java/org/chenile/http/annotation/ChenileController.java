/**
 * 
 */
package org.chenile.http.annotation;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Annotate all chenile controllers with this annotation. This makes a normal
 * Spring controller into a Chenile controller. Chenile controllers define a service
 * that maps to a Service Impl but also has other policies attached to it.
 */
@Retention(RUNTIME)
@Target(TYPE)
@Documented
public @interface ChenileController {
	String value();
	String serviceName() default "";
	String healthCheckerName() default "";
	String mockName() default "";
	Class<?> interfaceClass() default Object.class;
}
