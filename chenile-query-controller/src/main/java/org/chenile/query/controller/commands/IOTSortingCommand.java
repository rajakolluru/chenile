/**
 * 
 */
package org.chenile.query.controller.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.chenile.owiz.Command;

import org.chenile.query.controller.dto.QueryExchange;
import com.meratransport.query.model.SearchRequest;
import com.meratransport.query.model.SortCriterion;

/**
 * @author Deepak N
 *
 */
public class IOTSortingCommand implements Command<QueryExchange> {

	@Override
	public void execute(QueryExchange exchange) throws Exception {
		SearchRequest<Map<String, Object>> searchRequest = exchange.getSearchRequest();
		List<SortCriterion> sc = searchRequest.getSortCriteria();
		if (null != searchRequest) {
			/**
			 * Will work for IOT, need to think when we use the same interceptor for other listings.
			 */
			SortCriterion slaSort = new SortCriterion();
			slaSort.setName("SLA_CODE");
			slaSort.setAscendingOrder(false);
			if(null == sc || sc.isEmpty()) {
				SortCriterion sortCriteria = new SortCriterion();
				sortCriteria.setName("CREATED_TIME");
				sortCriteria.setAscendingOrder(false);			
				sc = new ArrayList<SortCriterion>(2);
				/**
				 * If no sorting specified by the user, SLA sorting is always be given priority.
				 * So it is added first.
				 */
//				sc.add(slaSort);
				sc.add(sortCriteria);
				searchRequest.setSortCriteria(sc);
			} else {
				/**
				 * If sorting specified by the user, SLA sorting is appended to the end of the list.
				 * So it is added first.
				 */
//				sc.add(0, slaSort);
			}
		}
		searchRequest.setSortCriteria(sc);
		
		exchange.setSearchRequest(searchRequest);
	}

}
