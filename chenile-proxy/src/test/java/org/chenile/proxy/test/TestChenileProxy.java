package org.chenile.proxy.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.chenile.base.exception.ErrorNumException;
import org.chenile.proxy.test.service.FooExceptionModel;
import org.chenile.proxy.test.service.FooModel;
import org.chenile.proxy.test.service.FooService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;


@RunWith(SpringRunner.class)
@SpringBootTest(classes = SpringConfig.class,webEnvironment = WebEnvironment.DEFINED_PORT)
@ActiveProfiles("unittest")
public class TestChenileProxy {

   @Autowired FooService fooService;
   @Autowired @Qualifier("fooServiceOnlyRemote") FooService fooServiceOnlyRemote;

   @Autowired
      
    @Test public void testIt() { 	
		FooModel fooM = new FooModel(23);
		fooM = fooService.increment(3, fooM);
		// It should increment by 3 + 1 (by the interceptor)
		assertEquals(27, fooM.getIncrement());
    }

	@Test public void testPathParams() {
		FooModel fooM = new FooModel(23);
		fooM = fooServiceOnlyRemote.increment1(3, fooM);
		// It should increment by 3 + 1 (by the interceptor)
		assertEquals(27, fooM.getIncrement());
	}
    
    @Test public void testWithNonInterfaceMethods() {
    	assertEquals("ProxyBuilder.Proxy.fooService",fooService.toString());
    }
    
    /**
     * Test the remote proxy
     */
    @Test public void testRemote() {
    	FooModel fooM = new FooModel(23);
		fooM = fooServiceOnlyRemote.increment(3, fooM);
		// It should increment by 3 + 1 (by the interceptor)
		assertEquals(27, fooM.getIncrement());
    }
    
    /**
     * Test the remote proxy for exceptions
     */
    @Test public void testRemoteForExceptions() {
    	FooExceptionModel e = new FooExceptionModel();
    	e.errorCode = 400; e.subErrorCode = 2002; e.message = "exception message";
    	try {
    		fooServiceOnlyRemote.throwException(e);
    		// must never come here
    		assertTrue("fooService must have thrown an exception", false);
    	}catch(Throwable t) {
    		assertTrue("Exception must be an error num exception",t instanceof ErrorNumException);
    		ErrorNumException ex = (ErrorNumException)t;
    		assertEquals("Error code must be " + e.errorCode,e.errorCode,ex.getErrorNum());
    		assertEquals("Sub Error Code must be " + e.subErrorCode, e.subErrorCode,
    				ex.getSubErrorNum());
    		assertEquals("Exception message must be " + e.message, e.message,
    				ex.getMessage());
    	}
		
    }
    
}
