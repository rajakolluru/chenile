package org.chenile.core.interceptors;

import java.util.Map;

import org.chenile.core.context.ChenileExchange;
import org.chenile.owiz.Command;
import org.chenile.owiz.impl.ChainContext;

/**
 * A generic interceptor for other Chenile Interceptors to override.
 * If it is a pre or post interceptor, override the corresponding methods
 * If it is a surround interceptor that needs to stop the chain under certaim circumstances then
 * override the {@link #execute(ChenileExchange)} method. For stopping the chain return without
 * calling the doContinue method
 * If you wish to bypass interception then override {@link #bypassInterception(ChenileExchange)} method.
 * @author Raja Shankar Kolluru
 *
 */
public class BaseChenileInterceptor implements Command<ChenileExchange>{

	/**
	 * If this method is over-ridden, make sure that you call doContinue unless you want to end the 
	 * chain here!
	 * 
	 */
	@Override
	public void execute(ChenileExchange exchange) throws Exception {
		if (bypassInterception(exchange)) {
			doContinue(exchange);
			return;
		}
		doPreProcessing(exchange);
		try{
			doContinue(exchange);
		} finally {
			doPostProcessing(exchange);
		}		
	}
	
	/**
	 * Override this to do post processing. 
	 * This will be called even if there is an exception downstream
	 * @param exchange
	 */
	protected void doPostProcessing(ChenileExchange exchange) {}

	/** 
	 * Override this to do pre processing
	 * @param exchange
	 */
	protected void doPreProcessing(ChenileExchange exchange) {}

	/**
	 * This method needs to be called if you wish to continue processing with the rest of the
	 * interception chain.
	 * @param exchange
	 * @throws Exception
	 */
	protected final void doContinue(ChenileExchange exchange) throws Exception{
		exchange.getChainContext().doContinue();
	}

	protected ChainContext.SavePoint savePoint(ChenileExchange exchange){
		return exchange.getChainContext().savePoint();
	}

	protected void resumeFromSavedPoint(ChainContext.SavePoint savePoint, ChenileExchange exchange)
			throws  Exception{
		exchange.getChainContext().resumeFromSavedPoint(savePoint);
	}
	
	/**
	 * Over-ride this to bypass interception in special circumstances
	 * @param exchange
	 * @return
	 */
	protected boolean bypassInterception(ChenileExchange exchange) {
		return false;
	}
	
	/**
	 * See if there is an annotation of type T defined in either the OperationDefinition or ServiceDefinition. Return if it exists
	 * @param name - the annotation class name
	 * @param exchange - the context thst is being passed around
	 * @return the annotation if it exists or null if it does not. Opertion level annotations override service level annotations
	 */
	@SuppressWarnings("unchecked")
	protected Map<String,Object> getExtensionByAnnotation(String name,ChenileExchange exchange) {
		Map<String,Object> ret = (Map<String,Object>)exchange.getOperationDefinition().getExtension(name);
		if (ret == null) {
			ret = (Map<String,Object>)exchange.getServiceDefinition().getExtension(name);
		}
		return ret;
	}

}
