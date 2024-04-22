package org.chenile.http.test;

import org.junit.Test;
import org.junit.jupiter.api.Order;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;


@RunWith(SpringRunner.class)
@SpringBootTest(classes = TestChenileHttp.class)
@AutoConfigureMockMvc
@ActiveProfiles("unittest")
public class TestAnnotationController {

   @Autowired TestUtil testUtil;
    
   @Test @Order(1)
    public void testService() throws Exception {
	  testUtil.testService("/c/getOne/");
    }

   @Test @Order(2)
    public void testPostService() throws Exception {
	   	testUtil.testPostService("/c/save");
    }

   @Test @Order(3)
   public void testException() throws Exception {
	   	testUtil.testException("/c/throw-exception");
   }

    @Test @Order(4)
    public void testPostProcessInterceptionException() throws Exception {
        testUtil.testPostProcessInterceptionException("/c/save");
    }

    @Test @Order(5)
    public void testPreProcessInterceptionException() throws Exception {
        testUtil.testPreProcessInterceptionException("/c/save");
    }
     
   @Test @Order(6)
   public void testWarning() throws Exception {
	   	testUtil.testWarning("/c/throw-warning");
   }

    @Test @Order(7)
    public void testMultipleExceptions() throws Exception {
        testUtil.testMultipleExceptions("/c/throw-multiple-exceptions");
    }

    @Test @Order(8)
    public void testDoubleInterceptorException() throws Exception {
        testUtil.testDoubleInterceptorException("/c/ping");
    }
}
