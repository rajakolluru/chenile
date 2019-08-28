package org.chenile.stm.test.basicflow;

import org.chenile.stm.action.STMAction;

public class ExitAction implements STMAction<Cart>{
	public static final String LOGMESSAGE = "ExitAction";
	public void execute(Cart cart) throws Exception {
		cart.log(LOGMESSAGE + ":" + cart.getCurrentState().getStateId());
	}

}
