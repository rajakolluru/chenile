package org.chenile.stm.test.basicflow;

import org.chenile.stm.STMInternalTransitionInvoker;
import org.chenile.stm.State;
import org.chenile.stm.action.STMTransitionAction;
import org.chenile.stm.model.Transition;

public class AddItem implements STMTransitionAction<Cart> {

	public static final String LOGMESSAGE = "AddItem";
	public void doTransition(Cart cart, Object transitionParam, State startState, String eventId,State endState,STMInternalTransitionInvoker<?> stmhandle,
			Transition transition)
			throws Exception {
		Item item = (Item) transitionParam;
		cart.addItem(item);
		cart.log(LOGMESSAGE);
	}

}
