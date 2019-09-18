package org.chenile.scheduler.test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.chenile.core.context.ChenileExchange;
import org.chenile.core.entrypoint.ChenileEntryPoint;
import org.chenile.core.model.ChenileConfiguration;
import org.chenile.core.model.ChenileServiceDefinition;
import org.chenile.core.model.OperationDefinition;
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

    @Autowired ChenileEntryPoint chenileEntryPoint;
    @Autowired ChenileConfiguration chenileConfiguration;
    public static boolean fooServiceInvoked = false;
    private CountDownLatch latch = new CountDownLatch(1);
      
    @Test public void testIt() throws InterruptedException { 	
		ChenileExchange exchange = makeExchange("fooService","schedule");
//		exchange.setBody(fooM);
		chenileEntryPoint.execute(exchange);
		//Runs at `5` second interval.
		//Wait for few seconds before shutting down the test.
		latch.await(10, TimeUnit.SECONDS);
    }
    
    private ChenileServiceDefinition findService(String serviceName) {
		return chenileConfiguration.getServices().get(serviceName);
	}
	
	private OperationDefinition findOperationInService(ChenileServiceDefinition serviceDefinition, String opName) {
		for (OperationDefinition od: serviceDefinition.getOperations()) {
			if (od.getName().equals(opName)){
				return od;
			}
		}
		return null;
	}
	
	private ChenileExchange makeExchange(String serviceName, String opName) {
		ChenileServiceDefinition serviceDefinition = findService(serviceName);
		OperationDefinition operationDefinition = findOperationInService(serviceDefinition, opName);
		ChenileExchange exchange = new ChenileExchange();
		exchange.setServiceDefinition(serviceDefinition);
		exchange.setOperationDefinition(operationDefinition);
		return exchange;
	}
}
