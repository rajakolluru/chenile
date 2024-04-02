/**
 * 
 */
package org.chenile.query.controller.impl;

import java.util.HashMap;
import java.util.Map;

import org.chenile.base.exception.ErrorNumException;
import org.chenile.base.exception.ServerException;
import org.chenile.owiz.OrchExecutor;
import org.chenile.query.controller.dto.QueryExchange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.meratransport.context.ContextContainer;
import org.chenile.query.controller.QueryController;
import com.meratransport.query.model.SearchRequest;
import com.meratransport.query.model.SearchResponse;
import com.meratransport.query.service.SearchService;

public class QueryControllerImpl implements QueryController {
	
	private static final Logger LOG = LoggerFactory.getLogger(QueryControllerImpl.class);

	@Autowired
	private SearchService<Map<String, Object>> searchService;
	
	@Autowired private ContextContainer contextContainer;
	
	@Autowired
	private OrchExecutor<QueryExchange> queryControllerOrchExecutor;

//	@Override
//	public SearchResponse getAll(String entity, SearchRequest<Map<String, Object>> searchRequest) {
//		SearchResponse searchResponse = null;
//		final long startTime = System.currentTimeMillis();
//		try {
////			String queryId = GetAllEntities.valueOf(entity).get();
////			searchRequest.setQueryName(queryId);			
//			searchResponse = searchService.search(searchRequest);
//			final long endTime = System.currentTimeMillis();
//			LOG.info("Total time for the query " + searchRequest.getQueryName() + " to be executed: " + (endTime - startTime) / 1000 + " second(s).");
//		} catch (IllegalArgumentException e) {
//			LOG.error("Unable to retrieve the list: ", e);
//			throw new ServerException("Entity not supported.");
//		} catch (Exception e) {
//			e.printStackTrace();
//			LOG.error("Unable to retrieve the list: ", e);
//			if (e instanceof ErrorNumException) {
//				throw e;
//			}
//			throw new ServerException("Unable to retrieve the list.");
//		}
//		return searchResponse;
//	}
	
	@Override
	public SearchResponse getAll(String entity, SearchRequest<Map<String, Object>> searchRequest) {
		QueryExchange exchange = doProcess(searchRequest);
		SearchResponse searchResponse = exchange.getSearchResponse();
		return searchResponse;
	}
	
	private QueryExchange doProcess(SearchRequest<Map<String, Object>> request) {
		QueryExchange exchange = makeProfileExchange(request);
		invokeOrchExecutor(exchange);
		return exchange;
	}
	
	private QueryExchange makeProfileExchange(SearchRequest<Map<String, Object>> request) {
		QueryExchange queryExchange = new QueryExchange();
		if (null == request) {
			throw new ServerException("Need a request body to process.");
		}
		queryExchange.setSearchRequest(request);
		queryExchange.setQueryName(request.getQueryName());
		queryExchange.setAppType(contextContainer.getAppType());
		return queryExchange;
	}
	
	public void invokeOrchExecutor(QueryExchange profileExchange) {
		try {			
			queryControllerOrchExecutor.execute(profileExchange);
		} catch (ErrorNumException e) {
			LOG.error("Unable to process the profile request: ", e);
			throw e;
		} catch (Exception e) {
//			if (e instanceof ErrorNumException) {
//				throw (ErrorNumException) e;
//			}
			e.printStackTrace();
			LOG.error("Unable to process the profile request: ", e);
			throw new ServerException("Unable to process the search request.");
		}	
	}

	@Override
	public SearchResponse getById(String entity, String id, SearchRequest<Map<String, Object>> searchRequest) {
		SearchResponse searchResponse = null;
		try {
//			String queryId = GetByIdEntities.valueOf(entity).get();
//			searchRequest.setQueryName(queryId);
			Map<String, Object> filters = searchRequest.getFilters();
			if (null == filters || filters.isEmpty()) {
				filters = new HashMap<>();
			}
			filters.put("id", id);
			searchRequest.setFilters(filters);
			QueryExchange exchange = doProcess(searchRequest);
			searchResponse = exchange.getSearchResponse();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
			throw new ServerException("Entity not supported.");
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServerException("Unable to retrieve.");
		}
		return searchResponse;
	}	
}
