package org.chenile.scheduler.test;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.chenile.scheduler.init.SchedulerBuilder;
import org.chenile.scheduler.model.SchedulerInfo;
import org.chenile.scheduler.test.service.FooModel;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.*;


@RunWith(SpringRunner.class)
@SpringBootTest(classes = SpringConfig.class)
@ActiveProfiles("unittest")
public class TestChenileScheduler {

	public static FooModel fooModel;
	@Autowired SchedulerBuilder schedulerBuilder;
    public static CountDownLatch latch = new CountDownLatch(1);
    public static CountDownLatch latch1 = new CountDownLatch(1);
	public static CountDownLatch postLatch = new CountDownLatch(1);
    public static int expectedIndex = 286;
    public static int actualIndex;
	public static final String expectedString = "fooString";
	public static String actualString;
      
    @Test public void testIt() throws Exception {
		assertTrue("Failed: timed out in 10 seconds without executing the test",
				latch.await(10, TimeUnit.SECONDS));
		testPost();
		testManuallyConfiguredGet();
	}

	private void testPost() throws Exception {
		assertTrue("Failed: timed out in 10 seconds without executing the test",
				postLatch.await(10, TimeUnit.SECONDS));
		assertEquals(expectedIndex,actualIndex);
		assertNotNull(fooModel);
		assertEquals("value-of-x",fooModel.x);
		assertEquals("value-of-y",fooModel.y);
	}

	private void testManuallyConfiguredGet() throws Exception{
		SchedulerInfo schedulerInfo = new SchedulerInfo();
		schedulerInfo.serviceName = "fooService";
		schedulerInfo.operationName = "sch";
		schedulerInfo.setCronSchedule("0/5 * * * * ? *");
		schedulerInfo.setJobDescription("Job1");
		schedulerInfo.setJobName("jobname");
		schedulerInfo.setTriggerGroup("grp1");
		schedulerInfo.setTriggerName("name1");
		Map<String,Object> map = new HashMap<>();
		map.put("x", expectedString);
		map.put("index", expectedIndex);
		schedulerInfo.setHeaders(map);
		schedulerBuilder.scheduleJob(schedulerInfo);
		assertTrue("Failed: timed out in 10 seconds without executing the test - second",
				latch1.await(10, TimeUnit.SECONDS));
		assertEquals(expectedIndex, actualIndex );
		assertEquals(expectedString, actualString);
    }
    
    
}
