package org.chenile.stm.test.basicflow;

import org.chenile.stm.STMInternalTransitionInvoker;
import org.chenile.stm.State;
import org.chenile.stm.action.STMTransitionAction;
import org.chenile.stm.model.Transition;

public class UserLogin implements STMTransitionAction<Cart> {

	public static final String LOGMESSAGE = "UserLogin";
	public void doTransition(Cart cart, Object transitionParam, State startState, String eventId,
			State endState,STMInternalTransitionInvoker<?> stmhandle,Transition transition)
			throws Exception {
		String userId = (String)transitionParam;
		cart.setUserId(userId);
		cart.log(LOGMESSAGE);
	}

}
