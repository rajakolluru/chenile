package org.chenile.owiz.impl.splitaggregate;

import java.util.concurrent.CountDownLatch;

import org.chenile.owiz.impl.splitaggregate.IndividualSplitContext;

public class MockComponentIndividualSplitContext implements IndividualSplitContext{
	private String key;
	private Object value;
	
	public MockComponentIndividualSplitContext(String key, CountDownLatch latch){
		this.setKey(key);
	}
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	public Object getValue() {
		return value;
	}
	public void setValue(Object value) {
		this.value = value;
	}
	
	
}