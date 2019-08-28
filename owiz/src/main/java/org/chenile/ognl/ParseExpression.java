package org.chenile.ognl;


import java.util.Map;
import java.util.WeakHashMap;

import ognl.Ognl;
import ognl.OgnlException;

/**
 * Wrapper around Ognl.parseExpression() which uses caching.
 * Provides optimized access to an expression. Expressions must be parsed first
 * using the parseIt() method before they are used using Ognl.
 * @author raja
 * 
 */
public class ParseExpression {
	private static Map<String,Object> parsedExpressionMap ;

	static {
		init();
	}
	
	public static void init(){
		parsedExpressionMap = new WeakHashMap<String, Object>();
	}

	/**
	 *  .
	 * @param code .
	 * @return .
	 * @throws OgnlException .
	 */
	public static Object parseIt(String code) throws OgnlException {
		// don't use  contains method to verify if the object exists for the GC could have removed the
		// key between the contains and the get invocation. Easier to do null checks for obj as implemented below.
		Object obj = parsedExpressionMap.get(code);
		if (obj != null) return obj;
	
	    String sanitizedCode = sanitizeExpression(code); 
		obj = Ognl.parseExpression(sanitizedCode);
		parsedExpressionMap.put(code,obj); // use the unsanitized code as key since we then optimize the sanitization part as well
		// when checking for cached keys. 
		return obj;
	}
	
	/**
	 * 	tighten the code.. remove \ns and \ts , fuse multiple spaces into one, remove leading and trailing spaces etc.
		All semi-colons are replaced by commas since OGNL supports only expressions not statements
		Ensure that the code does not end with , since OGNL will crap out.
	 * @param code .
	 * @return .
	 */
	
	private static String sanitizeExpression(String code) {
		code =	code.replaceAll("\n"," ").replaceAll("\t"," ").replaceAll(" +"," ").trim().replaceAll(";",",");
	    code = code.endsWith(",")? code.substring(0,code.length() - 1) : code;
		return code;
	}
	
	
}
