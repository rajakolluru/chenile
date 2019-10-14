package org.chenile.cache.test.service;

import org.chenile.cache.test.TestChenileCache;

public class FooServiceImpl implements FooService {
	@Override
	public FooModel increment(FooModel foo) {
		FooModel fooRet = new FooModel();
		int inc = foo.getIncrement();
		fooRet.setIncrement(++inc);
		TestChenileCache.fooServiceInvoked = true;
		return fooRet;
	}

	@Override
	public int sum2() {
		return this.sum(1, 1);
	}
	
	@Override
	public int sum(int a, int b) {
		TestChenileCache.fooServiceInvoked = true;
		return a + b;
	}
}
