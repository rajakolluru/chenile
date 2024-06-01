package org.chenile.http.annotation;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import org.chenile.core.annotation.ChenileAnnotation;

/**
 * This is required to state the possible response codes for successful responses.<br/>
 * This is not required for error responses since the exception will already have the response code
 * for error responses.
 */
@Retention(RUNTIME)
@Target(METHOD)
@Documented
@ChenileAnnotation
public @interface ChenileResponseCodes {
	public int success() default 200;
	public int warning() default 200;
}
