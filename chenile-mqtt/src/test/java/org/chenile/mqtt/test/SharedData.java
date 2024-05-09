package org.chenile.mqtt.test;

import java.util.concurrent.CountDownLatch;

/**
 * Shared data structure that is updated by the server and checked by the
 * test class. It contains a countdown latch that will be updated when the
 * server is done so the test class can check
 */
public class SharedData {
    public CountDownLatch latch = new CountDownLatch(1);
    public int sum; // this will be updated with the computed sum by the test class.
}
