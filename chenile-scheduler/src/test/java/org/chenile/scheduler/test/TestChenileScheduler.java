package org.chenile.scheduler.test;

import static org.junit.Assert.assertTrue;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;


@RunWith(SpringRunner.class)
@SpringBootTest(classes = SpringConfig.class)
@ActiveProfiles("unittest")
public class TestChenileScheduler {

    public static CountDownLatch latch = new CountDownLatch(1);
      
    @Test public void testIt() throws InterruptedException { 	
		assertTrue("Failed: timed out in 10 seconds without executing the test",
				latch.await(10, TimeUnit.SECONDS));
    }
}
