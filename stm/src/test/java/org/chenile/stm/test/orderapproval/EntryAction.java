package org.chenile.stm.test.orderapproval;

import org.chenile.stm.action.STMAction;

public class EntryAction implements STMAction<Order>{

	public void execute(Order order) throws Exception {
		String newStateId = order.getCurrentState().getStateId();
		if (newStateId.equals("new")){
			order.setId("1234"); // generated a new order Id.
		}
		order.addEntryTransition(order.getCurrentState());
	}

}
