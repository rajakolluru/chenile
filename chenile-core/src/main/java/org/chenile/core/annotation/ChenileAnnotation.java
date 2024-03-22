package org.chenile.core.annotation;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Marker annotation to make a new annotation
 * @author Raja Shankar Kolluru
 *
 */
@Retention(RUNTIME)
@Target(ElementType.ANNOTATION_TYPE)
public @interface ChenileAnnotation {

}
