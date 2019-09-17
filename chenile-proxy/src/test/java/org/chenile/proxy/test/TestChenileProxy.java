package org.chenile.proxy.test;

import static org.junit.Assert.assertEquals;

import org.chenile.proxy.test.service.FooModel;
import org.chenile.proxy.test.service.FooService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;


@RunWith(SpringRunner.class)
@SpringBootTest(classes = SpringConfig.class)
@ActiveProfiles("unittest")
public class TestChenileProxy {

   @Autowired FooService fooService;
      
    @Test public void testIt() { 	
		FooModel fooM = new FooModel(23);
		fooM = fooService.increment(3, fooM);
		// It should increment by 3 + 1 (by the interceptor)
		assertEquals(27, fooM.getIncrement());
    }
}
