package org.chenile.filewatch.init;

import java.util.Map;

import org.chenile.core.init.BaseInitializer;
import org.chenile.core.model.ChenileConfiguration;
import org.chenile.filewatch.model.FileWatchDefinition;
import org.springframework.core.io.Resource;

/**
 * Picks up all the file watch resources and registers all of them in {@link ChenileConfiguration}
 * ChenileConfiguration provides for extension points for registering new type of resources.
 * This class uses the extension point to register the file watches
 * @author Raja Shankar Kolluru
 *
 */
public class ChenileFileWatchInitializer extends BaseInitializer<FileWatchDefinition>{

	public ChenileFileWatchInitializer(Resource[] resources) {
		super(resources);
	}

	protected void registerModelInChenile(FileWatchDefinition fwd) {
		Map<String,FileWatchDefinition> map = getExtensionMap(FileWatchDefinition.EXTENSION);
		map.put(fwd.getFileWatchId(), fwd);		
	}

	@Override
	protected Class<FileWatchDefinition> getModelType() {
		return FileWatchDefinition.class;
	}	
	
}
