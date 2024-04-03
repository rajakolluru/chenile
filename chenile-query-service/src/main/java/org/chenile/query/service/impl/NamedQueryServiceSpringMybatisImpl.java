package org.chenile.query.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;

import org.chenile.query.model.ColumnMetadata;
import org.chenile.query.model.QueryMetadata;
import org.chenile.query.model.ResponseRow;
import org.chenile.query.model.SearchRequest;
import org.chenile.query.model.SearchResponse;
import org.chenile.query.service.AbstractSearchServiceImpl;
import org.chenile.query.service.QueryStore;

public class NamedQueryServiceSpringMybatisImpl extends AbstractSearchServiceImpl{

	public NamedQueryServiceSpringMybatisImpl(QueryStore queryStore) {
		super(queryStore);
	}
	
	public SearchResponse search(String queryName,SearchRequest<Map<String,Object>> searchRequest) {
		// Make sure that searchRequest.query name is populated
		searchRequest.setQueryName(queryName);
		return search(searchRequest);
	}

	protected static final String PAGINATION_PART = "pagination";
	@Autowired SqlSessionTemplate sessionTemplate;
	
	/**
	 * @param sessionTemplate the sessionTemplate to set
	 */
	public void setSessionTemplate(SqlSessionTemplate sessionTemplate) {
		this.sessionTemplate = sessionTemplate;
	}

	protected SearchResponse doSearch(EnhancedSearchRequest searchRequest,SearchResponse searchResponse,QueryMetadata queryMetadata) {
		// see if there is a count query to process first
		if (queryMetadata.isPaginated()) {
			processCountQuery(searchRequest.enhancedFilters,searchResponse,queryMetadata);
		}
		
		List<Object> list = executeQuery(queryMetadata.getId(),
				searchRequest.enhancedFilters);
		List<ResponseRow> responseList = new ArrayList<ResponseRow>();
		for (Object o : list) {
			ResponseRow row = new ResponseRow();
			row.setRow(o);
			
			// row.setAllowedActions(getAllowedActionsForWorkflowEntity(queryMetadata.getWorkflowName(),o));
			responseList.add(row);
		}
		searchResponse.setList(responseList);
		searchResponse.setNumRowsReturned(responseList.size());
		// populate drop down values
		for (Entry<String, ColumnMetadata> entry: searchResponse.getColumnMetadata().entrySet()) {
			populateDropDownValues(entry.getValue(),searchRequest.enhancedFilters);
		}
		return searchResponse;
	}
		
	@SuppressWarnings("unchecked")
	private void populateDropDownValues(ColumnMetadata cmd, Map<String, Object> filters) {
		if (cmd.getDropDownValues() != null) {
			return;
		}
		if (cmd.getDropDownQuery() == null)
			return;
		@SuppressWarnings("rawtypes")
		List list = executeQuery(cmd.getDropDownQuery(),filters);
		cmd.setDropDownValues((List<String>)list);
	}

	protected int processCountQuery(Map<String,Object> filters, SearchResponse searchResponse, QueryMetadata queryMetadata) {
		String qName = queryMetadata.getId() + "-count";
		
		Integer in = 0; 
		Object o = sessionTemplate.selectOne(qName, filters);
		// Object o = session.selectOne(qName, filters);
		if (o == null || !(o instanceof Integer)) {
			throw new RuntimeException ("Invalid count query for query name = " + queryMetadata.getId());
		}
		in = (Integer)o;
		setPaginationInResponse(searchResponse,in);
		constructPagination(filters,searchResponse.getStartRow(),searchResponse.getNumRowsInPage());
		return in.intValue();
				
	}
	
	protected List<Object> executeQuery(String queryName,Map<String, Object>filters){		
		return sessionTemplate.selectList(queryName, filters);
	}
	
	@Override
	protected List<Object> executeQuery(EnhancedSearchRequest searchRequest) {
		String queryName = searchRequest.originalSearchRequest.getQueryName();
		Map<String, Object> filters = searchRequest.enhancedFilters;
		return this.executeQuery(queryName, filters);
	}

}
