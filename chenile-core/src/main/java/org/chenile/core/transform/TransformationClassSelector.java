package org.chenile.core.transform;

import org.chenile.core.context.ChenileExchange;
import org.chenile.core.model.ChenileServiceDefinition;
import org.chenile.core.model.OperationDefinition;
import org.chenile.owiz.Command;

/**
 * Determines the target type for the body of the ChenileExchange and populates that into 
 * {@link ChenileExchange#getBodyType()}
 * Uses registered bodyTypeSeletors either at {@link ChenileServiceDefinition} or {@link OperationDefinition}
 * 
 * @author Raja Shankar Kolluru
 *
 */

public class TransformationClassSelector implements Command<ChenileExchange>{

	@Override
	public void execute(ChenileExchange exchange) throws Exception {
		// Has a body type selector been registered against the service definition or operation definition
		// if so invoke it
		Command<ChenileExchange> bts = obtainBodyTypeSelector(exchange);
		if (bts != null) {
			bts.execute(exchange);
			return;
		}
		setDefaultBodyType(exchange);
	}
	
	private Command<ChenileExchange> obtainBodyTypeSelector(ChenileExchange exchange) {
		ChenileServiceDefinition sd = exchange.getServiceDefinition();
		OperationDefinition od = exchange.getOperationDefinition();
		if (od.getBodyTypeSelector() != null)
			return od.getBodyTypeSelector();
		else 
			return sd.getBodyTypeSelector();
	}
	
	private void setDefaultBodyType(ChenileExchange exchange) {
		OperationDefinition od = exchange.getOperationDefinition();
		if (od.getInput() != null) {
			exchange.setBodyType(od.getInput());
		}
	}
}
