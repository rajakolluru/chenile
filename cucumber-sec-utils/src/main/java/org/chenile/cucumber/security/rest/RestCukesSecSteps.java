package org.chenile.cucumber.security.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import cucumber.api.java.Before;
import cucumber.api.java.en.When;
import org.chenile.cucumber.CukesContext;
import org.chenile.security.KeycloakConnectionDetails;
import org.chenile.security.test.BaseSecurityTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.HashMap;
import java.util.Map;

import static org.chenile.security.test.BaseSecurityTest.getToken;

/**
 * Cucumber steps to facilitate injection of security tokens into the MVC request header.<br/>
 * See the methods  below for the precise Gherkin language that has been created.
 */
@ActiveProfiles("unittest")
@AutoConfigureMockMvc
public class RestCukesSecSteps {
    @Autowired
    private MockMvc mvc;
    @Autowired private KeycloakConnectionDetails connectionDetails;
    protected final ObjectMapper objectMapper = new ObjectMapper();

    CukesContext context = CukesContext.CONTEXT;

    @Before public void before(){
        connectionDetails.host = BaseSecurityTest.getUrl();
        connectionDetails.httpPort = BaseSecurityTest.getHttpPort();
    }
    @When("I construct a REST request with authorization header in realm {string} for user {string} and password {string}")
    public void i_construct_an_authorized_REST_request_in_realm_for_user_and_password
            (String realm, String user, String password) {
        Map<String, String> headers = context.get("headers");
        if (headers == null) {
            headers = new HashMap<>();
            context.set("headers", headers);
        }
        String headerName = "Authorization";
        String headerValue = "Bearer " + getToken(realm,user,password);
        headers.put(headerName, headerValue);
    }
}