package org.chenile.stm.action.scriptsupport;

import org.chenile.stm.STM;
import org.chenile.stm.StateEntity;

/**
 * Mimics a script. The value of the last line of the script is returned as the event from this action.
 * It uses the current scripting strategy for the evaluation. The code custom attribute is supported.
 * Ex: &lt;script code="(a == 100)? 'go ahead' : 'stop'"/&gt;
 * would evaluate to "go ahead" if a is 100 else it would evaluate to 'stop'
 * Languages such as OGNL support the comma operator which allows more exotic expressions. See the 
 * testcase for an example.
 *  
 * @author Raja Shankar Kolluru
 */

public class ScriptAction<StateEntityType extends StateEntity> extends BaseScriptingAction<StateEntityType> {

	private static final String CODE = "code";
	
	
	@Override
	protected String doExecute(StateEntityType stateEntity) throws Exception {
		Object ret = null;
		String code = getComponentProperty(stateEntity, CODE);
		if (code == null)
			throw new Exception("Cannot have an empty code attribute for script action");
		ret = scriptingStrategy.executeGenericScript(code,stateEntity);

		// the value of the last evaluated expression is returned.
		return (ret == null)? STM.FAILURE : ret.toString();
	}

}
