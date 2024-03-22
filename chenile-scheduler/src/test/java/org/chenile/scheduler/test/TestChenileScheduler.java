package org.chenile.scheduler.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.chenile.core.model.SchedulerInfo;
import org.chenile.scheduler.init.SchedulerBuilder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;


@RunWith(SpringRunner.class)
@SpringBootTest(classes = SpringConfig.class)
@ActiveProfiles("unittest")
public class TestChenileScheduler {

	@Autowired SchedulerBuilder schedulerBuilder;
    public static CountDownLatch latch = new CountDownLatch(1);
    public static CountDownLatch latch1 = new CountDownLatch(1);
    public static String expectedIndex = "286";
    public static int actualIndex;
      
    @Test public void testIt() throws Exception { 	
		assertTrue("Failed: timed out in 10 seconds without executing the test",
				latch.await(10, TimeUnit.SECONDS));
		
		SchedulerInfo schedulerInfo = new SchedulerInfo();
		schedulerInfo.setCronSchedule("0/5 * * * * ? *");
		schedulerInfo.setJobDescription("Job1");
		schedulerInfo.setJobName("jobname");
		schedulerInfo.setTriggerGroup("grp1");
		schedulerInfo.setTriggerName("name1");
		Map<String,Object> map = new HashMap<>();
		map.put("x", "y");
		map.put("index", expectedIndex);
		schedulerInfo.setJobMetadata(map);
		schedulerBuilder.scheduleJob("fooService", "sch", schedulerInfo);
		assertTrue("Failed: timed out in 10 seconds without executing the test - second",
				latch1.await(10, TimeUnit.SECONDS));
		assertEquals(expectedIndex, actualIndex + "");
    }
    
    
}
