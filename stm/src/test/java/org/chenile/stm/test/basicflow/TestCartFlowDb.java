package org.chenile.stm.test.basicflow;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import junit.framework.TestCase;

import org.chenile.stm.STM;
import org.chenile.stm.State;
import org.chenile.stm.StmDataProvider;
import org.chenile.stm.dto.FlowDescriptionDTO;
import org.chenile.stm.dto.StateAttributesDTO;
import org.chenile.stm.dto.StateDescriptionDTO;
import org.chenile.stm.dto.StatesDescriptionDTO;
import org.chenile.stm.dto.TransitionDescriptionDTO;
import org.chenile.stm.exception.STMException;
import org.chenile.stm.impl.DataFlowReader;
import org.chenile.stm.impl.STMFlowStoreImpl;
import org.chenile.stm.impl.STMImpl;

public class TestCartFlowDb extends TestCase{

	private STM<Cart> stm = new STMImpl<Cart>();

	private STM<Cart> specialFlow = new STMImpl<Cart>();

	public void setUp() throws Exception{

		StmDataProvider dataProvider=new StmDataProvider() {

			@Override
			public StatesDescriptionDTO fillStmData(String flowId) {
				StatesDescriptionDTO dto=new StatesDescriptionDTO();

				List<FlowDescriptionDTO> flowList=new ArrayList<>();
				FlowDescriptionDTO flow=new FlowDescriptionDTO();
				flow.setEntryAction("org.chenile.stm.test.basicflow.EntryAction");
				flow.setExitAction("org.chenile.stm.test.basicflow.ExitAction");
				flow.setFlowName("cart-flow");
				flow.setIsDefault(true);
				flow.setSkipEntryExitActionsForAutoStates(true);
				flow.setStmSecurityStrategy("org.chenile.stm.test.basicflow.MockSecurityStrategy");
				List<StateDescriptionDTO> listState=new ArrayList<StateDescriptionDTO>();
				StateDescriptionDTO state=new StateDescriptionDTO();
				state.setStateName("CREATED");
				state.setStateType("manual-state");
				state.setIsFinalState(false);
				state.setIsInitialState(true);

				List<TransitionDescriptionDTO> listTransitionDto=new ArrayList<TransitionDescriptionDTO>();

				TransitionDescriptionDTO transitionDto=new TransitionDescriptionDTO();
				transitionDto.setEventName("addItem");
				transitionDto.setTransitionAction("org.chenile.stm.test.basicflow.AddItem");
				transitionDto.setIsRetrievalTransition(false);
				listTransitionDto.add(transitionDto);

				transitionDto=new TransitionDescriptionDTO();
				transitionDto.setEventName("close");
				transitionDto.setTransitionAction("org.chenile.stm.test.basicflow.CloseCart");
				transitionDto.setIsRetrievalTransition(false);
				transitionDto.setNewStateId("CLOSED");
				transitionDto.setIsInvokableOnlyFromStm(true);
				listTransitionDto.add(transitionDto);

				transitionDto=new TransitionDescriptionDTO();
				transitionDto.setEventName("userLogin");
				transitionDto.setTransitionAction("org.chenile.stm.test.basicflow.UserLogin");
				transitionDto.setIsRetrievalTransition(false);
				transitionDto.setAcls("USER_MUST_BE_ABLE_TO_LOGIN");
				listTransitionDto.add(transitionDto);

				transitionDto=new TransitionDescriptionDTO();
				transitionDto.setEventName("initiatePayment");
				transitionDto.setTransitionAction("org.chenile.stm.test.basicflow.InitiatePayment");
				transitionDto.setIsRetrievalTransition(false);
				transitionDto.setNewStateId("PAYMENT_INITIATED");
				listTransitionDto.add(transitionDto);
				state.setTransitionDescriptions(listTransitionDto);
				listState.add(state);


				state=new StateDescriptionDTO();
				state.setStateName("TEST_STATE");
				state.setStateType("if");
				List<StateAttributesDTO> stateAttributes=new ArrayList<>();
				StateAttributesDTO stateAttributesDTO=new StateAttributesDTO();
				stateAttributesDTO.setKey("condition");
				stateAttributesDTO.setValue("testObj==1");
				stateAttributes.add(stateAttributesDTO);
				stateAttributesDTO=new StateAttributesDTO();
				stateAttributesDTO.setKey("then");
				stateAttributesDTO.setValue("xyz");
				stateAttributes.add(stateAttributesDTO);
				stateAttributesDTO=new StateAttributesDTO();
				stateAttributesDTO.setKey("else");
				stateAttributesDTO.setValue("abc");
				stateAttributes.add(stateAttributesDTO);
				state.setStateAttributes(stateAttributes);
				state.setIsFinalState(false);
				state.setIsInitialState(false);

				listTransitionDto=new ArrayList<TransitionDescriptionDTO>();
				transitionDto=new TransitionDescriptionDTO();
				transitionDto.setEventName("xyz");
				transitionDto.setNewStateId("PAYMENT_CONFIRMED");
				transitionDto.setIsRetrievalTransition(false);
				listTransitionDto.add(transitionDto);


				transitionDto=new TransitionDescriptionDTO();
				transitionDto.setEventName("abc");
				transitionDto.setNewStateId("PAYMENT_INITIATED");
				transitionDto.setIsRetrievalTransition(false);
				listTransitionDto.add(transitionDto);

				state.setTransitionDescriptions(listTransitionDto);
				listState.add(state);

				state=new StateDescriptionDTO();
				state.setStateName("PAYMENT_INITIATED");
				state.setStateType("manual-state");
				state.setIsFinalState(false);
				state.setIsInitialState(false);

				listTransitionDto=new ArrayList<TransitionDescriptionDTO>();
				transitionDto=new TransitionDescriptionDTO();
				transitionDto.setEventName("confirmPayment");
				transitionDto.setTransitionAction("org.chenile.stm.test.basicflow.ConfirmPayment");
				transitionDto.setNewStateId("TEST_STATE");
				transitionDto.setIsRetrievalTransition(false);
				listTransitionDto.add(transitionDto);
				state.setTransitionDescriptions(listTransitionDto);
				listState.add(state);


				state=new StateDescriptionDTO();
				state.setStateName("PAYMENT_CONFIRMED");
				state.setStateType("manual-state");
				state.setIsFinalState(true);
				state.setIsInitialState(false);
				listState.add(state);

				state=new StateDescriptionDTO();
				state.setStateName("CLOSED");
				state.setStateType("manual-state");
				state.setIsFinalState(true);
				state.setIsInitialState(false);
				listState.add(state);


				flow.setStateDescriptions(listState);
				flowList.add(flow);
				dto.setFlowDescriptions(flowList);

				return dto;
			}
		};
		STMFlowStoreImpl stmFlowStoreImpl = new STMFlowStoreImpl();
		DataFlowReader db= new DataFlowReader(dataProvider,stmFlowStoreImpl);
		db.init("cart-flow");


		StmDataProvider dataProvider1=new StmDataProvider() {

			@Override
			public StatesDescriptionDTO fillStmData(String flowId) {
				StatesDescriptionDTO dto=new StatesDescriptionDTO();

				List<FlowDescriptionDTO> flowList=new ArrayList<>();
				FlowDescriptionDTO flow=new FlowDescriptionDTO();
				flow.setEntryAction("org.chenile.stm.test.basicflow.EntryAction");
				flow.setExitAction("org.chenile.stm.test.basicflow.ExitAction");
				flow.setFlowName("special-flow");
				flow.setIsDefault(true);
				flow.setSkipEntryExitActionsForAutoStates(true);
				List<StateDescriptionDTO> listState=new ArrayList<StateDescriptionDTO>();
				StateDescriptionDTO state=new StateDescriptionDTO();
				state.setStateName("INIT");
				state.setStateType("manual-state");
				state.setIsFinalState(false);
				state.setIsInitialState(true);

				List<TransitionDescriptionDTO> listTransitionDto=new ArrayList<TransitionDescriptionDTO>();

				TransitionDescriptionDTO transitionDto=new TransitionDescriptionDTO();
				transitionDto.setEventName("end");
				transitionDto.setTransitionAction("org.chenile.stm.test.basicflow.EndAction1");
				transitionDto.setIsRetrievalTransition(false);
				transitionDto.setNewStateId("END");
				listTransitionDto.add(transitionDto);
				state.setTransitionDescriptions(listTransitionDto);

				listState.add(state);

				state=new StateDescriptionDTO();
				state.setStateName("END");
				state.setStateType("manual-state");
				state.setIsFinalState(true);
				state.setIsInitialState(false);
				listState.add(state);

				flow.setStateDescriptions(listState);
				flowList.add(flow);
				dto.setFlowDescriptions(flowList);

				return dto;
			}
		};

		STMFlowStoreImpl stmFlowStore1 = new STMFlowStoreImpl();
		DataFlowReader db1= new DataFlowReader(dataProvider1,stmFlowStore1);
		db1.init("special-flow");

		stm.setStmFlowStore(stmFlowStoreImpl);
		specialFlow.setStmFlowStore(stmFlowStore1);
	}

	public void testSTM() throws Exception{
		MockSecurityStrategy.tl.set("Invaliduser");
		Cart cart = new Cart();
		stm.proceed(cart);
		assertEquals(new State("CREATED","cart-flow"),cart.getCurrentState());

		Item item = new Item("1234",2,20);
		stm.proceed(cart,"addItem",item);
		assertEquals(2,cart.getItem(0).getQuantity());

		
		try{
			stm.proceed(cart,"userLogin","user1");
			System.err.println("user is " + MockSecurityStrategy.tl.get());
			assertFalse("Should not be here",true); 
		}catch(STMException e){
			assertEquals(551,e.getMessageId());
		}

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
		MockSecurityStrategy.tl.set("ValidUser");
		Cart cart = new Cart();
		stm.proceed(cart);
		assertEquals(new State("CREATED","cart-flow"),cart.getCurrentState());

		Item item = new Item("1234",2,20);
		stm.proceed(cart,"addItem",item);
		assertEquals(2,cart.getItem(0).getQuantity());

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
		MockSecurityStrategy.tl.remove();
		System.err.println("deleted the user. user now is " + MockSecurityStrategy.tl.get());
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

	public static class TransitionParams1 {
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

		TransitionParams1 tp = new TransitionParams1();
		tp.stm = stm; 
		tp.cart = cart;
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
