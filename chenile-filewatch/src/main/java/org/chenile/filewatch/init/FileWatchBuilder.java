package org.chenile.filewatch.init;

import java.util.Map;

import org.chenile.core.model.ChenileConfiguration;
import org.chenile.filewatch.handler.FileWatcherExecutorService;
import org.chenile.filewatch.model.FileWatchDefinition;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Builds the file watcher module. 
 * Takes all the  file watch IDs from every {@link FileWatchDefinition} and registers them 
 * against a file watcher. Starts the {@link FileWatcherExecutorService}
 * @author Raja Shankar Kolluru
 *
 */
public class FileWatchBuilder {

	@Autowired
	private ChenileConfiguration chenileConfiguration;
	@Autowired FileWatcherExecutorService fileWatcherExecutorService;
	
	public void build() throws Exception {
		@SuppressWarnings("unchecked")
		Map<String,FileWatchDefinition> map = (Map<String,FileWatchDefinition>)
				chenileConfiguration.getOtherExtensions().get(FileWatchDefinition.EXTENSION);
		if (map == null) return; //nothing to do
		for (FileWatchDefinition fwd : map.values()) {
			if(!fwd.getOriginatingModuleName().equals(chenileConfiguration.getModuleName())) continue;
			configureEntryPoint(fwd);
		}
		fileWatcherExecutorService.startWatch();
	}

	private void configureEntryPoint(FileWatchDefinition fwd) throws Exception{
		fileWatcherExecutorService.registerWatch(fwd);
	}

}
