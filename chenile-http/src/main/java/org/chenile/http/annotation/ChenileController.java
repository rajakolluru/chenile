/**
 * 
 */
package org.chenile.http.annotation;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Retention(RUNTIME)
@Target(TYPE)
@Documented
/**
 * Annotate all chenile controllers with this annotation
 * @author Raja Shankar Kolluru
 *
 */
public @interface ChenileController {
	String value();
	String serviceName() default "";
	String healthCheckerName() default "";
	String mockName() default "";
	Class<?> interfaceClass() default Object.class;
}
