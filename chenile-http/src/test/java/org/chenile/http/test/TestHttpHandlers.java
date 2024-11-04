package org.chenile.http.test;

import org.junit.Test;
import org.junit.jupiter.api.DisplayName;
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
public class TestHttpHandlers {

   @Autowired TestUtil testUtil;
    
   @Test @Order(1)
   @DisplayName("Tests the basic Get functionality.")
    public void testService() throws Exception {
	  testUtil.testService("/system/property/");
    }

   @Test @Order(2)
   @DisplayName("Tests the basic Post functionality. Payload is created from JSON")
    public void testPostService() throws Exception {
	   	testUtil.testPostService("/system/property");
    }

   @Test @Order(3)
   @DisplayName("Tests if the service class throws an exception")
   public void testException() throws Exception {
	   	testUtil.testException("/throw-exception");
   }

    @Test @Order(4)
    @DisplayName("Tests if the interceptor throws an exception in its post process")
    public void testPostProcessInterceptionException() throws Exception {
        testUtil.testPostProcessInterceptionException("/system/property");
    }

    @Test @Order(5)
    @DisplayName("Tests if the interceptor throws an exception in its pre process")
    public void testPreProcessInterceptionException() throws Exception {
        testUtil.testPreProcessInterceptionException("/system/property");
    }
   @Test @Order(6)
   @DisplayName("Tests if the service throws out a warning but is otherwise successful.")
   public void testWarning() throws Exception {
	   	testUtil.testWarning("/throw-warning");
   }
    @Test @Order(7)
    @DisplayName("Tests if the serve throws an exception with multiple errors")
    public void testMultipleExceptions() throws Exception {
        testUtil.testMultipleExceptions("/throw-multiple-exceptions");
    }

    @Test @Order(8)
    @DisplayName("Tests if two interceptors throw out exceptions one after the other in the post process method.")
    public void testDoubleInterceptorException() throws Exception {
        testUtil.testDoubleInterceptorException("/ping");
    }

    @Test @Order(9)
    @DisplayName("Tests if events trigger a call to the method.")
    public void testEvent() throws Exception {
        testUtil.testEvent("/c/save");
    }

    @Test @Order(9)
    @DisplayName("Tests if undefined events trigger a call to the method.")
    public void testUndefinedEvent() throws Exception {
        testUtil.testUndefinedEvent("/c/save");
    }
}
