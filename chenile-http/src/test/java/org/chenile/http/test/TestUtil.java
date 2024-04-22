package org.chenile.http.test;

import static org.chenile.testutils.SpringMvcUtils.assertErrors;
import static org.chenile.testutils.SpringMvcUtils.assertWarnings;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.chenile.http.test.service.JsonData;
import org.chenile.http.test.service.JsonInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;

public class TestUtil {
   @Autowired private MockMvc mvc;
   
    public void testService(String url) throws Exception {
	   String id = "myid";
	   String headerValue = "my.value";
        mvc.perform( MockMvcRequestBuilders
            .get(url + id)
            .header(JsonInterceptor.SOME_SPECIAL_HEADER, headerValue)
            .accept(MediaType.APPLICATION_JSON))
            // .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.payload.someSpecialHeaderValue").value(headerValue + JsonInterceptor.SOME_CONSTANT))
            .andExpect(jsonPath("$.payload.id",is(id))) //a variant for using jsonpath
            .andExpect(jsonPath("$.payload.name").value("Hello"));
    }


   
    public void testPostService(String url) throws Exception {
	   	String id = "foo"; String name = "bar";
	    String headerValue = "my.value";
	    JsonData jsondata = new JsonData(id,name);
        mvc.perform( MockMvcRequestBuilders
            .post(url)
            .header(JsonInterceptor.SOME_SPECIAL_HEADER, headerValue)
            .content(asJsonString(jsondata))
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.payload.someSpecialHeaderValue").value(headerValue + JsonInterceptor.SOME_CONSTANT))
            .andExpect(jsonPath("$.payload.id").value(id))
            .andExpect(jsonPath("$.payload.name").value(name));
    }

   
   public void testException(String url) throws Exception {
	   	String id = "foo"; String name = "bar";
	   	String exceptionMessage = "chenile-http-test";
	   	int errorNum = 2000;
	    JsonData jsondata = new JsonData(id,name);
	    jsondata.setMessage(exceptionMessage);
	    jsondata.setErrorNum(errorNum);
        ResultActions actions = mvc.perform( MockMvcRequestBuilders
           .post(url)
           .content(asJsonString(jsondata))
           .contentType(MediaType.APPLICATION_JSON)
           .accept(MediaType.APPLICATION_JSON))
           // .andDo(print())
           .andExpect(status().is5xxServerError());
        assertErrors(actions,500,errorNum,exceptionMessage);
   }
   
   
   
   public void testWarning(String url) throws Exception {
	   	String id = "foo"; String name = "bar";
	   	String warningMessage = "chenile-http-test-warning";
	   	int errorNum = 2000;
	    JsonData jsondata = new JsonData(id,name);
	    jsondata.setMessage(warningMessage);
	    jsondata.setErrorNum(errorNum);
       ResultActions actions = mvc.perform( MockMvcRequestBuilders
           .post(url)
           .content(asJsonString(jsondata))
           .contentType(MediaType.APPLICATION_JSON)
           .accept(MediaType.APPLICATION_JSON))
           // .andDo(print())
           .andExpect(status().isOk())
           .andExpect(jsonPath("$.payload").exists())
           .andExpect(jsonPath("$.payload.id").value(id))
           .andExpect(jsonPath("$.payload.name").value(name));
       assertWarnings(actions,errorNum,warningMessage);
   }

    public void testPostProcessInterceptionException(String url) throws Exception {
        String id = "foo"; String name = "bar";
        String exceptionMessage = "interceptor-exception-post-process";
        int errorNum = 2000;
        JsonData jsondata = new JsonData(id,name);
        jsondata.setMessage(exceptionMessage);
        jsondata.setErrorNum(errorNum);
        ResultActions actions = mvc.perform( MockMvcRequestBuilders
            .post(url)
            .header(JsonInterceptor.SOME_SPECIAL_HEADER, "throw-exception-post-process")
            .content(asJsonString(jsondata))
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON))
            // .andDo(print())
            .andExpect(status().is5xxServerError())
            .andExpect(jsonPath("$.errors[0]").exists());
        assertErrors(actions,500,errorNum,exceptionMessage);
    }

    public void testPreProcessInterceptionException(String url) throws Exception {
        String id = "foo"; String name = "bar";
        String exceptionMessage = "interceptor-exception-pre-process";
        int errorNum = 2000;
        JsonData jsondata = new JsonData(id,name);
        jsondata.setMessage(exceptionMessage);
        jsondata.setErrorNum(errorNum);
        ResultActions actions = mvc.perform( MockMvcRequestBuilders
            .post(url)
            .header(JsonInterceptor.SOME_SPECIAL_HEADER, "throw-exception-pre-process")
            .content(asJsonString(jsondata))
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON))
            //.andDo(print())
            .andExpect(status().is5xxServerError());
        assertErrors(actions,500,errorNum,exceptionMessage);
    }

    public void testMultipleExceptions(String url) throws Exception {
        String id = "foo"; String name = "bar";
        String exceptionMessage = "chenile-http-test";
        int errorNum = 2000;
        JsonData jsondata = new JsonData(id,name);
        jsondata.setMessage(exceptionMessage);
        jsondata.setErrorNum(errorNum);
        ResultActions actions = mvc.perform( MockMvcRequestBuilders
            .post(url)
            .content(asJsonString(jsondata))
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON))
            //.andDo(print())
            .andExpect(status().is5xxServerError())
            .andExpect(jsonPath("$.errors[0].code").value(500))
            .andExpect(jsonPath("$.errors[0].subErrorCode").value(errorNum))
            .andExpect(jsonPath("$.errors[0].description").value(exceptionMessage))
            .andExpect(jsonPath("$.errors[1].code").value(501))
            .andExpect(jsonPath("$.errors[1].subErrorCode").value(errorNum))
            .andExpect(jsonPath("$.errors[1].description").value(exceptionMessage));
        assertErrors(actions,500,errorNum,exceptionMessage);
    }

    public void testDoubleInterceptorException(String url) throws Exception {
        String id = "foo"; String name = "bar";
        String exceptionMessage = "chenile-http-test";
        int errorNum = 2000;
        JsonData jsondata = new JsonData(id,name);
        jsondata.setMessage(exceptionMessage);
        jsondata.setErrorNum(errorNum);
        ResultActions actions = mvc.perform( MockMvcRequestBuilders
            .post(url)
            .header(JsonInterceptor.SOME_SPECIAL_HEADER, "throw-exception-post-process")
            .content(asJsonString(jsondata))
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON))
            // .andDo(print())
            .andExpect(status().is5xxServerError())
            .andExpect(jsonPath("$.errors[0].code").value(501))
            .andExpect(jsonPath("$.errors[0].subErrorCode").value(errorNum+1))
            .andExpect(jsonPath("$.errors[0].description").value(exceptionMessage +"1"))
            .andExpect(jsonPath("$.errors[1].code").value(500))
            .andExpect(jsonPath("$.errors[1].subErrorCode").value(errorNum))
            .andExpect(jsonPath("$.errors[1].description").value(exceptionMessage));
        assertErrors(actions,501,errorNum + 1,exceptionMessage + "1");
    }
   
    static String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
