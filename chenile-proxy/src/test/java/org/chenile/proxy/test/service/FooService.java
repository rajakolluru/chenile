package org.chenile.proxy.test.service;

public interface FooService {

	FooModel increment(int inc, FooModel foo);
	FooModel throwException(FooExceptionModel foo);
}