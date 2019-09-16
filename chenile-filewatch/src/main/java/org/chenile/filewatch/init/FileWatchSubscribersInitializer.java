package org.chenile.filewatch.init;

import java.util.Map;

import org.chenile.base.exception.ServerException;
import org.chenile.core.errorcodes.ErrorCodes;
import org.chenile.core.model.ChenileConfiguration;
import org.chenile.core.model.ChenileServiceDefinition;
import org.chenile.core.model.OperationDefinition;
import org.chenile.core.model.SubscriberVO;
import org.chenile.filewatch.model.FileWatchDefinition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;

/**
 * Runs after all the file watches and services are registered in Chenile.
 * It goes through all services and registers the operations to the subscribed file watchers. 
 * This will enable Chenile to trigger all the subscribers upon the receipt of a file watch.
 * @author Raja Shankar Kolluru
 *
 */
public class FileWatchSubscribersInitializer {
	@Autowired ChenileConfiguration chenileConfiguration;
	@Autowired FileWatchBuilder fileWatchBuilder;
	
	@EventListener(ApplicationReadyEvent.class)
	public void init() throws Exception {
		for (ChenileServiceDefinition s : chenileConfiguration.getServices().values()) {
			// ignore if the service does not belong to the current module.
			if (!s.getModuleName().equals(chenileConfiguration.getModuleName())) continue; 
			for (OperationDefinition operationDefinition : s.getOperations()) {
				String fileWatchId = operationDefinition.getFileWatchId();
				if (fileWatchId == null ) continue;
				registerSubscriber(s,operationDefinition,fileWatchId);				
			}
		}
		fileWatchBuilder.build();
	}

	private void registerSubscriber(ChenileServiceDefinition s, OperationDefinition operationDefinition, String fileWatchId) {
		@SuppressWarnings("unchecked")
		Map<String, FileWatchDefinition> map = (Map<String,FileWatchDefinition>)
				chenileConfiguration.getOtherExtensions().get(FileWatchDefinition.EXTENSION);
		if (map == null) {
			throw new ServerException(ErrorCodes.MISCONFIGURATION.getSubError(), "Operation " + s.getId() + "." +
					operationDefinition.getName() + " misconfigured. File Watch  subscribed to " + fileWatchId +
					" does not exist in the file watch configuration.");
		}
		FileWatchDefinition fwd = map.get(fileWatchId);
		if (fwd == null) {
			throw new ServerException(ErrorCodes.MISCONFIGURATION.getSubError(), "Operation " + s.getId() + "." +
					operationDefinition.getName() + " misconfigured. File Watch  subscribed to " + fileWatchId +
					" does not exist in the file watch configuration.");
		}
		// Record class must match the class accepted by the body of the operation
		if (!fwd.getRecordClass().equals(operationDefinition.getInput())) {
			throw new ServerException(ErrorCodes.MISCONFIGURATION.getSubError(), "Operation " + s.getId() + "." +
					operationDefinition.getName() + " misconfigured. Watch ID subscribed to " + fileWatchId +
					" has a type " + fwd.getRecordClass() + " that does not match the type of body input "
					+ operationDefinition.getInput() + " for this operation.");
		}
		
		fwd.addSubscriber(new SubscriberVO(s,operationDefinition));
	}

}
