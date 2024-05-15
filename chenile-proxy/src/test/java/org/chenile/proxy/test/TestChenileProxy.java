package org.chenile.proxy.test;

import com.github.tomakehurst.wiremock.junit.WireMockRule;
import org.chenile.base.exception.ErrorNumException;
import org.chenile.proxy.test.service.FooExceptionModel;
import org.chenile.proxy.test.service.FooModel;
import org.chenile.proxy.test.service.FooService;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.*;


@RunWith(SpringRunner.class)
@SpringBootTest(classes = SpringConfig.class,webEnvironment = WebEnvironment.DEFINED_PORT)
@ActiveProfiles("unittest")
public class TestChenileProxy {
	@Rule
	public WireMockRule wireMockRule = new WireMockRule(8089);
   @Autowired FooService fooService;
   @Autowired @Qualifier("fooServiceOnlyRemote") FooService fooServiceOnlyRemote;

   @Autowired @Qualifier("wireMockProxy") FooService wireMockProxy;

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
            fail("fooService must have thrown an exception");
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

	// This test proves that Chenile proxy can work equally well even if the
	// target is wire mock which is not a chenile service
	@Test public void testWireMockProxy() {
		FooModel fooM = new FooModel(23);
		fooM = wireMockProxy.increment(3, fooM);
		// It should increment by 3
		assertEquals(26, fooM.getIncrement());
	}

	// Trap a disconnected exception. Check if the error code emitted by Chenile Proxy is 650
	@Test public void testDisconnectedWireMockProxy() {
		wireMockRule.stop();
		FooModel fooM = new FooModel(23);
		try {
			fooM = wireMockProxy.increment(3, fooM);
		}catch(ErrorNumException e){
			assertEquals(650, e.getSubErrorNum());
		}
	}
}
