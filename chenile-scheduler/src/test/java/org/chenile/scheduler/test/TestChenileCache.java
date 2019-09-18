package org.chenile.scheduler.test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.chenile.core.context.ChenileExchange;
import org.chenile.core.entrypoint.ChenileEntryPoint;
import org.chenile.core.model.ChenileConfiguration;
import org.chenile.core.model.ChenileServiceDefinition;
import org.chenile.core.model.OperationDefinition;
import org.chenile.scheduler.test.service.FooModel;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;


@RunWith(SpringRunner.class)
@SpringBootTest(classes = SpringConfig.class)
@ActiveProfiles("unittest")
public class TestChenileCache {

    @Autowired ChenileEntryPoint chenileEntryPoint;
    @Autowired ChenileConfiguration chenileConfiguration;
    public static boolean fooServiceInvoked = false;
      
    @Test public void testIt() { 	
		FooModel fooM = new FooModel(23);
		ChenileExchange exchange = makeExchange("fooService","increment");
		exchange.setBody(fooM);
		chenileEntryPoint.execute(exchange);
		FooModel fooModified = (FooModel)exchange.getResponse();
		assertTrue(fooModified.getIncrement() == 24);
		assertTrue(fooServiceInvoked);
		
		fooServiceInvoked = false;
		fooM = new FooModel(23);
		exchange.setBody(fooM);
		chenileEntryPoint.execute(exchange);
		fooModified = (FooModel)exchange.getResponse();
		assertTrue(fooModified.getIncrement() == 24); 
		assertFalse(fooServiceInvoked);
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
