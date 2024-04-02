/**
 * 
 */
package org.chenile.query.controller.dto;

import java.util.Map;

import com.meratransport.query.model.SearchRequest;
import com.meratransport.query.model.SearchResponse;

/**
 * @author Deepak N
 *
 */
public class QueryExchange {

	private SearchRequest<Map<String, Object>> searchRequest;
	private SearchResponse searchResponse;
	private String appType;
	private String queryName;

	public SearchRequest<Map<String, Object>> getSearchRequest() {
		return searchRequest;
	}

	public void setSearchRequest(SearchRequest<Map<String, Object>> searchRequest) {
		this.searchRequest = searchRequest;
	}

	public SearchResponse getSearchResponse() {
		return searchResponse;
	}

	public void setSearchResponse(SearchResponse searchResponse) {
		this.searchResponse = searchResponse;
	}

	public String getAppType() {
		return appType;
	}

	public void setAppType(String appType) {
		this.appType = appType;
	}

	public String getQueryName() {
		return queryName;
	}

	public void setQueryName(String queryName) {
		this.queryName = queryName;
	}
}
