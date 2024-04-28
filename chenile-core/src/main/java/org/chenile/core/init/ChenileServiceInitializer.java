package org.chenile.core.init;

import org.springframework.core.io.Resource;

public class ChenileServiceInitializer extends AbstractServiceInitializer{
	
	private final Resource[] chenileServiceJsonResources;

	public ChenileServiceInitializer(Resource[] chenileServiceJsonResources) {	
		this.chenileServiceJsonResources = chenileServiceJsonResources;
	}

	@Override
	public void init() throws Exception {
		for(Resource chenileResource: chenileServiceJsonResources ) {
			// look up service registry and push the service json
			registerService(chenileResource);
		}
	}

}
