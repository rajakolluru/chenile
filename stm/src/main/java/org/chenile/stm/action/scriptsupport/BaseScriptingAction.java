package org.chenile.stm.action.scriptsupport;

import org.chenile.stm.StateEntity;
import org.chenile.stm.action.ScriptingStrategyAware;
import org.chenile.stm.impl.ScriptingStrategy;
import org.chenile.stm.ognl.OgnlScriptingStrategy;

public class BaseScriptingAction<StateEntityType extends StateEntity> extends 
		BaseCustomComponentPropertiesAction<StateEntityType> implements ScriptingStrategyAware{
	protected ScriptingStrategy scriptingStrategy = new OgnlScriptingStrategy();
	

	public void setScriptingStrategy(ScriptingStrategy ss) {
		this.scriptingStrategy = ss;		
	}

	
}
