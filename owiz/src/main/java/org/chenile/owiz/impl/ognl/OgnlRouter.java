package org.chenile.owiz.impl.ognl;

import java.util.Map;

import org.chenile.ognl.MapAccessor;
import org.chenile.ognl.ParseExpression;
import org.chenile.owiz.impl.Router;

import ognl.Ognl;
import ognl.OgnlRuntime;

/**
 * Avoids creation of multiple routers by allowing an expression that can be utilized on the 
 * context to obtain the route. 
 * <p>Sample:
 * &lt;router id='xxx' componentName='yyy' expression='a.b'&gt;
 * ---
 * &lt;/router&gt;
 * <p>
 * In the above example, context.getA().getB() would be evaluated to obtain the route.
 * The object obtained from the evaluation is then converted to a string and returned as 
 * a routing string which is then used to compute the correct command that can be used.
 * @author Raja Shankar Kolluru
 *
 * @param <InputType> .
 */
public class OgnlRouter<InputType> extends Router<InputType> {

	public static final String EXPRESSION = "expression";
	static {
		OgnlRuntime.setPropertyAccessor(Map.class, new MapAccessor());
	}
	
	@Override
	protected String computeRoutingString(InputType context) throws Exception {
		String expression = getConfigValue(EXPRESSION);
		if (expression == null) return null;
		Object o = Ognl.getValue(ParseExpression.parseIt(expression), context);
		return (o != null)?  o.toString() : null;
	}
}
