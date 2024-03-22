package org.chenile.proxy.interceptors;

import org.chenile.core.context.ChenileExchange;
import org.chenile.core.model.ChenileConfiguration;
import org.chenile.core.model.ChenileServiceDefinition;
import org.chenile.owiz.impl.Router;
import org.chenile.proxy.builder.ProxyBuilder;
import org.chenile.proxy.builder.ProxyBuilder.ProxyMode;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Returns the correct route based on if the service is local or remote
 * @author Raja Shankar Kolluru
 *
 */
public class ProxyTypeRouter extends Router<ChenileExchange>{

	@Autowired ChenileConfiguration chenileConfiguration;
	@Override
	protected String computeRoutingString(ChenileExchange exchange) throws Exception {		
		return proxyMode(exchange);			
	}
	
	private String proxyMode(ChenileExchange exchange) {
		ProxyMode mode = exchange.getHeader(ProxyBuilder.PROXYMODE, ProxyMode.class);
		switch(mode) {
		case LOCAL:
		case REMOTE:
			return mode.name();
		case COMPUTE_DYNAMICALLY:
		default:
			return computeDynamic(exchange);			
		}
	}
	
	private String computeDynamic(ChenileExchange exchange) {
		ChenileServiceDefinition sd = exchange.getServiceDefinition();
		if(chenileConfiguration.getModuleName().equals(sd.getModuleName()))
			return ProxyMode.LOCAL.name();
		else
			return ProxyMode.REMOTE.name();	
	}

}
