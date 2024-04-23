package org.chenile.proxy.test.service;

public interface FooService {

	FooModel increment(int inc, FooModel foo);
	FooModel throwException(FooExceptionModel foo);

	/**
	 *
	 * An alternative to increment() to test the path param functionality.
	 */
	FooModel increment1(int inc, FooModel foo);
}