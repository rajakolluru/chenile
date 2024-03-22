package org.chenile.stm.impl;

import org.chenile.stm.STM;
import org.chenile.stm.StateEntity;
import org.chenile.stm.action.STMAutomaticStateComputation;

/**
 * A base action that can serve as a "do nothing" action.  A good stub to insert custom logic that needs to be
 * done for all actions ( inserting performance monitors for instance) 
 * @author Raja Shankar Kolluru
 *
 */
public  class STMActionBase<StateEntityType extends StateEntity> implements STMAutomaticStateComputation<StateEntityType>{

	public final String execute(StateEntityType stateEntity) throws Exception {

		try{
			doSetup(stateEntity);
			return doExecute(stateEntity);
		} finally {
			cleanUp(stateEntity);
		}
	}
	
	protected  String doExecute(StateEntityType flowContext) throws Exception{ return STM.SUCCESS;}
	protected void doSetup(StateEntityType flowContext) throws Exception { }
	protected void cleanUp(StateEntityType flowContext) throws Exception { }

}
