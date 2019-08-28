package org.chenile.stm.action.scriptsupport;

import org.chenile.stm.StateEntity;
import org.chenile.stm.action.ScriptingStrategyAware;
import org.chenile.stm.impl.ScriptingStrategy;

public class BaseScriptingAction<StateEntityType extends StateEntity> extends 
		BaseCustomComponentPropertiesAction<StateEntityType> implements ScriptingStrategyAware{
	protected ScriptingStrategy scriptingStrategy;
	

	public void setScriptingStrategy(ScriptingStrategy ss) {
		this.scriptingStrategy = ss;		
	}

	
}
