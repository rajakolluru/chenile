package org.chenile.core.interceptors;

import java.lang.reflect.Method;

import org.chenile.base.exception.ServerException;
import org.chenile.core.context.ChenileExchange;
import org.chenile.core.context.HeaderUtils;
import org.chenile.core.model.ChenileConfiguration;
import org.chenile.core.model.ChenileServiceDefinition;
import org.chenile.core.model.TrajectoryDefinition;
import org.chenile.core.util.MethodUtils;
import org.springframework.beans.factory.annotation.Autowired;
import static org.chenile.core.errorcodes.ErrorCodes.*;

/**
 * Find the correct service to invoke. 
 * The correct service is configured by trajectories. 
 * A trajectory definition can override the service reference for specific trajectories and
 * services.
 * @author Raja Shankar Kolluru
 *
 */
public class ConstructServiceReference extends BaseChenileInterceptor{
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
		Object o = exchange.getHeader(HeaderUtils.MOCK_HEADER);
		if (o == null) return false;
        return o.toString().equalsIgnoreCase("true");
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
			throw new ServerException(UNKNOWN_METHOD.getSubError(),new Object[] {
				exchange.getServiceDefinition().getId(), exchange.getOperationDefinition().getName()
			});
		}
		exchange.setMethod(method);
	}
}
