package org.chenile.foo;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import org.chenile.core.annotation.ChenileAnnotation;

@Retention(RUNTIME)
@Target(ElementType.METHOD)
@ChenileAnnotation
public @interface Foo {
	public String message();
}
