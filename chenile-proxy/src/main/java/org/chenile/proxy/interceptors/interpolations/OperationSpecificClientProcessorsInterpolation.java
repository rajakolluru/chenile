package org.chenile.proxy.interceptors.interpolations;

import java.util.List;

import org.chenile.core.context.ChenileExchange;
import org.chenile.core.model.ChenileConfiguration;
import org.chenile.owiz.Command;
import org.chenile.owiz.impl.InterpolationCommand;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Interpolates all the operation specific processors on the client side for Chenile Proxy
 */
public class OperationSpecificClientProcessorsInterpolation extends InterpolationCommand<ChenileExchange> {

	@Autowired ChenileConfiguration chenileConfiguration;
	@Override
	protected List<Command<ChenileExchange>> fetchCommands(ChenileExchange exchange) {
		return exchange.getOperationDefinition().getClientInterceptorCommands();
	}
}
