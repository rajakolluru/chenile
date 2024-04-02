package org.chenile.query.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.beans.factory.annotation.Autowired;

import org.chenile.query.model.ColumnMetadata;
import org.chenile.query.model.QueryMetadata;
import org.chenile.query.model.ResponseRow;
import org.chenile.query.model.SearchResponse;
import org.chenile.query.service.AbstractSearchServiceImpl;

public class NamedQueryServiceImpl extends AbstractSearchServiceImpl{

	@Autowired SqlSessionFactory sqlSessionFactory;
	/**
	 * @param sqlSessionFactory the sqlSessionFactory to set
	 */
	public void setSqlSessionFactory(SqlSessionFactory sqlSessionFactory) {
		this.sqlSessionFactory = sqlSessionFactory;
	}
	
	protected SearchResponse doSearch(EnhancedSearchRequest searchRequest,SearchResponse searchResponse,QueryMetadata queryMetadata) {
		// see if there is a count query to process first
		if (queryMetadata.isPaginated()) {
			processCountQuery(searchRequest.enhancedFilters,searchResponse,queryMetadata);
		}
		
		List<Object> list = executeQuery(searchRequest.originalSearchRequest.getQueryName(),
				searchRequest.enhancedFilters);
		List<ResponseRow> responseList = new ArrayList<ResponseRow>();
		for (Object o : list) {
			ResponseRow row = new ResponseRow();
			row.setRow(o);
			
			// row.setAllowedActions(getAllowedActionsForWorkflowEntity(o));
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

	@Override
	protected int processCountQuery(Map<String,Object> filters, SearchResponse searchResponse, QueryMetadata queryMetadata) {
		String qName = queryMetadata.getId() + "-count";
		SqlSession session = null;
		Integer in = 0; 
		try {
			session = sqlSessionFactory.openSession();
			
			Object o = session.selectOne(qName, filters);
			if (o == null || !(o instanceof Integer)) {
				throw new RuntimeException ("Invalid count query for query name = " + queryMetadata.getId());
			}
			in = (Integer)o;
			setPaginationInResponse(searchResponse,in);
			constructPagination(filters,searchResponse.getStartRow(),searchResponse.getNumRowsInPage());
			return in.intValue();
		}finally {
			try {
				if (session != null) {
					session.close();
				}
			} catch (Exception e) {
				return in;
			}
		}		
	}
	
	protected List<Object> executeQuery(String queryName,Map<String, Object>filters){
		SqlSession session = null;
		try {
			session = sqlSessionFactory.openSession();			
			return session.selectList(queryName, filters);
		} finally {
			try {
				if (session != null) {
					session.close();
				}
			} catch (Exception e) {
				return null;
			}
		}
	}
	
	@Override
	protected List<Object> executeQuery(EnhancedSearchRequest searchRequest) {
		String queryName = searchRequest.originalSearchRequest.getQueryName();
		Map<String, Object> filters = searchRequest.enhancedFilters;
		return this.executeQuery(queryName, filters);
	}

}
