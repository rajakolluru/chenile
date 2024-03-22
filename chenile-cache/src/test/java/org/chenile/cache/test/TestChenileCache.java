package org.chenile.cache.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.chenile.base.response.GenericResponse;
import org.chenile.cache.test.service.FooModel;
import org.chenile.cache.test.service.FooService;
import org.chenile.core.context.ChenileExchange;
import org.chenile.core.context.ChenileExchangeBuilder;
import org.chenile.core.entrypoint.ChenileEntryPoint;
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
    @Autowired ChenileExchangeBuilder chenileExchangeBuilder;
    @Autowired FooService fooService;
    public static boolean fooServiceInvoked = false;
        
    @Test
    public void testIt() { 	
		FooModel fooM = new FooModel(23);
		ChenileExchange exchange = chenileExchangeBuilder.makeExchange("fooService","increment",null);
		exchange.setBody(fooM);
		chenileEntryPoint.execute(exchange);
		GenericResponse<?> genericResponse = (GenericResponse<?>)(exchange.getResponse());
		FooModel fooModified = (FooModel)genericResponse.getData();
		assertTrue(fooModified.getIncrement() == 24);
		assertTrue(fooServiceInvoked);
		
		fooServiceInvoked = false;
		fooM = new FooModel(23);
		exchange.setBody(fooM);
		chenileEntryPoint.execute(exchange);
		genericResponse = (GenericResponse<?>)(exchange.getResponse());
		fooModified = (FooModel)genericResponse.getData();
		assertTrue(fooModified.getIncrement() == 24); 
		assertFalse(fooServiceInvoked);
    }
    
    @Test 
    public void testWithProxy() {
    	fooServiceInvoked = false;
		FooModel fooM = new FooModel(29);
		FooModel fooModified = fooService.increment(fooM);
		assertTrue(fooModified.getIncrement() == 30);
		assertTrue(fooServiceInvoked);
		
		fooServiceInvoked = false;
		fooM = new FooModel(29);
		fooModified = fooService.increment(fooM);
		assertTrue(fooModified.getIncrement() == 30); 
		assertFalse(fooServiceInvoked);
    }
    
    @SuppressWarnings("unchecked")
	@Test
    public void testSum() {
    	fooServiceInvoked = false;
    	ChenileExchange exchange = chenileExchangeBuilder.makeExchange("fooService","sum2",null);
    	chenileEntryPoint.execute(exchange);
    	GenericResponse<Integer> genericResponse = (GenericResponse<Integer>)(exchange.getResponse());
    	int result = (int)genericResponse.getData();
    	assertTrue(result == 2);
		assertTrue(fooServiceInvoked);
    	
		fooServiceInvoked = false;
		chenileEntryPoint.execute(exchange);
		genericResponse = (GenericResponse<Integer>)(exchange.getResponse());
    	result = (int)genericResponse.getData();
    	assertTrue(result == 2);
    	assertFalse(fooServiceInvoked);

    	exchange = chenileExchangeBuilder.makeExchange("fooService","sum",null);
    	exchange.setHeader("a", 1);
    	exchange.setHeader("b", 2);
		chenileEntryPoint.execute(exchange);
		genericResponse = (GenericResponse<Integer>)(exchange.getResponse());
    	result = (int)genericResponse.getData();
    	assertTrue(result == 3);
		assertTrue(fooServiceInvoked);
		
		fooServiceInvoked = false;
		exchange.setHeader("a", 1);
    	exchange.setHeader("b", 2);
		chenileEntryPoint.execute(exchange);
		genericResponse = (GenericResponse<Integer>)(exchange.getResponse());
    	result = (int)genericResponse.getData();
    	assertTrue(result == 3);
    	assertFalse(fooServiceInvoked);
    }
    
    @Test
    public void testSumWithProxy() {
    	fooServiceInvoked = true;
    	int result = fooService.sum2();
    	assertTrue(result == 2);
    	assertTrue(fooServiceInvoked);
    	
		fooServiceInvoked = false;
    	result = fooService.sum2();
    	assertTrue(result == 2);
    	assertFalse(fooServiceInvoked);
    	
    	result = fooService.sum(9, 15);
    	assertEquals("Actual & Expected dont match",24,result);
		assertTrue("Foo service must be invoked but it was not invoked",fooServiceInvoked);
		
		fooServiceInvoked = false;
		result = fooService.sum(9, 15);
		assertEquals("Actual & Expected dont match",24,result);
    	assertFalse("Foo service must not be invoked but it got invoked",fooServiceInvoked);
    }
}
