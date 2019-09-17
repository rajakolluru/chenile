package org.chenile.proxy.test.service;

public class FooServiceImpl implements FooService {
	@Override
	public FooModel increment(int inc,FooModel foo) {
		FooModel fooRet = new FooModel();
		fooRet.setIncrement(inc + foo.getIncrement());
		return fooRet;
	}
}
