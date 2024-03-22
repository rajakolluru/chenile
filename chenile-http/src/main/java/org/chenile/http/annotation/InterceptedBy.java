/**
 * 
 */
package org.chenile.http.annotation;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Retention(RUNTIME)
@Target(ElementType.METHOD)
@Documented
/**
 * Use this annotation to specify the interceptors at both the
 * service or operation level.
 * @author Raja Shankar Kolluru
 *
 */
public @interface InterceptedBy {
	String[] value() default {};
}
