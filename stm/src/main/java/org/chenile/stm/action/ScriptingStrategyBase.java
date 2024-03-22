package org.chenile.stm.action;

import java.util.Map;
import java.util.WeakHashMap;

import org.chenile.stm.impl.ScriptingStrategy;

import ognl.Ognl;
import ognl.OgnlException;

/**
 * A base class for all script aware generic actions.
 * @author Raja Shankar Kolluru
 *
 */
public abstract class ScriptingStrategyBase implements ScriptingStrategy {
	
	private static Map<String,Object> parsedExpressionMap ;

	public boolean executeBooleanExpression(String script, Object context) throws Exception {
		Boolean b = (Boolean)executeGenericScript(script,context);
		return b.booleanValue();
	}

	public Object executeGenericScript(String script, Object context) throws Exception {
		Object o = parseIt(script);
		return getValue(o, context);
	}
	
	protected abstract Object getValue(Object parsedScript, Object context) throws Exception;
	protected abstract Object parseExpression(String code) throws Exception;
	
	public ScriptingStrategyBase() {
		init();
	}

	public static void init(){
		parsedExpressionMap = new WeakHashMap<String, Object>();
	}
	/**
	 * 
	 * @param code the code to parse.
	 * @return the parsed code. i.e. the code is given in an optimized form so that it is faster to execute.
	 */
	protected Object parseIt(String code) throws OgnlException {
		// don't use  contains method to verify if the object exists for the GC could have removed the
		// key between the contains and the get invocation. Easier to do null checks for obj as implemented below.
		Object obj = parsedExpressionMap.get(code);
		if (obj != null) return obj;
		// tighten the code.. remove \ns and \ts , fuse multiple spaces into one, remove leading and trailing spaces etc.
		// All semi-colons are replaced by commas since OGNL supports only expressions not statements
		// Ensure that the code does not end with , since OGNL will crap out.
	    String sanitizedCode = sanitizeExpression(code); 
		obj = Ognl.parseExpression(sanitizedCode);
		parsedExpressionMap.put(code,obj); // use the unsanitized code as key since we then optimize the sanitization part as well
		// when checking for cached keys. 
		return obj;
	}
	
	protected String sanitizeExpression(String code) {
		code =	code.replaceAll("\n"," ").replaceAll("\t"," ").replaceAll(" +"," ").trim().replaceAll(";",",");
	    code = code.endsWith(",")? code.substring(0,code.length() - 1) : code;
		return code;
	}
	

}
