package org.chenile.stm.dummy;

import org.chenile.stm.StateEntity;
import org.chenile.stm.action.STMAutomaticStateComputation;

public  class DummyAutomaticStateComputation implements STMAutomaticStateComputation<StateEntity> {
    @Override
    public String execute(StateEntity stateEntity) throws Exception {
        return "";
    }
}
