package org.chenile.scheduler.test.service;

import java.util.Date;

import org.chenile.scheduler.test.TestChenileScheduler;

public class FooService {
	public void schedule() {
		System.out.println("I am executed at: " + new Date());
		TestChenileScheduler.latch.countDown();
	}
}
