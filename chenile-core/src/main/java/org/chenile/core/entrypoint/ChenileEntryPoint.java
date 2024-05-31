package org.chenile.core.entrypoint;

import org.chenile.core.context.ChenileExchange;
import org.chenile.owiz.OrchExecutor;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * The entry point to the Chenile highway. All transports must eventually delegate to this class.
 * It uses an OWIZ orchestration chain to trigger all the interceptors and eventually the
 * service class.
 */
public class ChenileEntryPoint {
	@Autowired private OrchExecutor<ChenileExchange> chenileOrchExecutor;
	
	public void execute(ChenileExchange chenileExchange) {
		try {
			chenileOrchExecutor.execute(chenileExchange);
		}catch(Exception e) {
			chenileExchange.setException(e);
		}
	}
}
