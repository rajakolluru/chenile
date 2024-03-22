package org.chenile.stm.test.orderapproval;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.chenile.stm.STM;
import org.chenile.stm.State;
import org.chenile.stm.action.STMAction;
import org.chenile.stm.exception.STMException;
import org.chenile.stm.impl.STMFlowStoreImpl;
import org.chenile.stm.impl.STMImpl;
import org.chenile.stm.impl.XmlFlowReader;

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

public class TestOrderFlow extends TestCase {
	private static final String FLOW_DEFINITION_FILE = "org/chenile/stm/test/orderapproval/order-flow.xml";
	private STM<Order> stm ;
	private Map<String, Object> compMap = new HashMap<String, Object>();
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
	
	public class MyStore extends STMFlowStoreImpl {

		// Provide a hook to a local component factory to facilitate mocking
		@Override
		public Object makeComponent(String componentName) throws STMException{
			Object action =  compMap.get(componentName);
			if (action == null) {
				action = super.makeComponent(componentName);
			}
			return action;
		}
	}
	
	private  void createComponents(){
		STMAction<Order> entryAction = new EntryAction();
		compMap.put("entryAction",entryAction);
		STMAction<Order> exitAction = new ExitAction();
		compMap.put("exitAction",exitAction);
	}
	
	
	public void setUp() throws Exception{
		createComponents();
		stm = new STMImpl<Order>();
		STMFlowStoreImpl stmFlowStoreImpl = new MyStore();
		XmlFlowReader xfr = new XmlFlowReader(stmFlowStoreImpl);
		xfr.setFilename(FLOW_DEFINITION_FILE);
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
		assertEquals(2,order.getEntryStates().size());
		assertEquals(2,order.getExitStates().size());
		assertEquals(MANUAL_CORRECTION_STATE,order.getEntryStates().get(0));
		assertEquals(DISCARDED_STATE,order.getEntryStates().get(1));

		
		assertEquals(MANUAL_CORRECTION_STATE,order.getExitStates().get(0));
		assertEquals(DISCARDED_STATE,order.getExitStates().get(1));
	}

}
