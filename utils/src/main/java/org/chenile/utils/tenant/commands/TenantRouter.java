package org.chenile.utils.tenant.commands;

import org.chenile.owiz.impl.Router;

public class TenantRouter extends Router<HeadersAwareContext>{

	@Override
	protected String computeRoutingString(HeadersAwareContext context) throws Exception {
		Object region = context.getHeaders().get("x-aurora-region-id");
		return (region == null)? "": region.toString();
	}

}
