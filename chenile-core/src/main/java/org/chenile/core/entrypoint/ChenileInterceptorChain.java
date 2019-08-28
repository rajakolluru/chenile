package org.chenile.core.entrypoint;

import java.util.ArrayList;
import java.util.List;

import org.chenile.core.context.ChenileExchange;
import org.chenile.core.interceptors.ServiceInvoker;
import org.chenile.core.model.ChenileConfiguration;
import org.chenile.owiz.Command;
import org.chenile.owiz.impl.FilterChain;
import org.springframework.beans.factory.annotation.Autowired;

public class ChenileInterceptorChain extends FilterChain<ChenileExchange>{

	@Autowired ServiceInvoker serviceInvoker;
	@Autowired ChenileConfiguration chenileConfiguration;
	/**
	 * Override this method to return something specific to the current ChenileServiceDefinition and OperationDefinition
	 */
	@Override
	protected List<Command<?>> toList(ChenileExchange exchange) {
		List<Command<?>> commands = new ArrayList<Command<?>>();
		
		List<Command<?>> interceptors;
		interceptors = chenileConfiguration.getPreProcessorCommands();
		if (interceptors != null && interceptors.size() > 0) commands.addAll(interceptors);
		interceptors = exchange.getOperationDefinition().getInterceptorCommands();
		if (interceptors == null)
			interceptors = exchange.getServiceDefinition().getInterceptorCommands();
		if (interceptors != null && interceptors.size() > 0) commands.addAll(interceptors);
		interceptors = chenileConfiguration.getPostProcessorCommands();
		if (interceptors != null && interceptors.size() > 0) commands.addAll(interceptors);
		commands.add(serviceInvoker);		
		return commands;
	}
}
