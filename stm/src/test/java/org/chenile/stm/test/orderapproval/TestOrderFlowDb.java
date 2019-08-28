package org.chenile.stm.test.orderapproval;

import java.util.ArrayList;
import java.util.List;

import org.chenile.stm.STM;
import org.chenile.stm.State;
import org.chenile.stm.StmDataProvider;
import org.chenile.stm.dto.FlowDescriptionDTO;
import org.chenile.stm.dto.StateAttributesDTO;
import org.chenile.stm.dto.StateDescriptionDTO;
import org.chenile.stm.dto.StatesDescriptionDTO;
import org.chenile.stm.dto.TransitionDescriptionDTO;
import org.chenile.stm.impl.DataFlowReader;
import org.chenile.stm.impl.STMFlowStoreImpl;
import org.chenile.stm.impl.STMImpl;

import junit.framework.TestCase;

/**
 * The <a ref="testcase.htm">described</a> order approval process, is used to run these series of tests. 
 * The tests verify aspects of all the flows and in the process exhibit most of the features of the STM functionality. 
 * Things to note:
 * <ol>
 * <li>Please check the order-flow.xml for an insight into the flow configuration.
 * <li>Check how the context ({@link Order} is used to store order related information as it flows from one state to the other.
 * <li>Check how the {@link Order#entryStates} and the {@link Order#exitStates} is used to link the flow. 
 * </ol>
 * @author Raja Shankar Kolluru
 *
 */

public class TestOrderFlowDb extends TestCase {
	private STM<Order> stm = new STMImpl<Order>();
	public static final String ORDER_APPROVAL_FLOW = "orderApproval";
	public static final String INVALID_ORDER_FLOW = "invalidOrder";

	public static final State NEW_STATE = new State("new",ORDER_APPROVAL_FLOW);
	public static final State VALID_STATE = new State("valid",ORDER_APPROVAL_FLOW);
	public static final State INVALID_STATE = new State("invalid",INVALID_ORDER_FLOW);
	public static final State DISCARDED_STATE = new State("discarded",ORDER_APPROVAL_FLOW);
	public static final State FULFILLED_STATE = new State("fulfilled", ORDER_APPROVAL_FLOW);
	public static final State PENDING_APPROVAL = new State("pendingApproval",ORDER_APPROVAL_FLOW);
	public static final State AUTO_CORRECTION_STATE = new State("autoCorrected",INVALID_ORDER_FLOW);
	public static final State MANUAL_CORRECTION_STATE = new State("manualCorrection",INVALID_ORDER_FLOW);

	public void setUp() throws Exception{


		StmDataProvider dataProvider=new StmDataProvider() {

			@Override
			public StatesDescriptionDTO fillStmData(String flowId) {
				StatesDescriptionDTO dto=new StatesDescriptionDTO();

				List<FlowDescriptionDTO> flowList=new ArrayList<>();
				FlowDescriptionDTO flow=new FlowDescriptionDTO();
				flow.setEntryAction("org.chenile.stm.test.orderapproval.EntryAction");
				flow.setExitAction("org.chenile.stm.test.orderapproval.ExitAction");
				flow.setFlowName(ORDER_APPROVAL_FLOW);
				flow.setIsDefault(true);
				flow.setSkipEntryExitActionsForAutoStates(true);
				List<StateDescriptionDTO> listState=new ArrayList<StateDescriptionDTO>();

				StateDescriptionDTO state=new StateDescriptionDTO();
				state.setStateName("new");
				state.setStateType("if");
				List<StateAttributesDTO> stateAttributes=new ArrayList<>();
				StateAttributesDTO stateAttributesDTO=new StateAttributesDTO();
				stateAttributesDTO.setKey("condition");
				String condition="numItems == 1 && itemNames[0] != 'foo'";
				stateAttributesDTO.setValue(condition);
				stateAttributes.add(stateAttributesDTO);
				stateAttributesDTO=new StateAttributesDTO();
				stateAttributesDTO.setKey("then");
				stateAttributesDTO.setValue("valid");
				stateAttributes.add(stateAttributesDTO);
				stateAttributesDTO=new StateAttributesDTO();
				stateAttributesDTO.setKey("else");
				stateAttributesDTO.setValue("invalid");
				stateAttributes.add(stateAttributesDTO);
				state.setStateAttributes(stateAttributes);
				state.setIsFinalState(false);
				state.setIsInitialState(true);

				List<TransitionDescriptionDTO>listTransitionDto=new ArrayList<TransitionDescriptionDTO>();
				TransitionDescriptionDTO transitionDto=new TransitionDescriptionDTO();
				transitionDto.setEventName("valid");
				transitionDto.setNewStateId("valid");
				transitionDto.setIsRetrievalTransition(false);
				listTransitionDto.add(transitionDto);


				transitionDto=new TransitionDescriptionDTO();
				transitionDto.setEventName("invalid");
				transitionDto.setNewStateId("invalid");
				transitionDto.setNewFlowId("invalidOrder");
				transitionDto.setIsRetrievalTransition(false);
				listTransitionDto.add(transitionDto);

				state.setTransitionDescriptions(listTransitionDto);
				listState.add(state);

				state=new StateDescriptionDTO();
				state.setStateName("pendingApproval");
				state.setStateType("manual-state");
				state.setIsFinalState(false);
				state.setIsInitialState(false);

				listTransitionDto=new ArrayList<TransitionDescriptionDTO>();
				transitionDto=new TransitionDescriptionDTO();
				transitionDto.setEventName("approved");
				transitionDto.setIsRetrievalTransition(false);
				transitionDto.setNewStateId("fulfilled");

				listTransitionDto.add(transitionDto);


				transitionDto=new TransitionDescriptionDTO();
				transitionDto.setEventName("rejected");
				transitionDto.setIsRetrievalTransition(false);
				transitionDto.setNewStateId("discarded");
				listTransitionDto.add(transitionDto);

				state.setTransitionDescriptions(listTransitionDto);
				listState.add(state);

				state=new StateDescriptionDTO();
				state.setStateName("discarded");
				state.setStateType("manual-state");
				state.setIsFinalState(true);
				state.setIsInitialState(false);
				listState.add(state);

				state=new StateDescriptionDTO();
				state.setStateName("fulfilled");
				state.setStateType("auto-state");
				state.setIsFinalState(true);
				state.setIsInitialState(false);
				listState.add(state);


				state=new StateDescriptionDTO();
				state.setStateName("valid");
				state.setStateType("if");
				stateAttributes=new ArrayList<>();
				stateAttributesDTO=new StateAttributesDTO();
				stateAttributesDTO.setKey("condition");
				stateAttributesDTO.setValue("orderTotal <= 10000");
				stateAttributes.add(stateAttributesDTO);
				stateAttributesDTO=new StateAttributesDTO();
				stateAttributesDTO.setKey("then");
				stateAttributesDTO.setValue("autoApproval");
				stateAttributes.add(stateAttributesDTO);
				stateAttributesDTO=new StateAttributesDTO();
				stateAttributesDTO.setKey("else");
				stateAttributesDTO.setValue("requiresApproval");
				stateAttributes.add(stateAttributesDTO);
				state.setStateAttributes(stateAttributes);
				state.setIsFinalState(false);
				state.setIsInitialState(false);

				listTransitionDto=new ArrayList<TransitionDescriptionDTO>();
				transitionDto=new TransitionDescriptionDTO();
				transitionDto.setEventName("autoApproval");
				transitionDto.setNewStateId("fulfilled");
				transitionDto.setIsRetrievalTransition(false);
				listTransitionDto.add(transitionDto);


				transitionDto=new TransitionDescriptionDTO();
				transitionDto.setEventName("requiresApproval");
				transitionDto.setNewStateId("pendingApproval");
				transitionDto.setIsRetrievalTransition(false);
				listTransitionDto.add(transitionDto);

				state.setTransitionDescriptions(listTransitionDto);
				listState.add(state);

				flow.setStateDescriptions(listState);
				flowList.add(flow);


				FlowDescriptionDTO flow2=new FlowDescriptionDTO();
				flow2.setFlowName(INVALID_ORDER_FLOW);
				flow2.setIsDefault(false);
				flow2.setSkipEntryExitActionsForAutoStates(true);
				flow2.setEntryAction("org.chenile.stm.test.orderapproval.EntryAction");
				flow2.setExitAction("org.chenile.stm.test.orderapproval.ExitAction");
				//flow.setInitialState("new");
				listState=new ArrayList<StateDescriptionDTO>();

				state=new StateDescriptionDTO();
				state.setStateName("invalid");
				state.setStateType("if");
				stateAttributes=new ArrayList<>();
				stateAttributesDTO=new StateAttributesDTO();
				stateAttributesDTO.setKey("condition");
				condition="orderTotal <= 10000";
				stateAttributesDTO.setValue(condition);
				stateAttributes.add(stateAttributesDTO);
				stateAttributesDTO=new StateAttributesDTO();
				stateAttributesDTO.setKey("then");
				stateAttributesDTO.setValue("autoCorrected");
				stateAttributes.add(stateAttributesDTO);
				stateAttributesDTO=new StateAttributesDTO();
				stateAttributesDTO.setKey("else");
				stateAttributesDTO.setValue("needsManualCorrection");
				stateAttributes.add(stateAttributesDTO);
				state.setStateAttributes(stateAttributes);
				state.setIsFinalState(false);
				state.setIsInitialState(false);

				listTransitionDto=new ArrayList<TransitionDescriptionDTO>();
				transitionDto=new TransitionDescriptionDTO();
				transitionDto.setEventName("autoCorrected");
				transitionDto.setNewStateId("autoCorrected");
				transitionDto.setIsRetrievalTransition(false);
				listTransitionDto.add(transitionDto);


				transitionDto=new TransitionDescriptionDTO();
				transitionDto.setEventName("needsManualCorrection");
				transitionDto.setNewStateId("manualCorrection");
				transitionDto.setIsRetrievalTransition(false);
				listTransitionDto.add(transitionDto);

				state.setTransitionDescriptions(listTransitionDto);
				listState.add(state);

				state=new StateDescriptionDTO();
				state.setStateName("manualCorrection");
				state.setStateType("manual-state");
				state.setIsFinalState(false);
				state.setIsInitialState(false);

				listTransitionDto=new ArrayList<TransitionDescriptionDTO>();
				transitionDto=new TransitionDescriptionDTO();
				transitionDto.setEventName("corrected");
				transitionDto.setIsRetrievalTransition(false);
				transitionDto.setNewStateId("new");
				transitionDto.setNewFlowId("orderApproval");

				listTransitionDto.add(transitionDto);

				transitionDto=new TransitionDescriptionDTO();
				transitionDto.setEventName("rejected");
				transitionDto.setIsRetrievalTransition(false);
				transitionDto.setNewStateId("discarded");
				transitionDto.setNewFlowId("orderApproval");
				listTransitionDto.add(transitionDto);

				state.setTransitionDescriptions(listTransitionDto);
				listState.add(state);

				state=new StateDescriptionDTO();
				state.setStateName("autoCorrected");
				state.setStateType("script");
				stateAttributes=new ArrayList<>();
				stateAttributesDTO=new StateAttributesDTO();
				stateAttributesDTO.setKey("code");
				condition="(itemNames[0] == 'foo') ? 'rejected' : (numItems = 1, 'corrected') ";
				stateAttributesDTO.setValue(condition);
				stateAttributes.add(stateAttributesDTO);
				state.setStateAttributes(stateAttributes);
				state.setIsFinalState(false);
				state.setIsInitialState(false);

				listTransitionDto=new ArrayList<TransitionDescriptionDTO>();
				transitionDto=new TransitionDescriptionDTO();
				transitionDto.setEventName("corrected");
				transitionDto.setIsRetrievalTransition(false);
				transitionDto.setNewStateId("new");
				transitionDto.setNewFlowId("orderApproval");

				listTransitionDto.add(transitionDto);

				transitionDto=new TransitionDescriptionDTO();
				transitionDto.setEventName("rejected");
				transitionDto.setIsRetrievalTransition(false);
				transitionDto.setNewStateId("discarded");
				transitionDto.setNewFlowId("orderApproval");
				listTransitionDto.add(transitionDto);

				state.setTransitionDescriptions(listTransitionDto);
				listState.add(state);

				flow2.setStateDescriptions(listState);
				flowList.add(flow2);

				dto.setFlowDescriptions(flowList);
				return dto;
			}
		};
		STMFlowStoreImpl stmFlowStoreImpl = new STMFlowStoreImpl();
		DataFlowReader db= new DataFlowReader(dataProvider,stmFlowStoreImpl);
		db.init("order-flow");
		stm.setStmFlowStore(stmFlowStoreImpl);

	}

	public void testValidAutoApprovedFlow()  throws Exception{
		Order order = new Order();
		order.setNumItems(1);
		List<String> itemNames = new ArrayList<String>();
		itemNames.add("bar");
		order.setItemNames(itemNames);
		order.setOrderTotal(1000);
		
		order = stm.proceed(order);
		assertEquals(null,order.getId());
		// As per the flow, we expect the sequence of states to be new->valid->fulfilled
		// the entry actions for all the states need to be called.
		// the exit action for the fulfilled state would not be called.
		// Verify all these.
		assertEquals(1,order.getEntryStates().size());
		assertEquals(1,order.getExitStates().size());
		assertEquals(FULFILLED_STATE,order.getEntryStates().get(0));
		assertEquals(FULFILLED_STATE,order.getExitStates().get(0));

	}
	
	public void testValidManualApprovalFlowWithApproval()  throws Exception{
		Order order = new Order();
		order.setNumItems(1);
		List<String> itemNames = new ArrayList<String>();
		itemNames.add("bar");
		order.setItemNames(itemNames);
		order.setOrderTotal(10001);
		
		order = stm.proceed(order);
		State newStateId = order.getCurrentState();
		
		assertEquals(PENDING_APPROVAL, newStateId);
		order = stm.proceed(order,"approved",null);
		newStateId = order.getCurrentState();
		assertEquals(FULFILLED_STATE,newStateId);

		// sequence expected is new->valid->pendingApproval->fulfilled
		assertEquals(2,order.getEntryStates().size());
		assertEquals(2,order.getExitStates().size());
		assertEquals(PENDING_APPROVAL,order.getEntryStates().get(0));
		assertEquals(FULFILLED_STATE,order.getEntryStates().get(1));
		assertEquals(PENDING_APPROVAL,order.getExitStates().get(0));	
		assertEquals(FULFILLED_STATE,order.getExitStates().get(1));
	}
	
	public void testValidManualApprovalFlowWithRejection()  throws Exception{
		Order order = new Order();
		order.setNumItems(1);
		List<String> itemNames = new ArrayList<String>();
		itemNames.add("bar");
		order.setItemNames(itemNames);
		order.setOrderTotal(10001);
		
		order = stm.proceed(order);
		State newStateId = order.getCurrentState();
		assertEquals(PENDING_APPROVAL, newStateId);
		order = stm.proceed(order,"rejected",null);
		newStateId = order.getCurrentState(); 
		assertEquals(DISCARDED_STATE,newStateId);
		
		// sequence expected is new->valid->pendingApproval->discarded
		assertEquals(2,order.getEntryStates().size());
		assertEquals(2,order.getExitStates().size());
		assertEquals(PENDING_APPROVAL,order.getEntryStates().get(0));
		assertEquals(DISCARDED_STATE,order.getEntryStates().get(1));
		assertEquals(PENDING_APPROVAL,order.getExitStates().get(0));
		assertEquals(DISCARDED_STATE,order.getExitStates().get(1));
	}
	
	public void testInvalidAutoCorrectedFlowWithwithCorrectionAutomatic() throws Exception {
		Order order = new Order();
		order.setNumItems(2);
		List<String> itemNames = new ArrayList<String>();
		itemNames.add("bar"); // we expect this order to go thru with one item bar instead of two items bar and baz
		itemNames.add("baz");
		order.setItemNames(itemNames);
		order.setOrderTotal(10000);
		
		order = stm.proceed(order);
		State newStateId = order.getCurrentState();
		assertEquals(FULFILLED_STATE, newStateId);
		 
		// we expect that the order was auto corrected to make num Items as 1
		// though we had set it to 2 above.
		assertEquals(1, order.getNumItems());
		// new->invalid(coz num items != 1) ->autocorrected (coz itemnames[0] != 'foo' and total <= 10000) ->new (this time valid since 
		// num items made to 1) ->valid->fulfilled
		assertEquals(1,order.getEntryStates().size());
		assertEquals(1,order.getExitStates().size());
		assertEquals(FULFILLED_STATE,order.getEntryStates().get(0));
		
		assertEquals(FULFILLED_STATE,order.getExitStates().get(0));
	}
	
	public void testInvalidAutoCorrectedFlowWithwithRejection() throws Exception {
		Order order= new Order();
		order.setNumItems(2);
		List<String> itemNames = new ArrayList<String>();
		itemNames.add("foo"); // since foo is a prohibited item we expect this order to be discarded.
		itemNames.add("baz");
		order.setItemNames(itemNames);
		order.setOrderTotal(10000);
		order = stm.proceed(order);
		State newStateId = order.getCurrentState();
		assertEquals(DISCARDED_STATE, newStateId);
		
		// new -> invalid(item[0] = 'foo') -> autoCorrected (< 10000) -> discarded (item[0] = 'foo')
		assertEquals(1,order.getEntryStates().size());
		assertEquals(1,order.getExitStates().size());
		assertEquals(DISCARDED_STATE,order.getEntryStates().get(0));

		assertEquals(DISCARDED_STATE,order.getExitStates().get(0));
	}
	
	public void testInvalidManualCorrectionFlowWithCorrectedEvent() throws Exception {
		Order order = new Order();
		order.setNumItems(2);
		List<String> itemNames = new ArrayList<String>();
		itemNames.add("foo"); 
		itemNames.add("baz");
		order.setItemNames(itemNames);
		order.setOrderTotal(10001);
		
		order = stm.proceed(order);
		State newStateId = order.getCurrentState();
		assertEquals(MANUAL_CORRECTION_STATE, newStateId);
		// now correct the context.
		order.setNumItems(1);
		itemNames = new ArrayList<String>();
		itemNames.add("bar");
		order.setItemNames(itemNames);
		order.setOrderTotal(10000); // decrease order total so that auto approval goes thru.
		order = stm.proceed(order,"corrected",null);
		newStateId = order.getCurrentState();
		assertEquals(FULFILLED_STATE,newStateId);
		
		// new -> invalid -> manualCorrection -> new -> valid -> fulfilled
		
		assertEquals(2,order.getEntryStates().size());
		assertEquals(2,order.getExitStates().size());
		assertEquals(MANUAL_CORRECTION_STATE,order.getEntryStates().get(0));
		assertEquals(FULFILLED_STATE,order.getEntryStates().get(1));
		
		assertEquals(MANUAL_CORRECTION_STATE,order.getExitStates().get(0));
		assertEquals(FULFILLED_STATE,order.getExitStates().get(1));
	}
	
	public void testInvalidManualCorrectionFlowWithRejectedEvent() throws Exception {
		Order order = new Order();
		order.setNumItems(2);
		List<String> itemNames = new ArrayList<String>();
		itemNames.add("foo"); 
		itemNames.add("baz");
		order.setItemNames(itemNames);
		order.setOrderTotal(10001);
		order = stm.proceed(order);
		State newStateId = order.getCurrentState();
		assertEquals(MANUAL_CORRECTION_STATE, newStateId);

		order = stm.proceed(order,"rejected",null);
		newStateId = order.getCurrentState();
		assertEquals(DISCARDED_STATE,newStateId);

		// new -> invalid -> manual correction -> discarded
		System.out.println(":::::::::order.getEntryStates()"+order.getEntryStates());
		assertEquals(2,order.getEntryStates().size());
		assertEquals(2,order.getExitStates().size());
		assertEquals(MANUAL_CORRECTION_STATE,order.getEntryStates().get(0));
		assertEquals(DISCARDED_STATE,order.getEntryStates().get(1));

		
		assertEquals(MANUAL_CORRECTION_STATE,order.getExitStates().get(0));
		assertEquals(DISCARDED_STATE,order.getExitStates().get(1));
	}
	
}
