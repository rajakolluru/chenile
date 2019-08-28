package org.chenile.stm.action.scriptsupport;

import ognl.OgnlException;

import org.chenile.stm.StateEntity;
import org.chenile.stm.impl.ScriptingStrategy;

/**
 * Implements an IF action using the same expression language as the one supported by the {@link ScriptingStrategy}. 
 * Support custom properties "condition", "then" and "else". If the condition evaluates to true then the "then" event
 * is returned else the "else" event is returned.
 * <p>The three attributes can also contain constructs such as ${xxx} which would also be evaluated against the flow context.
 * Ex:
 * &lt;if condition='a == 100' then='enabled' else='disabled'/&gt;
 * if a (which is a property of the StateEntity) is 100 then the enabled event would be returned else
 * the disabled event would be returned. 
 * @author Raja Shankar Kolluru
 * 
 */
public class IfAction<StateEntityType extends StateEntity> extends BaseScriptingAction<StateEntityType> {
	
	private static final String EXPRESSION = "condition";
	private static final String FALSE_EVENT = "else";
	private static final String TRUE_EVENT = "then";
	

	
	
	protected String getTrueEvent(StateEntityType stateEntity) throws Exception{
		return getComponentProperty(stateEntity, TRUE_EVENT);
	}
	
	protected String getFalseEvent(StateEntityType stateEntity) throws Exception {
		return getComponentProperty(stateEntity, FALSE_EVENT);
	}
		
	protected String getExpression(StateEntityType stateEntity) throws Exception{
		return getComponentProperty(stateEntity, EXPRESSION);
	}
	
	public String doExecute(StateEntityType stateEntity) throws Exception {		
		try {		
			Boolean o = scriptingStrategy.executeBooleanExpression(getExpression(stateEntity),stateEntity);
			return  (o == true)? getTrueEvent(stateEntity): getFalseEvent(stateEntity);
		} catch (OgnlException e) {
			e.printStackTrace();
			return getFalseEvent(stateEntity);
		}
	}


}