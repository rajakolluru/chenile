package org.chenile.cache.test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

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
        
    @Test public void testIt() { 	
		FooModel fooM = new FooModel(23);
		ChenileExchange exchange = chenileExchangeBuilder.makeExchange("fooService","increment",null);
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
    
    @Test public void testWithProxy() { 	
		FooModel fooM = new FooModel(23);
		FooModel fooModified = fooService.increment(fooM);
		assertTrue(fooModified.getIncrement() == 24);
		assertTrue(fooServiceInvoked);
		
		fooServiceInvoked = false;
		fooM = new FooModel(23);
		fooModified = fooService.increment(fooM);
		assertTrue(fooModified.getIncrement() == 24); 
		assertFalse(fooServiceInvoked);
    }
}
