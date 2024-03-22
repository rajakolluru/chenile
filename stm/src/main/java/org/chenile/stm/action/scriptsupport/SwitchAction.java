package org.chenile.stm.action.scriptsupport;

import org.chenile.stm.StateEntity;

import ognl.OgnlException;

/**
 * Emulates a switch construct. Introduces the following custom attributes:
 * expression - the expression to be evaluated.
 * case-FOO - the event that would be returned if the expression's value is 'FOO'
 * default - the event that would be returned if none of the cases match the value of the expression. 
 * @author Raja Shankar Kolluru 
 * 
 */
public class SwitchAction<StateEntityType extends StateEntity> extends BaseScriptingAction<StateEntityType> {
	
	private static final String EXPRESSION = "expression";
	private static final String CASE_PREFIX = "case-";
	private static final String DEFAULT = "default";
	

	
	public String doExecute(StateEntityType stateEntity) throws Exception {
		String de = getComponentProperty(stateEntity,DEFAULT) ;
		try {
			Object o = scriptingStrategy.executeGenericScript(getComponentProperty(stateEntity,EXPRESSION), stateEntity);
			String caseToPick = CASE_PREFIX + o; // for null this would be case-null
			String caseEvent = getComponentProperty(stateEntity,caseToPick);
			
			return (caseEvent == null)? de : caseEvent;
		} catch (OgnlException e) {
			e.printStackTrace();
			return de;
		}
	}
	


}