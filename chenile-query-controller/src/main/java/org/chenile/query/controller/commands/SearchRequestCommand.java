/**
 * 
 */
package org.chenile.query.controller.commands;

import java.util.Map;

import org.chenile.base.exception.ErrorNumException;
import org.chenile.base.exception.ServerException;
import org.chenile.owiz.Command;
import org.chenile.query.controller.dto.QueryExchange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.meratransport.query.model.SearchRequest;
import com.meratransport.query.model.SearchResponse;
import com.meratransport.query.service.SearchService;

/**
 * @author Deepak N
 *
 */
public class SearchRequestCommand implements Command<QueryExchange> {
	
	private static final Logger LOG = LoggerFactory.getLogger(SearchRequestCommand.class);
	
	@Autowired
	private SearchService<Map<String, Object>> searchService;

	@Override
	public void execute(QueryExchange context) throws Exception {
		SearchRequest<Map<String, Object>> searchRequest = context.getSearchRequest();
		SearchResponse searchResponse = null;
		final long startTime = System.currentTimeMillis();
		try {
//			String queryId = GetAllEntities.valueOf(entity).get();
//			searchRequest.setQueryName(queryId);			
			searchResponse = searchService.search(searchRequest);
			context.setSearchResponse(searchResponse);
			final long endTime = System.currentTimeMillis();
			LOG.debug("Total time for the query " + searchRequest.getQueryName() + " to be executed: " + (endTime - startTime) / 100 + " millisecond(s).");
		} catch (IllegalArgumentException e) {
			LOG.error("Unable to retrieve the list: ", e);
			throw new ServerException("Entity not supported.");
		} catch (Exception e) {
			e.printStackTrace();
			LOG.error("Unable to retrieve the list: ", e);
			if (e instanceof ErrorNumException) {
				throw e;
			}
			throw new ServerException("Unable to retrieve the list.");
		}
	}

}
