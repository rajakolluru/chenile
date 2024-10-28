package org.chenile.stm.dummy;

import org.chenile.stm.StateEntity;
import org.chenile.stm.action.StateEntityRetrievalStrategy;

public class DummyRetrievalStrategy implements StateEntityRetrievalStrategy<StateEntity> {
    @Override
    public StateEntity retrieve(StateEntity stateEntity) throws Exception {
        return null;
    }

    @Override
    public StateEntity merge(StateEntity stateEntity, StateEntity persistentEntity, String eventId) throws Exception {
        return null;
    }
}
