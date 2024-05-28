package org.chenile.core.service;

import org.chenile.base.exception.NotFoundException;
import org.chenile.core.model.ChenileConfiguration;
import org.chenile.core.model.ChenileServiceDefinition;
import org.chenile.core.model.OperationDefinition;
import org.chenile.core.model.TrajectoryDefinition;
import org.chenile.core.service.Info.OperationInfo;
import org.springframework.beans.factory.annotation.Autowired;
import static org.chenile.core.errorcodes.ErrorCodes.*;

public class ChenileInfoServiceImpl implements ChenileInfoService{
	@Autowired private ChenileConfiguration chenileConfiguration;

	@Override
	public Info info() {
		Info versionInfo = new Info();
		versionInfo.version = chenileConfiguration.getVersion();
		versionInfo.moduleName = chenileConfiguration.getModuleName();
		for (ChenileServiceDefinition csd: chenileConfiguration.getServices().values()) {
			Info.ServiceInfo s = new Info.ServiceInfo(csd.getId());
			for (OperationDefinition od: csd.getOperations()) {
				OperationInfo o = new OperationInfo();
				o.name = od.getName();
				o.url = od.getUrl();
				o.method = od.getHttpMethod().name();
				s.operations.add(o);
			}
			versionInfo.services.add(s);
		}
		
		return versionInfo;
	}

	@Override
	public HealthCheckInfo healthCheck(String currTrajectory,String service) {
		ChenileServiceDefinition desc = chenileConfiguration.getServices().get(service);
		if (desc == null) {
			throw new NotFoundException(SERVICE_NOT_FOUND.getSubError(), new Object[] {service});
		}
		HealthChecker healthChecker = getHealthChecker(currTrajectory,desc);
		if (healthChecker == null) {
			throw new NotFoundException(HEALTH_CHECKER_NOT_CONFIGURED.getSubError(), new Object[] {service});
		}
		return healthChecker.healthCheck();
	}
	
	@Override 
	public ChenileServiceDefinition serviceInfo(String service) {
		ChenileServiceDefinition csd = chenileConfiguration.getServices().get(service);
		if (csd == null) {
			throw new NotFoundException(SERVICE_NOT_FOUND.getSubError(), new Object[] {service});
		}
		return csd;
	}
	
	protected HealthChecker getHealthChecker(String currTrajectory,ChenileServiceDefinition csd) {
		HealthChecker defaulthealthChecker = csd.getHealthChecker();
		TrajectoryDefinition td = chenileConfiguration.getTrajectories().get(currTrajectory);
		if (td == null) return defaulthealthChecker;
		// Has the trajectory overridden this particular service's health checker?
		HealthChecker hc = td.getHealthCheckerReference(csd.getId());
		if (hc == null) return defaulthealthChecker; 
		return hc;	
	}
	
	

}
