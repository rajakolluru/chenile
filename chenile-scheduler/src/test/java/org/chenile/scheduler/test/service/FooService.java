package org.chenile.scheduler.test.service;

import org.chenile.scheduler.test.TestChenileScheduler;

public class FooService {
	public void schedule() {
		TestChenileScheduler.latch.countDown();
	}
	
	public void sch(String x, int index) {
		TestChenileScheduler.actualString = x;
		TestChenileScheduler.actualIndex = index;
		TestChenileScheduler.latch1.countDown();
	}
	public void post(int index, FooModel foo){
		TestChenileScheduler.actualIndex = index;
		TestChenileScheduler.fooModel = foo;
		TestChenileScheduler.postLatch.countDown();
	}
}
