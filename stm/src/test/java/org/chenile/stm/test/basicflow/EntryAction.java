package org.chenile.stm.test.basicflow;

import org.chenile.stm.action.STMAction;

public class EntryAction implements STMAction<Cart>{

	public static final String LOGMESSAGE = "EntryAction";
	public void execute(Cart cart) throws Exception {
		cart.log( LOGMESSAGE + ":" + cart.getCurrentState().getStateId());
	}

}
