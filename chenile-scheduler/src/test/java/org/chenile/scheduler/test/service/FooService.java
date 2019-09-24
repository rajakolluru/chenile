package org.chenile.scheduler.test.service;

import java.util.Date;

import org.chenile.scheduler.test.TestChenileScheduler;

public class FooService {
	public void schedule() {
		TestChenileScheduler.latch.countDown();
	}
	
	public void sch(String x, int index) {
		TestChenileScheduler.actualIndex = index;
		TestChenileScheduler.latch1.countDown();
	}
}
