package org.chenile.core.annotation;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Marker annotation to make a new annotation.
 * All annotations that have this marker are considered important. They automatically
 * get stored in {@link org.chenile.core.model.ChenileServiceDefinition} and {@link org.chenile.core.model.OperationDefinition}
 * They can then be retrieved by whoever wants to use these annotations.
 * @author Raja Shankar Kolluru
 *
 */
@Retention(RUNTIME)
@Target(ElementType.ANNOTATION_TYPE)
public @interface ChenileAnnotation {

}
