package org.chenile.stm.test.basicflow;

import org.chenile.stm.STMInternalTransitionInvoker;
import org.chenile.stm.State;
import org.chenile.stm.action.STMTransitionAction;
import org.chenile.stm.model.Transition;

public class InitiatePayment implements STMTransitionAction<Cart> {

	public static final String LOGMESSAGE = "InitiatePayment";
	public void doTransition(Cart cart, Object transitionParam, State startState, String eventId,
			State endState,STMInternalTransitionInvoker<?> stmhandle,
			Transition transition)
			throws Exception {
		Payment payment = (Payment)transitionParam;
		cart.setPayment(payment);
		cart.log(LOGMESSAGE);
	}

}
