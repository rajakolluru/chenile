package org.chenile.filewatch.test.service;

import org.chenile.filewatch.test.TestFileWatchFeatures;

public class FooService {
	public void saveFoo(FooModel foo) {
		TestFileWatchFeatures.actualList.add(foo);
		TestFileWatchFeatures.latch.countDown();
	}
}
