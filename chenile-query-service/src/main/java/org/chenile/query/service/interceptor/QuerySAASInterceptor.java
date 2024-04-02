package org.chenile.query.service.interceptor;

import java.util.HashMap;
import java.util.Map;

import org.chenile.core.context.ChenileExchange;
import org.chenile.core.context.ContextContainer;
import org.chenile.core.interceptors.BaseChenileInterceptor;
import org.springframework.beans.factory.annotation.Autowired;


import org.chenile.query.model.SearchRequest;

@SuppressWarnings("rawtypes")
public class QuerySAASInterceptor extends BaseChenileInterceptor{

	@Autowired ContextContainer contextContainer;
	
	@Override
	protected void doPreProcessing(ChenileExchange exchange) {
		if (!(exchange.getBody() instanceof SearchRequest)) return; // only process search requests
		
		SearchRequest<?> searchRequest = (SearchRequest) exchange.getBody();
			
		Map<String, Object> systemFilters = searchRequest.getSystemFilters();
		if (null == systemFilters) {
			systemFilters = new HashMap<String,Object>();
			searchRequest.setSystemFilters(systemFilters);
		}
		String tenantId = contextContainer.getTenant();
		String appType = contextContainer.getAppType();
		String tenantType = contextContainer.getTenantType();
		String userId = contextContainer.getUser();
		String employeeId = contextContainer.getEmployeeId();
		systemFilters.put("tenantId", tenantId);
		systemFilters.put("appType", appType);
		systemFilters.put("tenantType", tenantType);
		systemFilters.put("userId", userId);
		systemFilters.put("employeeId", employeeId);
	}
	
}
