/**
 * 
 */
package org.chenile.query.controller.commands;

import java.util.HashMap;
import java.util.Map;

import org.chenile.owiz.Command;
import org.chenile.query.controller.dto.QueryExchange;
import org.springframework.beans.factory.annotation.Autowired;

import com.meratransport.context.ContextContainer;
import com.meratransport.query.model.SearchRequest;

/**
 * @author Deepak N
 *
 */
public class EnhanceSystemFiltersCommand implements Command<QueryExchange> {
	
	@Autowired private ContextContainer contextContainer;

	@Override
	public void execute(QueryExchange exchange) throws Exception {
		SearchRequest<Map<String, Object>> searchRequest = exchange.getSearchRequest();
		Map<String, Object> systemFilters = searchRequest.getSystemFilters();
		if (null == systemFilters) {
			systemFilters = new HashMap<String,Object>();
			searchRequest.setSystemFilters(systemFilters);
		}
		String tenantId = contextContainer.getTenant();
		String appType = contextContainer.getAppType();
		String tenantType = contextContainer.getTenantType();
		String userId = contextContainer.getUser();
		systemFilters.put("tenantId", tenantId);
		systemFilters.put("appType", appType);
		systemFilters.put("tenantType", tenantType);	
		systemFilters.put("userId", userId);	
		
		searchRequest.setSystemFilters(systemFilters);
		exchange.setSearchRequest(searchRequest);
	}

}
