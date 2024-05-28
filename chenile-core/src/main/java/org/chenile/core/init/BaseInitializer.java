package org.chenile.core.init;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.chenile.base.exception.ServerException;
import org.chenile.core.errorcodes.ErrorCodes;
import org.chenile.core.model.ChenileConfiguration;
import org.chenile.core.model.ModuleAware;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;

import com.fasterxml.jackson.databind.ObjectMapper;

public abstract class BaseInitializer<T> implements InitializingBean{
	protected Resource[] chenileJsonResources;
	@Autowired protected ChenileConfiguration chenileConfiguration;
	
	public BaseInitializer(Resource[] resources) {
		this.chenileJsonResources = resources;
	}
	
	@Override
	public void afterPropertiesSet() throws Exception {
		init();		
	}
	
	protected void init() {
		for (Resource resource: chenileJsonResources) {
			registerResource(resource);
		}
	}
	
	protected void registerResource(Resource resource) {
		T resourceModel = populateModelFromResource(resource);
		registerModelInChenile(resourceModel);
	}
	
	protected abstract void registerModelInChenile(T resourceModel);

	private T populateModelFromResource(Resource chenileResource) {
		T model;
		try {
			model = populateEventFromInputStream(chenileResource.getInputStream());
			return model;
		} catch (Exception e) {
			throw new ServerException(ErrorCodes.CANNOT_CONFIGURE_CHENILE_RESOURCE.getSubError(), 
					new Object[]{chenileResource.getFilename()},e);
		}
		
	}

	private T populateEventFromInputStream(InputStream inputStream) throws Exception{	
		ObjectMapper om = new ObjectMapper();
		T model = om.readValue(inputStream, getModelType());
		if (model instanceof ModuleAware)
			((ModuleAware) model).setOriginatingModuleName(chenileConfiguration.getModuleName());
		return model;
	}

	protected abstract Class<T> getModelType() ;
	
	protected Map<String,T> getExtensionMap(String extensionName){
		@SuppressWarnings("unchecked")
		Map<String,T> map = (Map<String,T>)
				chenileConfiguration.getOtherExtensions().get(extensionName);
		if (map == null) {
			map = new HashMap<String, T>();
			chenileConfiguration.getOtherExtensions().put(extensionName, map);
		}
		return map;
	}

}
