package org.chenile.core.init;

import org.chenile.core.model.ChenileConfiguration;
import org.chenile.core.model.TrajectoryDefinition;
import org.chenile.core.model.TrajectoryOverride;
import org.chenile.core.service.HealthChecker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.Resource;

/**
 * Initializes a trajectory definition.
 */
public class ChenileTrajectoryInitializer extends BaseInitializer<TrajectoryDefinition>{

	@Autowired ApplicationContext applicationContext;
	public ChenileTrajectoryInitializer(Resource[] resources) {
		super(resources);
	}
	
	public static void registerTrajectoryDefinition(TrajectoryDefinition trajectoryDefinition, 
			ChenileConfiguration chenileConfiguration, ApplicationContext applicationContext) {
		for(TrajectoryOverride to: trajectoryDefinition.getTrajectoryOverrides().values()) {
			if (to.getNewServiceReferenceId() != null) {
				Object ref = applicationContext.getBean(to.getNewServiceReferenceId());
				to.setNewServiceReference(ref);
			}
			if (to.getNewHealthCheckerReferenceId() != null) {
				HealthChecker ref = (HealthChecker)applicationContext.getBean(to.getNewHealthCheckerReferenceId());
				to.setNewHealthCheckerReference(ref);
			}
			
		}
		chenileConfiguration.addTrajectory(trajectoryDefinition);
	}

	@Override
	protected void registerModelInChenile(TrajectoryDefinition trajectoryDefinition) {
		registerTrajectoryDefinition(trajectoryDefinition,chenileConfiguration, applicationContext);
	}

	@Override
	protected Class<TrajectoryDefinition> getModelType() {
		return TrajectoryDefinition.class;
	}

}
