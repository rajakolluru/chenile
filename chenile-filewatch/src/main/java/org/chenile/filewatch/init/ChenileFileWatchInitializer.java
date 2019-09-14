package org.chenile.filewatch.init;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.chenile.core.model.ChenileConfiguration;
import org.chenile.filewatch.model.FileWatchDefinition;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Picks up all the file watch resources and registers all of them in {@link ChenileConfiguration}
 * ChenileConfiguration provides for extension points for registering new type of resources.
 * This class uses the extension point to register the file watches
 * @author Raja Shankar Kolluru
 *
 */
public class ChenileFileWatchInitializer implements InitializingBean{
	private Resource[] chenileFileWatchResources;
	@Autowired ChenileConfiguration chenileConfiguration;
	
	@Autowired FileWatchSubscribersInitializer fileWatchSubscribersInitializer;

	public ChenileFileWatchInitializer(Resource[] chenileFileWatchResources) {
		this.chenileFileWatchResources = chenileFileWatchResources;
	}

	public void init() throws Exception {
		for(Resource chenileResource: chenileFileWatchResources ) {
			registerFileWatch(chenileResource);
		}
	}
	
	private void registerFileWatch(Resource chenileResource) throws Exception{
		FileWatchDefinition fwd = populateFileWatchFromResource(chenileResource);
		addFileWatchDefinition(fwd);	
	}

	private void addFileWatchDefinition(FileWatchDefinition fwd) {
		@SuppressWarnings("unchecked")
		Map<String,FileWatchDefinition> map = (Map<String,FileWatchDefinition>)
				chenileConfiguration.getOtherExtensions().get(FileWatchDefinition.EXTENSION);
		if (map == null) {
			map = new HashMap<String, FileWatchDefinition>();
			chenileConfiguration.getOtherExtensions().put(FileWatchDefinition.EXTENSION, map);
		}
		map.put(fwd.getFileWatchId(), fwd);		
	}

	private FileWatchDefinition populateFileWatchFromResource(Resource chenileResource) throws Exception{
		FileWatchDefinition fwd = populateFileWatchFromInputStream(chenileResource.getInputStream());
		return fwd;
	}

	private FileWatchDefinition populateFileWatchFromInputStream(InputStream inputStream) throws Exception{
		ObjectMapper om = new ObjectMapper();
        FileWatchDefinition fwd = om.readValue(inputStream, FileWatchDefinition.class);
        fwd.setOriginatingModuleName(chenileConfiguration.getModuleName());
		return fwd;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		init();		
	}	
	
}
