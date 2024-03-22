package org.chenile.proxy.test.service;

import org.chenile.base.exception.ErrorNumException;

public class FooServiceImpl implements FooService {
	@Override
	public FooModel increment(int inc,FooModel foo) {
		FooModel fooRet = new FooModel();
		fooRet.setIncrement(inc + foo.getIncrement());
		return fooRet;
	}

	@Override
	public FooModel throwException(FooExceptionModel foo) {
		throw new ErrorNumException(foo.errorCode,foo.subErrorCode,foo.message);
	}
}
