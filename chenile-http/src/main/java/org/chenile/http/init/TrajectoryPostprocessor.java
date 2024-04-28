package org.chenile.http.init;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.chenile.core.annotation.ConditionalHealthCheckOnTrajectory;
import org.chenile.core.annotation.ConditionalOnTrajectory;
import org.chenile.core.model.TrajectoryDefinition;
import org.chenile.core.model.TrajectoryOverride;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.core.type.AnnotatedTypeMetadata;

public class TrajectoryPostprocessor implements BeanFactoryPostProcessor{
	
	@Override
	public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
		List<TrajectoryDefinition> list = new ArrayList<>();
		Map<String,TrajectoryDefinition> tmap = new HashMap<>();
		for (String name : beanFactory.getBeanDefinitionNames()) {
			BeanDefinition bd = beanFactory.getBeanDefinition(name);
			if (bd.getSource() instanceof AnnotatedTypeMetadata metadata) {
                boolean healthCheckOverride = false;
				Map<String, Object> attributes = metadata.getAnnotationAttributes(ConditionalOnTrajectory.class.getName());
				if (null == attributes) {
					attributes = metadata.getAnnotationAttributes(ConditionalHealthCheckOnTrajectory.class.getName());
					if (null == attributes) continue;
					healthCheckOverride = true;
				}
							
				String id = (String)attributes.get("id");
				String service = (String)attributes.get("service");
				if (id == null || service == null) continue;
				TrajectoryDefinition td =  tmap.get(id);
				if (td == null) {
					td = new TrajectoryDefinition();
					tmap.put(id, td);
					list.add(td);
				}
				td.setId(id);				
				
				TrajectoryOverride to = td.getTrajectoryOverrides().get(service);
				if (null == to) {
					to = new TrajectoryOverride();
					to.setTrajectoryId(id);			
					to.setServiceId(service);
					td.getTrajectoryOverrides().put(service, to);
				}
				
				if (healthCheckOverride) {
					to.setNewHealthCheckerReferenceId(name);
				}else { // service override
					to.setNewServiceReferenceId(name);
				}
			}
		}
		// create a temporary bean. We cannot access ChenileConfiguration yet in a post processor
		beanFactory.registerSingleton("trajectoryDefinitions",list);
	}
}
