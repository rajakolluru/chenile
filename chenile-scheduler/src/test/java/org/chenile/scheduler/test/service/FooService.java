package org.chenile.scheduler.test.service;

import java.util.Date;

public class FooService {
	public void schedule() {
		System.out.println("I am executed at: " + new Date());
	}
}
