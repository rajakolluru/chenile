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
	 * chain here! This is very important. Also, if there is an exception downstream it needs to be
	 * handled here after a call to {@link #doContinue(ChenileExchange)}
	 * In most cases, you will do fine overriding {@link #doPreProcessing(ChenileExchange)} and 
	 * {@link #doPostProcessing(ChenileExchange)}
	 */
	@Override
	public void execute(ChenileExchange exchange) throws Exception {
		if (bypassInterception(exchange)) {
			doContinue(exchange);
			return;
		}
		doPreProcessing(exchange);
		try {
			doContinue(exchange);
		}catch(Throwable e){
			exchange.setException(e);
		} finally {
			doPostProcessing(exchange);
		}		
	}
	
	/**
	 * Override this to do post processing. 
	 * This will be called even if there is an exception downstream. So make sure
	 * that if you don't want to do any processing in case of exception, check if
	 * {@link ChenileExchange#getException()} returns null value.
	 * If you happen to throw an exception in this method, it will get added to the
	 * set of errors that would ultimately be returned. See also {@link ChenileExchange#setException(Throwable)}
	 *
	 * @param exchange
	 */
	protected void doPostProcessing(ChenileExchange exchange) {}

	/** 
	 * Override this to do pre-processing. This will be called before the service is invoked.
	 * Don't expect to see exception or response being set in {@link ChenileExchange}. However
	 * you can check for {@link ChenileExchange#getBody()} to manipulate the body if this interceptor
	 * is a Chenile post processor or a service specific or operation specific interceptor.<br/>
	 * If this interceptor is a Chenile pre-processor, then  {@link ChenileExchange#getBody()} will
	 * return null. Then you need to look at headers only.
	 *
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

	/**
	 * This is used when an interceptor needs to save the current position in the interceptor chain
	 * with the intent of continuing again from the saved point. This is only required if there is
	 * a need to execute the downstream systems multiple times. Treat the SavePoint return value in an
	 * opaque fashion i.e. don't try to manipulate that data structure. This must only be used in the
	 * subsequent call to {@link #resumeFromSavedPoint(ChainContext.SavePoint, ChenileExchange)}
	 * @param exchange the Chenile Exchange
	 * @returns a save point object that can be called with {@link #resumeFromSavedPoint(ChainContext.SavePoint, ChenileExchange)}
	 */
	protected ChainContext.SavePoint savePoint(ChenileExchange exchange){
		return exchange.getChainContext().savePoint();
	}

	/**
	 * See {@link #savePoint(ChenileExchange)} above
	 * @param savePoint the structure returned by savePoint call.
	 * @param exchange the chenile exchange
	 * @throws Exception this can throw an exception if the downstream systems throw an error
	 */
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
