package org.chenile.query.service.interceptor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.chenile.core.context.ChenileExchange;
import org.chenile.core.interceptors.BaseChenileInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import org.chenile.query.model.ResponseRow;
import org.chenile.query.model.SearchRequest;
import org.chenile.query.model.SearchResponse;
import org.chenile.query.service.SearchService;

/**
 * For specific queries, we should restrict the results only to authorized
 * users. Typically authorized users are the users and their lines of
 * supervisors. Hence a user can see everyone who lies in their group. Such
 * queries must use the auth_id as the filter and must support a list of
 * authId's This interceptor generates the list of authId's from the current
 * user down to his reportees
 * 
 * @author Raja Shankar Kolluru
 *
 */
@SuppressWarnings("rawtypes")
public class QueryUserFilterInterceptor extends BaseChenileInterceptor {

	@Autowired
	private org.chenile.core.context.ContextContainer contextContainer;
	@Autowired
	private SearchService<Map<String, Object>> searchService;
	private static String USER_REPORTEES_QUERY_NAME = "AuthUser.reportees";
	
	@Value("${query.skip.auth.id:}")
	private String skipAuthIds;

	@Override
	protected void doPreProcessing(ChenileExchange exchange) {
		if (!(exchange.getBody() instanceof SearchRequest))
			return; // only process search requests

		SearchRequest<?> searchRequest = (SearchRequest) exchange.getBody();
		if (searchRequest == null)
			return;
		
		String queryName = searchRequest.getQueryName();
		// dont intercept the user_reportees query itself. This will result in recursion
		if (queryName.equals(USER_REPORTEES_QUERY_NAME))
			return;
		
		/**
		 * If the query has to skip the Auth ID's query.
		 * This is configured in query/datasource.properties.
		 */
		if (skipAuthIds.contains(queryName)) 
				return;

		Map<String, Object> systemFilters = searchRequest.getSystemFilters();
		if (null == systemFilters) {
			systemFilters = new HashMap<String, Object>();
			searchRequest.setSystemFilters(systemFilters);
		}
		enhanceSystemFilters(systemFilters);
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
