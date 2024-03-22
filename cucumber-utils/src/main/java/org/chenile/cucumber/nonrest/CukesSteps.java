package org.chenile.cucumber.nonrest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.List;

import org.chenile.base.exception.ErrorNumException;
import org.chenile.base.response.GenericResponse;
import org.chenile.base.response.ResponseMessage;
import org.chenile.core.context.ChenileExchange;
import org.chenile.core.context.ChenileExchangeBuilder;
import org.chenile.core.entrypoint.ChenileEntryPoint;
import org.chenile.cucumber.CukesContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.expression.MapAccessor;
import org.springframework.expression.Expression;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.test.context.ActiveProfiles;

import cucumber.api.java.Before;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

@ActiveProfiles("unittest")
public class CukesSteps {	
	
	@Autowired ChenileEntryPoint chenileEntryPoint;
	@Autowired private ChenileExchangeBuilder chenileExchangeBuilder;

	private CukesContext context = CukesContext.CONTEXT;
	
	private ChenileExchange makeExchange(String serviceName,String operationName) {
		return chenileExchangeBuilder.makeExchange(serviceName, operationName,null);
	}
	
	@Before
	public void before() {
		context.reset();
	}

	@When("I POST a request to service {string} and operation {string} with payload")
	public void i_POST_a_request_with_payload(String service, String operation, String docString) {
		ChenileExchange exchange = makeExchange(service,operation);
		exchange.setBody(docString);
		chenileEntryPoint.execute(exchange);
		if (exchange.getResponse() != null)
			context.set("response", exchange.getResponse());
		if (exchange.getException() != null)
			context.set("exception", exchange.getException());
		context.set("responseMessages", exchange.getResponseMessages());
		context.set("responseStatusCode", exchange.getHttpResponseStatusCode());
	}

	@Then("the response contains key {string}")
	public void the_response_contains_key(String string) {
		Object response = context.get("response");
		string = "data." + string; // prepend it with the data since this is a generic response
		Object o = evaluate(string,response);
		assertNotNull(o);
	}
	
	private static SpelExpressionParser parser = new SpelExpressionParser();
	protected static Object evaluate(String string, Object root) {
		try {
			Expression expression = parser.parseExpression(string);
			StandardEvaluationContext context = new StandardEvaluationContext(root);
		    context.addPropertyAccessor(new MapAccessor()); // required to access keys of maps
		    return expression.getValue(context);
		}catch(Exception e) {
			return null;
		}
		
	}

	@Then("the response key {string} is {string}")
	public void the_response_key_is(String string, String string2) {
		Object response = context.get("response");
		string = "data." + string; // prepend it with the data since this is a generic response
		Object o = evaluate(string,response);
		assertEquals(o,string2);
	}
	
	@Then("the response does not contain key {string}")
	public void the_response_does_not_contain_key(String string) {
		Object response = context.get("response");
		string = "data." + string; // prepend it with the data since this is a generic response
		Object o = evaluate(string,response);
		assertNull(o);
	}

	@SuppressWarnings("unchecked")
	@Then("a warning must be thrown that says {string}")
	public void a_warning_must_be_thrown_that_says(String string) {
		List<ResponseMessage> warnings = (List<ResponseMessage>)context.get("responseMessages");
		for (ResponseMessage rm: warnings) {
			if (rm.getDescription().equals(string))
				return;
		}
		assertFalse(true);
	}
	
	@Then("the response is null")
	public void the_response_is_null() {
		GenericResponse<?> response = (GenericResponse<?>)context.get("response");
	    assertNull(response.getData());
	}

	@Then("an exception is thrown with message code {int}")
	public void an_exception_is_thrown_with_message_code(Integer int1) {
	   RuntimeException exception = context.get("exception");
	   assertNotNull(exception);
	   assertTrue(exception instanceof ErrorNumException);
	   ErrorNumException ene = (ErrorNumException)exception;
	   assertEquals("Could not find error code " + int1 + " in the exception thrown",
			   int1.intValue(),ene.getSubErrorNum());
	}
	
	@Then("an exception is thrown with param number {int} value {string}")
	public void an_exception_is_thrown_with_param_number_value(Integer int1, String string) {
		RuntimeException exception = context.get("exception");
		assertNotNull(exception);
		assertTrue(exception instanceof ErrorNumException);
		ErrorNumException ene = (ErrorNumException)exception;
		Object param = ene.getParams()[int1-1].toString();
		assertEquals("Could not find param #" + int1 + " with value " + string + " in the error thrown",
				string,param);
	}

	@SuppressWarnings("unchecked")
	@Then("a warning must be thrown with code {int}")
	public void a_warning_must_be_thrown_with_code(Integer int1) {		
		List<ResponseMessage> warnings = (List<ResponseMessage>)context.get("responseMessages");
		for (ResponseMessage rm: warnings) {
			if (rm.getSubErrorCode()== int1)
				return;
		}
		fail("Could not find a warning with error code " + int1);
	}
	
	@SuppressWarnings("unchecked")
	@Then("a warning must be thrown with param number {int} value {string}")
	public void a_warning_must_be_thrown_with_param_number_value(Integer int1, String string) {
		List<ResponseMessage> warnings = (List<ResponseMessage>)context.get("responseMessages");
		for (ResponseMessage rm: warnings) {
			Object[] params = rm.getParams();
			if (params[int1-1].toString().equals(string))
				return;
		}
		assertFalse("Could not find param #" + int1 + " with value " + string + " in warnings", true);
	}
	
}
