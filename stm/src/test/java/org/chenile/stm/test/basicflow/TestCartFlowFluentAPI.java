package org.chenile.stm.test.basicflow;

import java.util.Arrays;
import java.util.List;

import org.chenile.stm.STM;
import org.chenile.stm.STMFlowStore;
import org.chenile.stm.State;
import org.chenile.stm.action.scriptsupport.IfAction;
import org.chenile.stm.exception.STMException;
import org.chenile.stm.impl.FluentFlowReader;
import org.chenile.stm.impl.STMFlowStoreImpl;
import org.chenile.stm.impl.STMImpl;

import junit.framework.TestCase;

public class TestCartFlowFluentAPI extends TestCase{
	
	protected STM<Cart> stm = new STMImpl<Cart>();
	public void setUp() throws Exception{
		stm.setStmFlowStore(setupCartWorkflow());
	}
	
	private STMFlowStore setupCartWorkflow() throws STMException{
		STMFlowStoreImpl stmFlowStore = new STMFlowStoreImpl();
		new FluentFlowReader(stmFlowStore).
			newFlow("cart-flow").
			makeDefault().
			securityStrategy(new MockSecurityStrategy()).
			entryAction(new EntryAction()).
			exitAction(new ExitAction()).
			manualState("CREATED",true).
				on("close").
					transitionTo("CLOSED","cart-flow").
					makeInvokableOnlyFromStm().
					state().
				on("addItem").
					transitionAction(new AddItem()).
					state().
				on("userLogin").
					transitionAction(new UserLogin()).
					acl("USER_MUST_BE_ABLE_TO_LOGIN,USER_CAN_ACCESS_SYSTEM").
					state().
				on("initiatePayment").
					transitionAction(new InitiatePayment()).
					transitionTo("PAYMENT_INITIATED").
					state().
				flow().
			manualState("PAYMENT_INITIATED").
				on("confirmPayment").
					transitionAction(new ConfirmPayment()).
					transitionTo("TEST_STATE").
					state().
				flow().
			autoState("TEST_STATE").
				component(new IfAction<>()).
				property("condition","testObj==1").
				property("then","success").
				property("else","failure").
				on("success").
					transitionTo("PAYMENT_CONFIRMED").
					state().
				on("failure").
					transitionTo("PAYMENT_INITIATED").
					state().
				flow().
			manualState("PAYMENT_CONFIRMED").
				flow().
			manualState("CLOSED");
		return stmFlowStore;
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

	
}