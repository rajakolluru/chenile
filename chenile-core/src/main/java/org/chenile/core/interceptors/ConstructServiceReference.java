package org.chenile.core.interceptors;

import java.lang.reflect.Method;

import org.chenile.base.exception.ErrorNumException;
import org.chenile.core.context.ChenileExchange;
import org.chenile.core.model.ChenileConfiguration;
import org.chenile.core.model.ChenileServiceDefinition;
import org.chenile.core.model.TrajectoryDefinition;
import org.chenile.core.util.MethodUtils;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Find the correct service to invoke. 
 * The correct service is configured by trajectories. 
 * A trajectory definition can override the service reference for specific trajectories and
 * services.
 * @author Raja Shankar Kolluru
 *
 */
public class ConstructServiceReference extends BaseChenileInterceptor{
	private static final String MOCK_HEADER = "x-chenile-mock-mode";
	@Autowired ChenileConfiguration chenileConfiguration;
	private final String trajectoryHeaderName ;
	
	public ConstructServiceReference(String trajectoryHeaderName) {
		this.trajectoryHeaderName = trajectoryHeaderName;
	}
	@Override
	protected void doPreProcessing(ChenileExchange exchange) {
		if (exchange.getServiceReference() != null) return;
		ChenileServiceDefinition serviceDefinition = exchange.getServiceDefinition();
		if (isMock(exchange)) {
			populateServiceRef(exchange, serviceDefinition.getMockName(),serviceDefinition.getMockServiceReference());
			return;
		}
		if (trajectoryDoesNotOverrideService(exchange)) {
			exchange.setServiceReferenceId(serviceDefinition.getId());
			exchange.setServiceReference(serviceDefinition.getServiceReference());
			exchange.setMethod(exchange.getOperationDefinition().getMethod());
		}
	}
	
	private boolean isMock(ChenileExchange exchange) {
		if (exchange.isInvokeMock()) return true;
		Object o = exchange.getHeader(MOCK_HEADER);
		if (o == null) return false;
		if (o.toString().equalsIgnoreCase("true"))return true;
		return false;
	}
	
	private boolean trajectoryDoesNotOverrideService(ChenileExchange exchange) {
		String trajectoryId = exchange.getHeader(trajectoryHeaderName,String.class);
		if (trajectoryId == null) return true;
		TrajectoryDefinition td = chenileConfiguration.getTrajectories().get(trajectoryId);
		if (td == null) return true;
		String serviceId = exchange.getServiceDefinition().getId();
		Object ref = td.getServiceReference(serviceId);
		String deflectedServiceId = td.getServiceReferenceId(serviceId);
		if (ref == null) return true;
		populateServiceRef(exchange,deflectedServiceId,ref);
		return false;
	}
	
	private void populateServiceRef(ChenileExchange exchange, String serviceId,Object ref) {
		exchange.setServiceReference(ref);
		exchange.setServiceReferenceId(serviceId);
		Method method = MethodUtils.computeMethod(ref.getClass(), exchange.getOperationDefinition());
		if (method == null){
			throw new ErrorNumException(500,508,new Object[] {
				exchange.getServiceDefinition().getId(), exchange.getOperationDefinition().getName()
			});
		}
		exchange.setMethod(method);
	}
}
