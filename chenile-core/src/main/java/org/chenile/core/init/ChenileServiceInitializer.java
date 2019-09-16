package org.chenile.core.init;

import org.springframework.core.io.Resource;

public class ChenileServiceInitializer extends AbstractServiceInitializer{
	
	private Resource[] chenileServiceJsonResources;

	public ChenileServiceInitializer(Resource[] chenileServiceJsonResources) {	
		this.chenileServiceJsonResources = chenileServiceJsonResources;
	}

	@Override
	public void init() throws Exception {
		for(Resource chenileResource: chenileServiceJsonResources ) {
			registerService(chenileResource);
		}
	}

}
