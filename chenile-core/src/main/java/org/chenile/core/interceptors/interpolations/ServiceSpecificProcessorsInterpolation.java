package org.chenile.core.interceptors.interpolations;

import java.util.List;

import org.chenile.core.context.ChenileExchange;
import org.chenile.core.model.ChenileConfiguration;
import org.chenile.owiz.Command;
import org.chenile.owiz.impl.InterpolationCommand;
import org.springframework.beans.factory.annotation.Autowired;

public class ServiceSpecificProcessorsInterpolation extends InterpolationCommand<ChenileExchange> {

	@Autowired ChenileConfiguration chenileConfiguration;
	@Override
	protected List<Command<ChenileExchange>> fetchCommands(ChenileExchange exchange) {
		return exchange.getServiceDefinition().getInterceptorCommands();
	}
}
