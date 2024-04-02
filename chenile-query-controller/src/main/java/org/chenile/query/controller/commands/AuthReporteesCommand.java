/**
 * 
 */
package org.chenile.query.controller.commands;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.chenile.owiz.Command;
import org.chenile.query.controller.dto.QueryExchange;
import org.springframework.beans.factory.annotation.Autowired;

import com.meratransport.context.ContextContainer;
import com.meratransport.query.model.ResponseRow;
import com.meratransport.query.model.SearchRequest;
import com.meratransport.query.model.SearchResponse;
import com.meratransport.query.service.SearchService;

/**
 * @author Deepak N
 *
 */
public class AuthReporteesCommand implements Command<QueryExchange> {
	
	@Autowired private ContextContainer contextContainer;
	@Autowired private SearchService<Map<String, Object>> searchService;
	private static String USER_REPORTEES_QUERY_NAME = "AuthUser.reportees";

	@Override
	public void execute(QueryExchange exchange) throws Exception {
		SearchRequest<Map<String, Object>> searchRequest = exchange.getSearchRequest();
		if (searchRequest == null)
			return;
		
		String queryName = searchRequest.getQueryName();
		// dont intercept the user_reportees query itself. This will result in recursion
		if (queryName.equals(USER_REPORTEES_QUERY_NAME))
			return;
		
		Map<String, Object> systemFilters = searchRequest.getSystemFilters();
		if (null == systemFilters) {
			systemFilters = new HashMap<String, Object>();
			searchRequest.setSystemFilters(systemFilters);
		}
		enhanceSystemFilters(systemFilters);	
		
		searchRequest.setSystemFilters(systemFilters);
		exchange.setSearchRequest(searchRequest);
	}
	
	protected void enhanceSystemFilters(Map<String, Object> systemFilters) {
		String userId = contextContainer.getEmployeeId();
		List<String> authIds = executeUserReporteesQuery(userId);
		systemFilters.put("authIds", authIds);
	}

	protected List<String> executeUserReporteesQuery(String userId) {
		List<String> authIds = new ArrayList<String>();
		SearchRequest<Map<String, Object>> searchRequest = new SearchRequest<Map<String, Object>>();
		searchRequest.setQueryName(USER_REPORTEES_QUERY_NAME);
		Map<String, Object> filters = new HashMap<>();
		filters.put("employeeId", userId);
		searchRequest.setFilters(filters);
		SearchResponse searchResponse = searchService.search(searchRequest);
		for (ResponseRow rr : searchResponse.getList()) {
			@SuppressWarnings("unchecked")
			Map<String, String> row = (Map<String, String>) rr.getRow();
			authIds.add(row.get("authId"));
		}
		authIds.add(userId);
		return authIds;
	}

}
