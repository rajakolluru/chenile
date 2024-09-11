package org.chenile.stm.dummy;

import org.chenile.stm.STMInternalTransitionInvoker;
import org.chenile.stm.State;
import org.chenile.stm.StateEntity;
import org.chenile.stm.action.STMTransitionAction;
import org.chenile.stm.model.Transition;

public class DummyTransitionAction implements STMTransitionAction<StateEntity> {
    @Override
    public void doTransition(StateEntity stateEntity, Object transitionParam, State startState, String eventId, State endState, STMInternalTransitionInvoker stm, Transition transition) throws Exception {

    }
}
