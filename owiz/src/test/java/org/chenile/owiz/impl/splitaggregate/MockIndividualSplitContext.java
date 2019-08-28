package org.chenile.owiz.impl.splitaggregate;

import java.util.concurrent.CountDownLatch;

import org.chenile.owiz.impl.splitaggregate.IndividualSplitContext;

public class MockIndividualSplitContext implements IndividualSplitContext{
	private String key;
	private Object value;
	private boolean mustWait = false;
	private CountDownLatch  latch ;
	private int timeOutInMilliSeconds  = 10;
	private boolean mustThrowException = false;
	
	public MockIndividualSplitContext(String key, CountDownLatch latch){
		this.setKey(key);
		this.latch = latch;
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
	public boolean isMustWait() {
		return mustWait;
	}
	public void setMustWait(boolean mustWait) {
		this.mustWait = mustWait;
	}
	public CountDownLatch getLatch() {
		return latch;
	}
	public void setLatch(CountDownLatch latch) {
		this.latch = latch;
	}
	public int getTimeOutInMilliSeconds() {
		return timeOutInMilliSeconds;
	}
	public void setTimeOutInMilliSeconds(int timeOutInMilliSeconds) {
		this.timeOutInMilliSeconds = timeOutInMilliSeconds;
	}
	public boolean isMustThrowException() {
		return mustThrowException;
	}
	public void setMustThrowException(boolean mustThrowException) {
		this.mustThrowException = mustThrowException;
	}
}