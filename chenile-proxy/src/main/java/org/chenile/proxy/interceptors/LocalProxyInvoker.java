package org.chenile.proxy.interceptors;

import org.chenile.base.response.GenericResponse;
import org.chenile.core.context.ChenileExchange;
import org.chenile.core.entrypoint.ChenileEntryPoint;
import org.chenile.owiz.Command;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Invokes the Chenile Entry point locally since this service is local.
 * @author Raja Shankar Kolluru
 *
 */
public class LocalProxyInvoker implements Command<ChenileExchange>{
	
	@Autowired
	private ChenileEntryPoint chenileEntryPoint;

	@Override
	public void execute(ChenileExchange exchange) throws Exception {
		exchange.setLocalInvocation(true);
		chenileEntryPoint.execute(exchange);
		GenericResponse<?> resp = (GenericResponse<?>)exchange.getResponse();
		if (resp != null) {
			exchange.setResponse(resp.getData());
		}
	}
}
