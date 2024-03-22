package org.chenile.stm.test.basicflow;

import java.util.Arrays;
import java.util.List;

import org.chenile.stm.STM;
import org.chenile.stm.State;
import org.chenile.stm.exception.STMException;
import org.chenile.stm.impl.STMFlowStoreImpl;
import org.chenile.stm.impl.STMImpl;
import org.chenile.stm.impl.XmlFlowReader;

import junit.framework.TestCase;

public class TestCartFlow extends TestCase{
	
	protected STM<Cart> stm = new STMImpl<Cart>();
	protected STM<Cart> specialFlow = new STMImpl<Cart>();
	protected static final String FLOW_DEFINITION_FILE = "org/chenile/stm/test/basicflow/cart.xml";
	protected static final String FLOW_DEFINITION_FILE1 = "org/chenile/stm/test/basicflow/cart-test1.xml";
	
	public void setUp() throws Exception{
		STMFlowStoreImpl stmFlowStore = new STMFlowStoreImpl();
		XmlFlowReader flowReader = new XmlFlowReader(stmFlowStore);
		flowReader.setFilename(FLOW_DEFINITION_FILE);
		stm.setStmFlowStore(stmFlowStore);	
		
		STMFlowStoreImpl stmFlowStore1 = new STMFlowStoreImpl();
		XmlFlowReader flowReader1 = new XmlFlowReader(stmFlowStore1);
		flowReader1.setFilename(FLOW_DEFINITION_FILE1);
		specialFlow.setStmFlowStore(stmFlowStore1);		
	}
	
	public void testSTM() throws Exception{
		
		Cart cart = new Cart(); 
		stm.proceed(cart);
		assertEquals(new State("CREATED","cart-flow"),cart.getCurrentState());
		
		Item item = new Item("1234",2,20);
		stm.proceed(cart,"addItem",item);
		assertEquals(2,cart.getItem(0).getQuantity());
		assertEquals(new State("CREATED","cart-flow"),cart.getCurrentState());
		
		try{
			stm.proceed(cart,"userLogin","user1");
		}catch(STMException e){
			assertEquals(551,e.getMessageId());
		}
		// assertEquals("user1",cart.getUserId());
		
		stm.proceed(cart,"initiatePayment",new Payment("amex"));
		assertEquals("amex",cart.getPayment().getPayee());
		assertEquals(new State("PAYMENT_INITIATED","cart-flow"),cart.getCurrentState());
		
		cart.setTestObj(1);
		stm.proceed(cart,"confirmPayment","confirm777");
		assertEquals("confirm777",cart.getPayment().getConfirmationId());
		assertEquals(new State("PAYMENT_CONFIRMED","cart-flow"),cart.getCurrentState());
		
		stm.proceed(cart);// redundant call should do nothing.
		
		try{
			stm.proceed(cart,"addItem",item);
		}catch(STMException e){
			assertTrue(e.getMessage().contains("addItem"));
			assertEquals(STMException.INVALID_EVENTID,e.getMessageId());
		}
		
		// Expected calling sequence as documented in the log.
		List<String> log = cart.getLog();
		// System.out.println(log);
	

		String[] expectedLog = {
				EntryAction.LOGMESSAGE + ":CREATED",
				ExitAction.LOGMESSAGE + ":CREATED",
				AddItem.LOGMESSAGE,
				EntryAction.LOGMESSAGE + ":CREATED",
				ExitAction.LOGMESSAGE + ":CREATED",
				InitiatePayment.LOGMESSAGE,
				EntryAction.LOGMESSAGE + ":PAYMENT_INITIATED",
				ExitAction.LOGMESSAGE + ":PAYMENT_INITIATED",
				ConfirmPayment.LOGMESSAGE,
				EntryAction.LOGMESSAGE + ":PAYMENT_CONFIRMED",
				ExitAction.LOGMESSAGE + ":PAYMENT_CONFIRMED"
		};
		
		assertEquals(Arrays.asList(expectedLog), log);
	}
	
	public void testSTMWithSecurityAllowed() throws Exception{
		
		Cart cart = new Cart();
		stm.proceed(cart);
		assertEquals(new State("CREATED","cart-flow"),cart.getCurrentState());
		
		Item item = new Item("1234",2,20);
		stm.proceed(cart,"addItem",item);
		assertEquals(2,cart.getItem(0).getQuantity());
		
		try {
			MockSecurityStrategy.tl.set("InValidUser");
			stm.proceed(cart,"userLogin","user1");
			assertTrue("Cannot succeed with invalid user", false);
		}catch(STMException e) {
			assertEquals("Must throw 551 security exception for invalid user",551,e.getMessageId());
		}
		MockSecurityStrategy.tl.set("ValidUser");
		stm.proceed(cart,"userLogin","user1");
		
		assertEquals("user1",cart.getUserId());
		
		stm.proceed(cart,"initiatePayment",new Payment("amex"));
		assertEquals("amex",cart.getPayment().getPayee());
		assertEquals(new State("PAYMENT_INITIATED","cart-flow"),cart.getCurrentState());
		
		cart.setTestObj(1);
		stm.proceed(cart,"confirmPayment","confirm777");
		assertEquals("confirm777",cart.getPayment().getConfirmationId());
		assertEquals(new State("PAYMENT_CONFIRMED","cart-flow"),cart.getCurrentState());
		
		stm.proceed(cart);// redundant call should do nothing.
		
		try{
			stm.proceed(cart,"addItem",item);
		}catch(STMException e){
			assertTrue(e.getMessage().contains("addItem"));
			assertEquals(STMException.INVALID_EVENTID,e.getMessageId());
		}
		
		// Expected calling sequence as documented in the log.
		List<String> log = cart.getLog();
		// System.out.println(log);
	

		String[] expectedLog = {
				EntryAction.LOGMESSAGE + ":CREATED",
				ExitAction.LOGMESSAGE + ":CREATED",
				AddItem.LOGMESSAGE,
				EntryAction.LOGMESSAGE + ":CREATED",
				ExitAction.LOGMESSAGE + ":CREATED",
				UserLogin.LOGMESSAGE,
				EntryAction.LOGMESSAGE + ":CREATED",
				ExitAction.LOGMESSAGE + ":CREATED",
				InitiatePayment.LOGMESSAGE,
				EntryAction.LOGMESSAGE + ":PAYMENT_INITIATED",
				ExitAction.LOGMESSAGE + ":PAYMENT_INITIATED",
				ConfirmPayment.LOGMESSAGE,
				EntryAction.LOGMESSAGE + ":PAYMENT_CONFIRMED",
				ExitAction.LOGMESSAGE + ":PAYMENT_CONFIRMED"
		};
		
		assertEquals(Arrays.asList(expectedLog), log);
	}
	
	public void testSTMWithInvokedFromOtherStm() throws Exception{
		
		Cart cart = new Cart();
		stm.proceed(cart);
		assertEquals(new State("CREATED","cart-flow"),cart.getCurrentState());
		try{
			stm.proceed(cart,"close",null);
		}catch(STMException e){
			assertEquals(551,e.getMessageId());
		}
		
		
		// Expected calling sequence as documented in the log.
		List<String> log = cart.getLog();
		// System.out.println(log);
	

		String[] expectedLog = {
				EntryAction.LOGMESSAGE + ":CREATED",
				ExitAction.LOGMESSAGE + ":CREATED",
				CloseCart.LOGMESSAGE,
				EntryAction.LOGMESSAGE + ":CLOSED",
				ExitAction.LOGMESSAGE + ":CLOSED"
		};
		
		assertEquals(Arrays.asList(expectedLog), log);
	}
	
	public static class TransitionParams {
		public STM<Cart> stm;
		public Cart cart;
	}
	
	public void testSTMWithInvokedFromOtherStm1() throws Exception{
		
		Cart cart = new Cart();
		stm.proceed(cart);
		assertEquals(new State("CREATED","cart-flow"),cart.getCurrentState());
		
		Cart specialCart = new Cart();
		specialFlow.proceed(specialCart);
		assertEquals(new State("INIT","special-flow"),specialCart.getCurrentState());
		
		TransitionParams tp = new TransitionParams();
		tp.stm = stm; tp.cart = cart;
		specialFlow.proceed(specialCart,"end",tp);	
		assertEquals(new State("CLOSED","cart-flow"),tp.cart.getCurrentState());
		
		// Expected calling sequence as documented in the log.
		List<String> log = specialCart.getLog();
		// System.out.println(log);
	

		String[] expectedLog = {
				EntryAction.LOGMESSAGE + ":INIT",
				ExitAction.LOGMESSAGE + ":INIT",
				EndAction.LOGMESSAGE,
				EntryAction.LOGMESSAGE + ":END",
				ExitAction.LOGMESSAGE + ":END"
		};
		
		assertEquals(Arrays.asList(expectedLog), log);
		
		log = tp.cart.getLog();
	

		String[] expectedLog1 = {
				EntryAction.LOGMESSAGE + ":CREATED",
				ExitAction.LOGMESSAGE + ":CREATED",
				CloseCart.LOGMESSAGE,
				EntryAction.LOGMESSAGE + ":CLOSED",
				ExitAction.LOGMESSAGE + ":CLOSED"
		};
		
		assertEquals(Arrays.asList(expectedLog1), log);
	}
}