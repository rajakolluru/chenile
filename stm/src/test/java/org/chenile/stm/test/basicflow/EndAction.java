package org.chenile.stm.test.basicflow;

import org.chenile.stm.STMInternalTransitionInvoker;
import org.chenile.stm.State;
import org.chenile.stm.action.STMTransitionAction;
import org.chenile.stm.model.Transition;
import org.chenile.stm.test.basicflow.TestCartFlow.TransitionParams;

public class EndAction implements STMTransitionAction<Cart> {

	public static final String LOGMESSAGE = "EndAction";
	@SuppressWarnings("unchecked")
	@Override
	public void doTransition(Cart cart, Object transitionParam,
			State startState, String eventId,State endState,
			STMInternalTransitionInvoker<?> stmHandle,Transition transition) throws Exception {
		TransitionParams tp = (TransitionParams)transitionParam;
		
		STMInternalTransitionInvoker<Cart> invoker = (STMInternalTransitionInvoker<Cart>) stmHandle;
		tp.cart = invoker.proceed(tp.stm, tp.cart, "close", null);
		cart.log(LOGMESSAGE);
	}

}
