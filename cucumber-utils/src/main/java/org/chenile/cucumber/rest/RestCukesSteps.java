package org.chenile.cucumber.rest;

import static org.chenile.testutils.SpringMvcUtils.assertErrors;
import static org.chenile.testutils.SpringMvcUtils.assertWarnings;
import static org.junit.Assert.fail;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.jayway.jsonpath.JsonPath;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import org.apache.commons.text.StringSubstitutor;
import org.chenile.base.response.GenericResponse;
import org.chenile.base.response.ResponseMessage;
import org.chenile.cucumber.CukesContext;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import static org.hamcrest.Matchers.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;

import cucumber.api.java.Before;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import org.springframework.util.StringUtils;

/**
 * Cucumber steps to facilitate running tests using Spring MOCK MVC.<br/>
 * These steps are generic since they leverage the scripting language and JSON PATH etc. that are
 * supported by Spring MVC.<br/>
 * See the methods  below for the precise Gherkin language that has been created.
 */
@ActiveProfiles("unittest")
@AutoConfigureMockMvc
public class RestCukesSteps {
    @Autowired
    private MockMvc mvc;
    private ObjectMapper objectMapper = new ObjectMapper();

    CukesContext context = CukesContext.CONTEXT;
    /**
     * Uses a variable to store the results of a scenario so that it can be used in the next scenario.<br/>
     * For example, if we create an entity and want to retrieve the same entity by ID, then we can store
     * the ID as a variable in the varMap. We can use the ID to retrieve the object back in the next scenario<br/>
     * varMap spans scenarios and hence needs to be stored outside the context.<br/>
     */
    private static Map<String, String> varMap = new HashMap<String, String>();

    @Before
    public void before() {
        context.reset();
    }
    // the request construction using various HTTP Methods

    @When("I construct a REST request with header {string} and value {string}")
    public void i_construct_a_REST_request_with_header_and_value(String headerName,
                                                                 String headerValue) throws Exception {
        Map<String, String> headers = context.get("headers");
        if (headers == null) {
            headers = new HashMap<>();
            context.set("headers", headers);
        }
        headers.put(headerName, headerValue);
    }

    @When("I POST a REST request to URL {string} with payload")
    public void i_POST_REST_request_with_payload(String url, String docString) throws Exception {
        Map<String, String> headers = context.get("headers");
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .post(substituteVariables(url))
                .content(substituteVariables(docString))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);
        if (headers != null) {
            for (Entry<String, String> entry : headers.entrySet()) {
                request.header(substituteVariables(entry.getKey()), substituteVariables(entry.getValue()));
            }
        }
        ResultActions actions = mvc.perform(request)
                .andDo(print());
        context.set("actions", actions);
    }

    @When("I GET a REST request to URL {string}")
    public void i_GET_a_REST_request_to_URL(String url) throws Exception {
        Map<String, String> headers = context.get("headers");
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .get(substituteVariables(url))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);
        if (headers != null) {
            for (Entry<String, String> entry : headers.entrySet()) {
                request.header(substituteVariables(entry.getKey()), substituteVariables(entry.getValue()));
            }
        }
        ResultActions actions = mvc.perform(request)
                .andDo(print());
        context.set("actions", actions);
    }

    @When("I PUT a REST request to URL {string} with payload")
    public void i_PUT_a_REST_request_to_URL(String url, String docString) throws Exception {
        Map<String, String> headers = context.get("headers");
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .put(substituteVariables(url))
                .content(substituteVariables(docString))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);
        if (headers != null) {
            for (Entry<String, String> entry : headers.entrySet()) {
                request.header(substituteVariables(entry.getKey()), substituteVariables(entry.getValue()));
            }
        }
        ResultActions actions = mvc.perform(request)
                .andDo(print());
        context.set("actions", actions);
    }

    /**
     * Check status codes.
     *
     * @param statusCode check if the status code matches the passed one.
     * @throws Exception in case there is an error in interacting with the REST service.
     */

    @Then("the http status code is {int}")
    public void the_http_status_code_is(Integer statusCode) throws Exception {
        ResultActions actions = (ResultActions) context.get("actions");
        actions.andExpect(status().is(statusCode));
    }

    // Check for success

    /**
     * Check if the success in {@link GenericResponse} is true
     *
     * @throws Exception if there is an error in retrieving the results
     */
    @Then("success is true")
    public void success_is_true() throws Exception {
        ResultActions actions = (ResultActions) context.get("actions");
        actions.andExpect(jsonPath("$.success").value(true));
    }

    @Then("success is false")
    public void success_is_false() throws Exception {
        ResultActions actions = (ResultActions) context.get("actions");
        actions.andExpect(jsonPath("$.success").value(false));
    }

    // Check for keys with their values

    @Then("the REST response is null")
    public void the_REST_response_is_null() throws Exception {
        ResultActions actions = (ResultActions) context.get("actions");
        actions.andExpect(jsonPath("$.payload").doesNotExist());
    }

    @Then("the REST response contains key {string}")
    public void the_REST_response_contains_key(String string) throws Exception {
        ResultActions response = (ResultActions) context.get("actions");
        response.andExpect(jsonPath("$.payload." + string).exists());
    }

    @Then("the REST response key {string} is {string}")
    public void the_REST_response_key_is(String key, String value) throws Exception {
        ResultActions response = (ResultActions) context.get("actions");
        response.andExpect(jsonPath("$.payload." + key).
                value(substituteVariables(value)));
    }


    @And("the REST response key {string} contains string {string}")
    public void theRESTResponseKeyContainsString(String key, String value) throws Exception {
        ResultActions response = (ResultActions) context.get("actions");
        response.andExpect(jsonPath("$.payload." + key).value(containsString(
                substituteVariables(value)
        )));
    }

    @Then("the REST response does not contain key {string}")
    public void the_REST_response_does_not_contain_key(String key) throws Exception {
        ResultActions response = (ResultActions) context.get("actions");
        response.andExpect(jsonPath("$.payload." + key).doesNotExist());
    }

    /**
     *  check if the number of errors returned match the expected value
     */
    @Then("the error array size is {int}")
    public void the_error_array_size_is(Integer size) throws Exception {
        ResultActions response = (ResultActions) context.get("actions");
        response.andExpect(jsonPath("$.errors.length()").value(size));
    }

    @Then("the top level code is {int}")
    public void the_top_level_code_is(Integer code) throws Exception {
        ResultActions response = (ResultActions) context.get("actions");
        response.andExpect(jsonPath("$.code").value(code));
    }

    @Then("the top level subErrorCode is {int}")
    public void the_top_level_subErrorCode_is(Integer code) throws Exception {
        ResultActions response = (ResultActions) context.get("actions");
        response.andExpect(jsonPath("$.subErrorCode").value(code));
    }

    @Then("the top level description is {string}")
    public void the_top_level_description_is(String description) throws Exception {
        ResultActions response = (ResultActions) context.get("actions");
        response.andExpect(jsonPath("$.description").
                value(substituteVariables(description)));
    }

    @Then("a REST warning must be thrown that says {string} with code {int}")
    public void a_REST_warning_must_be_thrown_that_says_with_code(String warningMessage, Integer errorNum) throws Exception {
        warningMessage = substituteVariables(warningMessage);
        GenericResponse<?> response = extractGenericResponse();
        for (ResponseMessage m : response.getErrors()) {
            if (m.getSubErrorCode() == errorNum && m.getDescription().equals(warningMessage)) {
                return;
            }
        }
        fail("Unable to find " + warningMessage + " in warnings");
    }

    @Then("a REST warning must be thrown that says {string} with code {int} and http status {int}")
    public void a_REST_warning_must_be_thrown_that_says_with_code_and_http_status
            (String warningMessage, Integer subErrorCode, Integer httpStatus) throws Exception {
        warningMessage = substituteVariables(warningMessage);
        GenericResponse<?> response = extractGenericResponse();
        for (ResponseMessage m : response.getErrors()) {
            if (m.getSubErrorCode() == subErrorCode &&
                    m.getDescription().equals(warningMessage) &&
                    m.getCode() == httpStatus) {
                return;
            }
        }
        fail("Unable to find " + warningMessage + " in warnings");
    }

    @Then("a REST warning must be thrown with code {int}")
    public void a_REST_warning_must_be_thrown_with_code(Integer errorNum) throws Exception {
        GenericResponse<?> response = extractGenericResponse();
        for (ResponseMessage m : response.getErrors()) {
            int code = m.getSubErrorCode();
            if (code == errorNum)
                return;
        }
        fail("Unable to find " + errorNum + " in warnings");
        ResultActions actions = (ResultActions) context.get("actions");
        assertWarnings(actions, errorNum, null);
    }

    @Then("a REST warning must be thrown with param number {int} value {string}")
    public void a_REST_warning_must_be_thrown_with_param_number_value
            (Integer pos, String message) throws Exception {
        message = substituteVariables(message);
        GenericResponse<?> response = extractGenericResponse();
        for (ResponseMessage m : response.getErrors()) {
            Object[] params = m.getParams();
            if (params != null && params.length >= pos &&
                    message.equals(params[pos - 1].toString()))
                return;
        }
        fail("Unable to find " + message + " at position " + pos + " in warnings");
    }

    @Then("a REST warning must be thrown that has field {string}")
    public void a_warning_must_be_thrown_that_has_field(String fieldValue) throws Exception {
        fieldValue = substituteVariables(fieldValue);
        GenericResponse<?> response = extractGenericResponse();
        for (ResponseMessage m : response.getErrors()) {
            if (fieldValue.equals(m.getField())) return;
        }
        fail("Unable to find " + fieldValue + " in warnings");
    }

    // Exception processing
    @Then("a REST exception is thrown with status {int} and message code {int}")
    public void a_REST_exception_is_thrown_with_status_and_message_code
    (Integer errCode, Integer subErrCode) throws Exception {
        ResultActions actions = (ResultActions) context.get("actions");
        assertErrors(actions, errCode, subErrCode, null);
    }

    @Then("a REST exception is thrown with message code {int}")
    public void a_REST_exception_is_thrown_with_message_code(Integer errorCode) throws Exception {
        ResultActions actions = (ResultActions) context.get("actions");
        assertErrors(actions, 400, errorCode, null);
    }

    @Then("a REST exception is thrown with param number {int} value {string}")
    public void a_REST_exception_is_thrown_with_param_number_value(Integer pos, String message)
            throws Exception {
        message = substituteVariables(message);
        ResultActions actions = (ResultActions) context.get("actions");
        actions.andExpect(jsonPath("$.errors[0].params[" + (pos - 1) + "]").value(message));
    }

    @Then("a REST exception is thrown with message {string}")
    public void a_REST_exception_is_thrown_with_message(String exceptionMessage)
            throws Exception {
        exceptionMessage = substituteVariables(exceptionMessage);
        ResultActions actions = (ResultActions) context.get("actions");
        actions.andExpect(jsonPath("$.description").value(exceptionMessage));
    }


    // extract the response for checking purposes
    private GenericResponse<?> extractGenericResponse() throws Exception {
        String contentAsString = extractStringFromResponse();
        return objectMapper.readValue(contentAsString, GenericResponse.class);
    }

    private String extractStringFromResponse() throws Exception {
        ResultActions actions = (ResultActions) context.get("actions");
        MvcResult result = actions.andReturn();
        return result.getResponse().getContentAsString();
    }

    @Then("store {string} from  response to {string}")
    public void store_from_response_to(String expression, String varName) throws Exception {
        String content = extractStringFromResponse();
        String varValue = JsonPath.parse(content).read(expression, String.class);
        // store this value against the name in the context
        varMap.put(varName, varValue);
    }

    private String substituteVariables(String s) {
        if (varMap == null || varMap.isEmpty()) return s;
        StringSubstitutor sub = new StringSubstitutor(varMap);
        return sub.replace(s);
    }

    @Given("that {string} equals {string}")
    public void that_varName_equals_varValue(String varName, String varValue) {
        varMap.put(varName, varValue);
    }
}

