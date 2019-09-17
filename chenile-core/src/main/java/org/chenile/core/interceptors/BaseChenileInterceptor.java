package org.chenile.core.interceptors;

import org.chenile.core.context.ChenileExchange;
import org.chenile.owiz.Command;

/**
 * A generic interceptor for other Chenile Interceptors to override.
 * If it is a pre or post interceptor, override the corresponding methods
 * If it is a surround interceptor override the {@link #execute(ChenileExchange)} method. 
 * But do this with caution. See notes below
 * @author Raja Shankar Kolluru
 *
 */
public class BaseChenileInterceptor implements Command<ChenileExchange>{

	/**
	 * If this method is over-ridden, make sure that you do not return unless you 
	 * want to stop processing with this interceptor. Else, doContinue must be called.
	 * For a complex surround processing that involves skipping this interceptor or 
	 * only doing pre or post processing in some cases and doing both in other cases the 
	 * code must be structured as follows:
	 * <p><code> 
	 * Ex: 
	 * if (interceptor does not need to be invoked ){
	 *   doContinue(exchange);
	 *   return
	 * }
	 * if (both pre and post processing required){
	 *   do pre processing
	 *   doContinue
	 *   do post processing
	 *   return
	 * } 
	 * if (only post processing is required){
	 *   doContinue
	 *   do post processing
	 *   return
	 * }
	 * if (only pre processing required){
	 *   do pre processing
	 *   doContinue
	 *   return
	 * }
	 * </code></p>
	 */
	@Override
	public void execute(ChenileExchange exchange) throws Exception {
		if (bypassInterception(exchange)) {
			doContinue(exchange);
			return;
		}
		doPreProcessing(exchange);
		doContinue(exchange);
		doPostProcessing(exchange);
	}
	
	protected void doPostProcessing(ChenileExchange exchange) {
		
	}

	protected void doPreProcessing(ChenileExchange exchange) {
		
		
	}

	/**
	 * must be called with over-riding
	 * @param exchange
	 * @throws Exception
	 */
	protected final void doContinue(ChenileExchange exchange) throws Exception{
		exchange.getChainContext().doContinue();
	}
	
	/**
	 * Over-ride this to bypass interception in special circumstances
	 * @param exchange
	 * @return
	 */
	protected boolean bypassInterception(ChenileExchange exchange) {
		return false;
	}

}
