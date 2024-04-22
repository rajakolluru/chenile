package org.chenile.core.entrypoint;

import org.chenile.core.context.ChenileExchange;
import org.chenile.owiz.OrchExecutor;
import org.springframework.beans.factory.annotation.Autowired;

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
