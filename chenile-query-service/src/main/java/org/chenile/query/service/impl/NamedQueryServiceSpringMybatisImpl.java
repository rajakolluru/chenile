package org.chenile.query.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.chenile.base.exception.ServerException;
import org.chenile.query.service.error.ErrorCodes;
import org.mybatis.spring.SqlSessionTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import org.chenile.query.model.ColumnMetadata;
import org.chenile.query.model.QueryMetadata;
import org.chenile.query.model.ResponseRow;
import org.chenile.query.model.SearchRequest;
import org.chenile.query.model.SearchResponse;
import org.chenile.query.service.AbstractSearchServiceImpl;
import org.chenile.query.service.QueryStore;

/**
 * An implementation of the query service using Mybatis. The service accomplishes search by
 * first looking up a query name in a query store. It retrieves the metadata about the query
 * that includes a Mybatis query name. This is used to execute the query using Mybatis.
 * The results are returned back
 */
public class NamedQueryServiceSpringMybatisImpl extends AbstractSearchServiceImpl{
	Logger logger = LoggerFactory.getLogger(NamedQueryServiceSpringMybatisImpl.class);
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
		List<Object> list = null;
		try {
			list = executeQuery(queryMetadata.getId(),
					searchRequest.enhancedFilters);
		}catch(Exception e){
			e.printStackTrace();
		}
		List<ResponseRow> responseList = new ArrayList<ResponseRow>();
		if (list != null) {
			for (Object o : list) {
				ResponseRow row = new ResponseRow();
				row.setRow(o);

				row.setAllowedActions(getAllowedActionsForWorkflowEntity(queryMetadata.getWorkflowName(),o,
						queryMetadata.getStateColumn(),queryMetadata.getFlowColumn()));
				responseList.add(row);
			}
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
		
		int in = 0;
		Object o = null;
		try {
			o = sessionTemplate.selectOne(qName, filters);
		}catch(Exception e){
			throw new ServerException(ErrorCodes.CANNOT_EXECUTE_COUNT_QUERY.getSubError(),
					new Object[]{qName,filters,e.getMessage()},e);
		}

		if (!(o instanceof Integer)) {
			throw new ServerException(ErrorCodes.COUNT_QUERY_DOES_NOT_RETURN_INT.getSubError(),
					new Object[]{queryMetadata.getId()});
		}
		in = (Integer)o;
		setPaginationInResponse(searchResponse,in);
		constructPagination(filters,searchResponse.getStartRow(),searchResponse.getNumRowsInPage());
		return in;
				
	}
	
	protected List<Object> executeQuery(String queryName,Map<String, Object>filters){
		try {
			return sessionTemplate.selectList(queryName, filters);
		}catch(Exception e){
			throw new ServerException(ErrorCodes.CANNOT_EXECUTE_QUERY.getSubError(),
					new Object[]{queryName,filters,e.getMessage()},e);
		}
	}
	
	@Override
	protected List<Object> executeQuery(EnhancedSearchRequest searchRequest) {
		String queryName = searchRequest.originalSearchRequest.getQueryName();
		Map<String, Object> filters = searchRequest.enhancedFilters;
		return this.executeQuery(queryName, filters);
	}

}
