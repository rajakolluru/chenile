package org.chenile.core.test;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.chenile.base.exception.ServerException;
import org.chenile.core.context.ChenileExchange;
import org.chenile.core.entrypoint.ChenileEntryPoint;
import org.chenile.core.model.ChenileConfiguration;
import org.chenile.core.model.ChenileServiceDefinition;
import org.chenile.core.model.OperationDefinition;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes={SpringTestConfig.class})
public class TestChenileCore {

	@Autowired private ChenileEntryPoint chenileEntryPoint;
	@Autowired private ChenileConfiguration chenileConfiguration;
	
	@Test
	public void testInterception() throws Exception{
		ChenileExchange exchange = makeExchange("mockService","mockMethod");
		exchange.setBody(new ArrayList<String>());
		chenileEntryPoint.execute(exchange);
		List<String> expected = new ArrayList<String>();
		expected.add("first");expected.add("second");expected.add("third");expected.add("fourth"); expected.add("fifth");
		expected.add("actual");
		expected.add("sixth");expected.add("seventh");expected.add("eighth");expected.add("ninth"); expected.add("tenth");
		assertEquals("interception order calling order does not match expected",expected, exchange.getResponse());
	}
	
	private ChenileServiceDefinition findService(String serviceName) {
		return chenileConfiguration.getServices().get(serviceName);
	}
	
	private OperationDefinition findOperationInService(ChenileServiceDefinition serviceDefinition, String opName) {
		for (OperationDefinition od: serviceDefinition.getOperations()) {
			if (od.getName().equals(opName)){
				return od;
			}
		}
		return null;
	}
	
	private ChenileExchange makeExchange(String serviceName, String opName) {
		ChenileServiceDefinition serviceDefinition = findService(serviceName);
		OperationDefinition operationDefinition = findOperationInService(serviceDefinition, opName);
		ChenileExchange exchange = new ChenileExchange();
		exchange.setServiceDefinition(serviceDefinition);
		exchange.setOperationDefinition(operationDefinition);
		return exchange;
	}
	
	@Test public void testServiceInvokerWithHeaderStringArgument() throws Exception{
		ChenileExchange exchange = makeExchange("mockService","s1");
		exchange.setHeader("id","myid");
		chenileEntryPoint.execute(exchange);
		assertEquals("Expected does not match the actual return value","mockmyid",exchange.getResponse());
	}
	
	@Test public void testServiceInvokerWithHeaderIntArgument() throws Exception{
		ChenileExchange exchange = makeExchange("mockService","s2");
		exchange.setHeader("id",123);
		chenileEntryPoint.execute(exchange);
		assertEquals("Expected does not match the actual return value","mockint123",exchange.getResponse());
	}
	
	@Test public void testServiceInvokerWithHeaderIntArgumentPassedAsString() throws Exception{
		ChenileExchange exchange = makeExchange("mockService","s2");
		exchange.setHeader("id","123");
		chenileEntryPoint.execute(exchange);
		assertEquals("Expected does not match the actual return value","mockint123",exchange.getResponse());
	}
	
	@Test public void testServiceInvokerWithHeaderIntArgumentPassedAndIntReturned() throws Exception{
		ChenileExchange exchange = makeExchange("mockService","s3");
		exchange.setHeader("id",7);
		chenileEntryPoint.execute(exchange);
		assertEquals("Expected does not match the actual return value",50,exchange.getResponse());
	}
	
	@Test public void testServiceInvokerWithHeaderStringArgumentPassedForPrimitiveIntAndIntReturned() throws Exception{
		ChenileExchange exchange = makeExchange("mockService","s3");
		exchange.setHeader("id","7");
		chenileEntryPoint.execute(exchange);
		assertEquals("Expected does not match the actual return value",50,exchange.getResponse());
	}
	
	@Test public void testServiceInvokerWithHeaderBooleanArgumentPassedAndBooleanReturned() throws Exception{
		ChenileExchange exchange = makeExchange("mockService","s4");
		exchange.setHeader("flag",true);
		chenileEntryPoint.execute(exchange);
		assertEquals("Expected does not match the actual return value",false,exchange.getResponse());
	}
	
	@Test public void testServiceInvokerWithHeaderStringArgumentPassedForPrimitiveBooleanAndBooleanReturned() throws Exception{
		ChenileExchange exchange = makeExchange("mockService","s4");
		exchange.setHeader("flag","true");
		chenileEntryPoint.execute(exchange);
		assertEquals("Expected does not match the actual return value",false,exchange.getResponse());
	}
	
	@Test public void testServiceInvokerWithExceptionHandled() {
		int exceptionNum = 1089;
		ChenileExchange exchange = makeExchange("mockService","s5");
		exchange.setHeader("exceptionNum",exceptionNum);
		try {
			chenileEntryPoint.execute(exchange);
			assertTrue("Exception hidden by Chenile Entry Point", false);
		}catch(Throwable e) {
			assertEquals("Exception returned must be of type ServerException", 
					e.getClass(),ServerException.class);
			ServerException e1 = (ServerException)e;
			assertEquals("Sub error code of exception does not match",exceptionNum,e1.getSubErrorNum());
		}
	}
	
	@Test public void testBodyTypeSelector() {
		ChenileExchange exchange = makeExchange("mockService","s6");
		
		exchange.setHeader("eventId","e1");
		exchange.setBody("{\"s1\": \"xyz\"}");
		
		chenileEntryPoint.execute(exchange);
		assertTrue("Response from mockService.s6(e1) must be castable to E1",exchange.getResponse() instanceof E1);
		E1 e1 = (E1)exchange.getResponse();
		assertEquals("xyz",e1.getS1());
		
		exchange.setHeader("eventId","e2");
		exchange.setBody("{\"s2\": \"abc\"}");
		
		chenileEntryPoint.execute(exchange);
		assertTrue("Response from mockService.s6(e2) must be castable to E2",exchange.getResponse() instanceof E2);
		E2 e2 = (E2)exchange.getResponse();
		assertEquals("abc",e2.getS2());
	}
	
}
