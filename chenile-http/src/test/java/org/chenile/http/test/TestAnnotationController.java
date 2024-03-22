package org.chenile.http.test;

import org.junit.Test;
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
    
   @Test
    public void testService() throws Exception {
	  testUtil.testService("/c/getOne/");
    }

   @Test
    public void testPostService() throws Exception {
	   	testUtil.testPostService("/c/save");
    }

   @Test
   public void testException() throws Exception {
	   	testUtil.testException("/c/throw-exception");
   }
     
   @Test
   public void testWarning() throws Exception {
	   	testUtil.testWarning("/c/throw-warning");
   }
}
