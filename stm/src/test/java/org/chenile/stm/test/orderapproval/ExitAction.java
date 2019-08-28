package org.chenile.stm.test.orderapproval;

import org.chenile.stm.action.STMAction;

public class ExitAction implements STMAction<Order>{

	public void execute(Order order) throws Exception {
		order.addExitTransition(order.getCurrentState());
	}

}
