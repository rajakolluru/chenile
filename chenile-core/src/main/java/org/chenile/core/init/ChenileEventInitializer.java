package org.chenile.core.init;

import org.chenile.core.model.ChenileEventDefinition;
import org.springframework.core.io.Resource;

/**
 * Picks up all the event resources and registers all the events in Chenile.
 * @author Raja Shankar Kolluru
 *
 */
public class ChenileEventInitializer extends BaseInitializer<ChenileEventDefinition>{

	public ChenileEventInitializer(Resource[] resources) {
		super(resources);
	}

	@Override
	protected void registerModelInChenile(ChenileEventDefinition event) {
		chenileConfiguration.addEvent(event);
		
	}

	@Override
	protected Class<ChenileEventDefinition> getModelType() {
		return ChenileEventDefinition.class;
	}	
}
