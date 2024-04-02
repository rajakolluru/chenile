/**
 * 
 */
package org.chenile.query.controller;

import java.util.Map;

import com.meratransport.query.model.SearchRequest;
import com.meratransport.query.model.SearchResponse;

/**	
 * @author Deepak N
 *
 */
public interface QueryController {

	public SearchResponse getAll(String entity, SearchRequest<Map<String, Object>> searchRequest);
	
	public SearchResponse getById(String entity, String id, SearchRequest<Map<String, Object>> searchRequest);
		
}
