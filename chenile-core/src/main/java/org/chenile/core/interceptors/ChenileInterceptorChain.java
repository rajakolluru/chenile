package org.chenile.core.interceptors;

import java.util.ArrayList;
import java.util.List;

import org.chenile.core.context.ChenileExchange;
import org.chenile.core.model.ChenileConfiguration;
import org.chenile.owiz.Command;
import org.chenile.owiz.impl.FilterChain;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * The implementation of a flexible interception chain for Chenile.
 * It adds interceptors in the following order:
 * <ul>
 * <li>First it adds all the pre-processors defined in chenile.properties
 * <li>Next it adds all of either service specific interceptors if they exist
 * <li>Next it adds all operation definition specific interceptors
 * <li>Next it constructs {@link ChenileExchange#getApiInvocation()} using {@link ConstructApiInvocation}
 * <li>Next it adds all the post processors defined in chenile.properties
 * <li>Finally, it adds the service invocation which is supposed to invoke the service
 * </ul>
 * @author Raja Shankar Kolluru
 *
 */
public class ChenileInterceptorChain extends FilterChain<ChenileExchange>{

	@Autowired ConstructApiInvocation constructApiInvocation;
	@Autowired ServiceInvoker serviceInvoker;
	@Autowired ChenileConfiguration chenileConfiguration;
	
	@Override
	protected List<Command<?>> toList(ChenileExchange exchange) {
		List<Command<?>> commands = new ArrayList<Command<?>>();
		
		List<Command<?>> interceptors;
		interceptors = chenileConfiguration.getPreProcessorCommands();
		if (interceptors != null && interceptors.size() > 0) commands.addAll(interceptors);
		interceptors = exchange.getOperationDefinition().getInterceptorCommands();
		if (interceptors != null && interceptors.size() > 0) commands.addAll(interceptors);
		
		interceptors = exchange.getServiceDefinition().getInterceptorCommands();
		if (interceptors != null && interceptors.size() > 0) commands.addAll(interceptors);
		commands.add(constructApiInvocation);
		interceptors = chenileConfiguration.getPostProcessorCommands();
		if (interceptors != null && interceptors.size() > 0) commands.addAll(interceptors);	
		commands.add(serviceInvoker);
		return commands;
	}
}
