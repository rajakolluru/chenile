package org.chenile.scheduler.init;

import org.chenile.base.exception.ConfigurationException;
import org.chenile.core.init.BaseInitializer;
import org.chenile.core.model.ChenileConfiguration;
import org.chenile.scheduler.errorcodes.ErrorCodes;
import org.chenile.scheduler.model.SchedulerInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import java.util.Map;

/**
 * Picks up all the file watch resources and registers all of them in
 * {@link ChenileConfiguration} ChenileConfiguration provides for extension
 * points for registering new type of resources. This class uses the extension
 * point to register the file watches
 * 
 * @author Raja Shankar Kolluru
 *
 */

public class ChenileSchedulerInitializer extends BaseInitializer<SchedulerInfo> {

	private static final Logger logger = LoggerFactory.getLogger(ChenileSchedulerInitializer.class);
	public ChenileSchedulerInitializer(Resource[] resources) {
		super(resources);
	}

	protected void registerModelInChenile(SchedulerInfo schedulerInfo) {
		Map<String, SchedulerInfo> map = getExtensionMap(SchedulerInfo.EXTENSION);

		if (map.get(schedulerInfo.getJobName()) != null){
			throw new ConfigurationException(ErrorCodes.MISCONFIGURATION_DUPLICATE_JOB_NAME.getSubError(),
					new Object[] {schedulerInfo.getJobName()});
		}
		logger.debug("Scheduler: Found Job Name {}",schedulerInfo.getJobName());
		map.put(schedulerInfo.getJobName(), schedulerInfo);
	}

	@Override
	protected Class<SchedulerInfo> getModelType() {
		return SchedulerInfo.class;
	}
}
