package org.chenile.scheduler.test.service;

import org.chenile.scheduler.test.TestChenileCache;

public class FooService {
	public FooModel increment(FooModel foo) {
		FooModel fooRet = new FooModel();
		int inc = foo.getIncrement();
		fooRet.setIncrement(++inc);
		TestChenileCache.fooServiceInvoked = true;
		return fooRet;
	}
}
