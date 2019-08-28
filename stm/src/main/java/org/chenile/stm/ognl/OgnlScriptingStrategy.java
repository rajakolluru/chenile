package org.chenile.stm.ognl;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.chenile.stm.action.ScriptingStrategyBase;

import ognl.Ognl;
import ognl.OgnlRuntime;

/**
 * 
 * @author raja
 * Ognl wrapper for using OGNL in scripting.
 * <p>initializes Ognl accessors
 */
public class OgnlScriptingStrategy extends ScriptingStrategyBase {
	

	static {
		// allow access to the http servlet request attributes from OGNL.
		OgnlRuntime.setPropertyAccessor(HttpServletRequest.class, new HttpServletRequestAccessor());
		OgnlRuntime.setPropertyAccessor(Map.class, new MapAccessor());
	}

	@Override
	protected Object getValue(Object parsedScript, Object context)
			throws Exception {
		return Ognl.getValue(parsedScript, context);
	}

	@Override
	protected Object parseExpression(String code) throws Exception {
		// return code;
		return Ognl.parseExpression(code);
	}
	
	
	
}
