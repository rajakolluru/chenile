package org.chenile.http.init;

import java.util.List;

import org.chenile.core.init.ChenileTrajectoryInitializer;
import org.chenile.core.model.ChenileConfiguration;
import org.chenile.core.model.TrajectoryDefinition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;

/**
 * Uses a special bean to configure all trajectories. This is not needed since trajectories
 * can be dynamically defined using {@link org.chenile.core.annotation.ConditionalOnTrajectory}
 */
public class AnnotationTrajectoryInitializer {
	@Autowired ApplicationContext applicationContext;
	@Autowired ChenileConfiguration chenileConfiguration;
	
	@EventListener(ApplicationReadyEvent.class)
	@Order(50)
	public void init() throws Exception {
		@SuppressWarnings("unchecked")
		List<TrajectoryDefinition> trajectoryDefinitions = 
				(List<TrajectoryDefinition>)applicationContext.getBean("trajectoryDefinitions");
		for (TrajectoryDefinition td: trajectoryDefinitions) {
			ChenileTrajectoryInitializer.registerTrajectoryDefinition(td, chenileConfiguration, applicationContext);
		}
	}
}
