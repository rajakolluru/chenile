package org.chenile.foo.test;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Locale;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.MessageSource;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;


@RunWith(SpringRunner.class)
@SpringBootTest(classes = SpringConfig.class)
@ActiveProfiles("unittest")
@AutoConfigureMockMvc
public class TestFoo {
	@Autowired private MockMvc mvc;
	@Autowired private MessageSource messageSource;
	@Test public void testFoo() throws Exception {
		mvc.perform( MockMvcRequestBuilders
            .get("/test")
            .accept(MediaType.APPLICATION_JSON))
        	.andDo(print())
        	.andExpect(status().isOk())
        	.andExpect(jsonPath("$.payload.test").value("test"))
        	.andExpect(jsonPath("$.payload.foo",is("some_message")));
	}
	
	@Test public void testFooWIthTrajectory() throws Exception {
		mvc.perform( MockMvcRequestBuilders
            .get("/test")
            .header("chenile-trajectory-id", "xxx")
            .accept(MediaType.APPLICATION_JSON))
        	.andDo(print())
        	.andExpect(status().isOk())
        	.andExpect(jsonPath("$.payload.test").value("test1"))
        	.andExpect(jsonPath("$.payload.foo",is("some_message")));
	}
	
	@Test public void testFooWIthTrajectoryYYY() throws Exception {
		mvc.perform( MockMvcRequestBuilders
            .get("/test")
            .header("chenile-trajectory-id", "yyy")
            .accept(MediaType.APPLICATION_JSON))
        	.andDo(print())
        	.andExpect(status().isOk())
        	.andExpect(jsonPath("$.payload.test").value("test"))
        	.andExpect(jsonPath("$.payload.foo",is("some_message")));
	}
	
	@Test public void testHealthChecker() throws Exception {
		mvc.perform( MockMvcRequestBuilders
            .get("/health-check/testService")
            .accept(MediaType.APPLICATION_JSON))
        	.andDo(print())
        	.andExpect(status().isOk())
        	.andExpect(jsonPath("$.payload.message").value("SUCCESS"));
	}
	
	@Test public void testHealthCheckerOverride() throws Exception {
		mvc.perform( MockMvcRequestBuilders
            .get("/health-check/testService")
             .header("chenile-trajectory-id", "xxx")
            .accept(MediaType.APPLICATION_JSON))
        	.andDo(print())
        	.andExpect(status().isOk())
        	.andExpect(jsonPath("$.payload.message").value("SUCCESS1"));
     }
	
	@Test public void testMessageSource() throws Exception {
		String s = messageSource.getMessage("E400", new Object[] {"abc","def"}, "default400", Locale.ENGLISH);
		System.out.println("***************************Value of E400 is " + s);
		s = messageSource.getMessage("E501", new Object[] {"abc"}, "default501", Locale.ENGLISH);
		System.out.println("***************************Value of E501 is " + s);
	}
    
}
