package org.chenile.core.init;

import java.io.InputStream;

import org.chenile.core.model.ChenileConfiguration;
import org.chenile.core.model.ChenileEventDefinition;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Picks up all the event resources and registers all the events in Chenile.
 * @author Raja Shankar Kolluru
 *
 */
public class ChenileEventInitializer implements InitializingBean{
	private Resource[] chenileEventJsonResources;
	@Autowired ChenileConfiguration chenileConfiguration;

	public ChenileEventInitializer(Resource[] chenileEventJsonResources) {
		this.chenileEventJsonResources = chenileEventJsonResources;
	}

	public void init() throws Exception {
		for(Resource chenileResource: chenileEventJsonResources ) {
			registerEvent(chenileResource);
		}
	}
	
	private void registerEvent(Resource chenileResource) throws Exception{
		ChenileEventDefinition event = populateEventFromResource(chenileResource);
		chenileConfiguration.addEvent(event);		
	}

	private ChenileEventDefinition populateEventFromResource(Resource chenileResource) throws Exception{
		ChenileEventDefinition ced = populateEventFromInputStream(chenileResource.getInputStream());
		return ced;
	}

	private ChenileEventDefinition populateEventFromInputStream(InputStream inputStream) throws Exception{
		ObjectMapper om = new ObjectMapper();
        ChenileEventDefinition ced = om.readValue(inputStream, ChenileEventDefinition.class);
        ced.setOriginatingModuleName(chenileConfiguration.getModuleName());
		return ced;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		init();		
	}	
	
}
