package org.chenile.stm.test.actionInfoProvider;

import junit.framework.TestCase;
import org.chenile.stm.STM;
import org.chenile.stm.State;
import org.chenile.stm.exception.STMException;
import org.chenile.stm.impl.STMActionsInfoProvider;
import org.chenile.stm.impl.STMFlowStoreImpl;
import org.chenile.stm.impl.STMImpl;
import org.chenile.stm.impl.XmlFlowReader;
import org.chenile.stm.test.basicflow.*;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class TestSTMInfoProvider extends TestCase{
	
	protected STM<Cart> stm = new STMImpl<Cart>();
	protected STMActionsInfoProvider stmActionsInfoProvider;
	protected static final String FLOW_DEFINITION_FILE = "org/chenile/stm/test/actionInfoProvider/cart.xml";
	
	public void setUp() throws Exception{
		STMFlowStoreImpl stmFlowStore = new STMFlowStoreImpl();
		XmlFlowReader flowReader = new XmlFlowReader(stmFlowStore);
		flowReader.setFilename(FLOW_DEFINITION_FILE);
		stm.setStmFlowStore(stmFlowStore);
		stmActionsInfoProvider = new STMActionsInfoProvider(stmFlowStore);
	}
	
	public void testAllowedActionsAndMetadata() throws Exception{
		Cart cart = new Cart();
		MockSS.userName = "random_user";
		stm.proceed(cart);
		assertEquals(new State("CREATED","cart-flow"),cart.getCurrentState());
		List<Map<String, String>> actions = stmActionsInfoProvider.getAllowedActionsAndMetadata(cart.getCurrentState());
		assertEquals(actions.size(),1);
		assertEquals(actions.get(0).get("allowedAction"),"initiatePayment");

		MockSS.userName = "finance";
		actions = stmActionsInfoProvider.getAllowedActionsAndMetadata(cart.getCurrentState());
		assertEquals(actions.size(),2);
		assertEquals(actions.get(0).get("allowedAction"),"initiatePayment");
		assertEquals(actions.get(1).get("allowedAction"),"close");
		assertEquals(actions.get(1).get("xxx"),"yyy");
		assertEquals(actions.get(1).get("acls"),"finance-user");

		MockSS.userName = "random_user";
		stm.proceed(cart,"initiatePayment",null);
		assertEquals(new State("PAYMENT_INITIATED","cart-flow"),cart.getCurrentState());
		actions = stmActionsInfoProvider.getAllowedActionsAndMetadata(cart.getCurrentState());
		assertEquals(actions.size(),1);
		assertEquals(actions.get(0).get("allowedAction"),"confirmPayment");

		stm.proceed(cart,"confirmPayment",null);
		assertEquals(new State("PAYMENT_CONFIRMED","cart-flow"),cart.getCurrentState());
		actions = stmActionsInfoProvider.getAllowedActionsAndMetadata(cart.getCurrentState());
		assertEquals(actions.size(),0);
		MockSS.userName = "finance";
		actions = stmActionsInfoProvider.getAllowedActionsAndMetadata(cart.getCurrentState());
		assertEquals(actions.size(),1);
		assertEquals(actions.get(0).get("allowedAction"),"close");
	}

	public void testGetStatesAllowedForRandomUser(){
		MockSS.userName = "random_user";
		List<String> states = stmActionsInfoProvider.getStatesAllowedForCurrentUser();
		assertEquals(states.size(),2);
		assertTrue(states.contains("PAYMENT_INITIATED"));
		assertTrue(states.contains("CREATED"));
	}

	public void testGetStatesAllowedForFinanceUser(){
		MockSS.userName = "finance";
		List<String> states = stmActionsInfoProvider.getStatesAllowedForCurrentUser();
		assertEquals(states.size(),3);
		assertTrue(states.contains("PAYMENT_CONFIRMED"));
		assertTrue(states.contains("PAYMENT_INITIATED"));
		assertTrue(states.contains("CREATED"));
	}

}