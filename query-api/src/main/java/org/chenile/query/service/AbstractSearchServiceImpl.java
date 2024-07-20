package org.chenile.query.service;

import java.util.*;
import java.util.Map.Entry;

import org.apache.commons.lang.StringUtils;
import org.chenile.base.exception.NotFoundException;
import org.chenile.base.exception.ServerException;
import org.chenile.core.context.ContextContainer;
import org.chenile.query.model.*;
import org.chenile.query.model.ColumnMetadata.ColumnType;
import org.chenile.query.service.error.ErrorCodes;
import org.chenile.stm.State;
import org.chenile.stm.StateEntity;
import org.chenile.stm.impl.STMActionsInfoProvider;
import org.chenile.workflow.api.WorkflowRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

@SuppressWarnings("unchecked")
public abstract class AbstractSearchServiceImpl implements SearchService<Map<String, Object>> {
	Logger logger = LoggerFactory.getLogger(AbstractSearchServiceImpl.class);
	protected static class EnhancedSearchRequest {
		public EnhancedSearchRequest(SearchRequest<Map<String, Object>> searchRequest) {
			this.originalSearchRequest = searchRequest;
		}

		public SearchRequest<Map<String, Object>> originalSearchRequest;
		public Map<String, Object> enhancedFilters; // the enhanced filters extracted from the search request.
		// these will be used to provide the parameters for the query
	}

	protected static final String PAGINATION_PART = "pagination";
	protected static final String ORDER_BY = "orderby";

	public AbstractSearchServiceImpl(QueryStore queryStore) {
		this.queryStore = queryStore;
	}

	protected QueryStore queryStore;
	// @Autowired protected SecurityService securityService;
	@Autowired
	protected ContextContainer contextContainer;
	// @Autowired protected STMActionsInfoProvider stmActionsInfoProvider;

	public final SearchResponse search(SearchRequest<Map<String, Object>> searchRequest) {

		QueryMetadata queryMetadata = queryStore.retrieve(searchRequest.getQueryName());

		if (queryMetadata == null)
			throw new NotFoundException(ErrorCodes.QUERY_ID_NOT_FOUND.getSubError(),
					new Object[]{ searchRequest.getQueryName()});

		// construct enhanced search request
		EnhancedSearchRequest esr = new EnhancedSearchRequest(searchRequest);

		enhanceFiltersUsingMetadata(esr, queryMetadata);

		buildOrderByClause(esr.enhancedFilters, esr.originalSearchRequest.getSortCriteria(), queryMetadata);
		SearchResponse searchResponse = makeSearchResponse(queryMetadata, esr);
		searchResponse = doSearch(esr, searchResponse, queryMetadata);
		return searchResponse;
	}


	/**
	 * Use the column metadata to enhance the filters that have been passed.
	 * 
	 * @param searchInput
	 */
	protected void enhanceFiltersUsingMetadata(EnhancedSearchRequest searchInput, QueryMetadata queryMetadata) {
		searchInput.enhancedFilters = new HashMap<String, Object>();
		Map<String, ColumnMetadata> cmdmap = queryMetadata.getColumnMetadata();
		Map<String, Object> systemFilters = searchInput.originalSearchRequest.getSystemFilters();
		if (systemFilters != null) {
			for (String name : systemFilters.keySet()) {
				ColumnMetadata cmd = cmdmap.get(name);
				Object value = systemFilters.get(name);
				if (null != cmd && cmd.isLikeQuery()) {
					searchInput.enhancedFilters.put(name, "%" + value + "%");
				} else {
					searchInput.enhancedFilters.put(name, value);
				}
			}
		}

		if (searchInput.originalSearchRequest.isToDoList()){
			if (!queryMetadata.isToDoList() ){
				logger.warn("QueryName: {}. Search specifies todoList but query is not configured to support toDoList",
						queryMetadata.getName());
			}
			else if (queryMetadata.getWorkflowName() == null){
				logger.warn("QueryName: {}. Workflow name is null but it is specified as a toDoList",
						queryMetadata.getName());
			}else {
				Collection<String> states = getAllowedStatesForCurrentUser(queryMetadata.getWorkflowName());
				constructContainsQuery(searchInput.enhancedFilters, queryMetadata.getStateColumn(), states);
			}
		}

		Map<String, Object> filters = searchInput.originalSearchRequest.getFilters();
		if (filters == null || cmdmap == null)
			return;
		for (String name : filters.keySet()) {
			ColumnMetadata cmd = cmdmap.get(name);
			if (cmd == null || !cmd.isFilterable()) { // ignore stuff in filters which is not filterable
				logger.warn("Warning: Filter name " + name + " is not filterable but has been passed as a filter!");
				continue;
			}
			Object value = filters.get(name);
			if (null != value && !value.toString().isEmpty()) {
				if (cmd.isLikeQuery()) {
					searchInput.enhancedFilters.put(name, "%" + value + "%");
				} else if (cmd.isContainsQuery()) {
					constructContainsQuery(searchInput.enhancedFilters, name, value);
				} else if (cmd.isBetweenQuery()) {
					constructBetweenQuery(searchInput.enhancedFilters, name, value, cmd);
				} else {
					searchInput.enhancedFilters.put(name, value);
				}
			}
		}
		if (queryMetadata.isFlexiblePropnames())
			enhanceFiltersWithPropNamesPropValues(searchInput.enhancedFilters);
		logger.debug("Filters = " + searchInput.enhancedFilters);
	}

	protected void constructBetweenQuery(Map<String, Object> enhancedFilters, String name, Object value,
			ColumnMetadata columnMetadata) {
		if (null == columnMetadata || null == value)
			return;
		List<Object> list;
		if (value instanceof List) {
			list = (List<Object>) value;
		}else {
			list = new ArrayList<Object>();
			list.add(value);
		}

		ColumnType columnType = columnMetadata.getColumnType();

		// No between operation for Checkbox and DropDown Type
		if (columnType == ColumnType.CheckBox || columnType == ColumnType.DropDown)
			return;
		if (1 == list.size()) {
			// If list contains only one element then add the same element again so that the
			// between query contains two elements.
			list.add(list.get(0));
		}

		Object first = list.get(0);
		Object second = list.get(1);
		String firstStr = (null == first) ? "" : first.toString().trim();
		String secondStr = (null == second) ? "" : second.toString().trim();

		if (firstStr.isEmpty() && secondStr.isEmpty())
			return;
		if (firstStr.isEmpty()) {
			first = second;
		} else if (secondStr.isEmpty()) {
			second = first;
		}

		List<Object> filterList = new ArrayList<>(2);
		if (columnType == ColumnType.Text) {
			filterList.add("%" + first + "%");
			filterList.add("%" + second + "%");
		} else if (columnType == ColumnType.Number) {
			filterList.add(first);
			filterList.add(second);
		} else if (columnType == ColumnType.Date || columnType == ColumnType.DateTime){
			// treat these as strings and send them to the DB. Let the database
			// worry about them.
			filterList.add(firstStr);
			filterList.add(secondStr);
		}
		enhancedFilters.put(name, filterList);
	}

	protected void constructContainsQuery(Map<String, Object> enhancedFilters, String name, Object value) {
		if ( value instanceof String[] || value instanceof Collection<?>) {
			enhancedFilters.put(name, value);
			return;
		}
		if (value instanceof String) {
			enhancedFilters.put(name, new String[] { (String) value });
			return;
		}
	}

	protected void enhanceFiltersWithPropNamesPropValues(Map<String, Object> filters) {

		List<String> propNamesList = new ArrayList<String>();
		List<String> propValuesList = new ArrayList<String>();
		for (Entry<String, Object> entry : filters.entrySet()) {
			propNamesList.add(entry.getKey());
			propValuesList.add(entry.getValue() + "");
		}
		if (!propNamesList.isEmpty()) {
			filters.put("propNames", propNamesList);
			filters.put("propValues", propValuesList);
		}
	}

	protected void buildOrderByClause(Map<String, Object> filters, List<SortCriterion> sortCriteria,
			QueryMetadata queryMetadata) {
		if (!queryMetadata.isSortable())
			return;
		String orderby = "order by 1 ASC";
		if (sortCriteria == null || sortCriteria.isEmpty()) {
			filters.put(ORDER_BY, orderby);
			return;
		}

		int counter = 0;
		int sconSize = sortCriteria.size();
		StringBuilder orberyByStringBuilder = new StringBuilder("order by ");
		for (SortCriterion s : sortCriteria) {
			counter++;
			if (StringUtils.isNotEmpty(s.getName())) {
				orberyByStringBuilder.append(s.getName()).append((s.isAscendingOrder() ? " ASC " : " DESC "));
			} else {
				orberyByStringBuilder.append(s.getIndex()).append((s.isAscendingOrder() ? " ASC " : " DESC "));
			}

			if (counter != sconSize) {
				orberyByStringBuilder.append(", ");
			}
		}
		orderby = orberyByStringBuilder.toString();
		filters.put(ORDER_BY, orderby);
	}

	protected abstract SearchResponse doSearch(EnhancedSearchRequest searchInput, SearchResponse searchResponse,
			QueryMetadata queryMetadata);

	public void setQueryStore(QueryStore queryStore) {
		this.queryStore = queryStore;
	}

	protected SearchResponse makeSearchResponse(QueryMetadata queryMetadata, EnhancedSearchRequest searchInput) {
		SearchResponse searchResponse = new SearchResponse();
		searchResponse.setColumnMetadata(queryMetadata.getColumnMetadata());
		searchResponse.setCurrentPage(searchInput.originalSearchRequest.getPageNum());
		searchResponse.setNumRowsInPage(searchInput.originalSearchRequest.getNumRowsInPage());
		searchResponse.setCannedReportName(searchInput.originalSearchRequest.getCannedReportName());
		searchResponse.setHiddenColumns(searchInput.originalSearchRequest.getHiddenColumns());
		return searchResponse;
	}

	public void setContextContainer(ContextContainer contextContainer) {
		this.contextContainer = contextContainer;
	}

	protected List<Map<String,String>> getAllowedActionsForWorkflowEntity(String workflowName, Object obj,
												  String stateColumn, String flowColumn) {
		if (obj == null)
			return null;
		State state = extractStateFromObject(obj, stateColumn, flowColumn);
		if (state == null) {
			logger.warn("State for object of type {} is null.", workflowName);
			System.err.println("State for object of type " + workflowName + " is null");
			return null;
		}
		List<Map<String,String>> ret = new ArrayList<>();
		STMActionsInfoProvider provider = WorkflowRegistry.getSTMActionInfoProvider(workflowName);
		if(provider == null) {
			logger.warn("provider for workflow {} is null.", workflowName);
			System.err.println("provider for object of type " + workflowName + " is null");
			return null;
		}
		List<Map<String, String>> listOfMaps = provider.getAllowedActionsAndMetadata(state);
		if (listOfMaps == null){
			logger.warn("return value from state info provider for workflow {} is null.", workflowName);
			System.err.println("return value from state info provider of type " + workflowName + " is null");
			return null;
		}
		/*for (Map<String,String> map: listOfMaps){
			HashMap<String,String> allowedActionInfo = new HashMap<>();
			for (Entry<String,String> e: map.entrySet()){
				if (e.getKey().startsWith("ui-")){
					allowedActionInfo.put(e.getKey().substring(3),e.getValue());
				}
			}
			ret.add(allowedActionInfo);
		}*/
		return listOfMaps;
	}

	protected Collection<String> getAllowedStatesForCurrentUser(String workflowName){
		STMActionsInfoProvider provider = WorkflowRegistry.getSTMActionInfoProvider(workflowName);
		return provider.getStatesAllowedForCurrentUser();
	}

	protected State extractStateFromObject(Object obj, String stateColumn, String flowColumn) {
		if (obj instanceof StateEntity) {
			return ((StateEntity) obj).getCurrentState();
		} else if (obj instanceof Map<?, ?>) {
			Map<String, Object> map = (Map<String, Object>) obj;
			String flowId = (String) map.get(flowColumn);
			String stateId = (String) map.get(stateColumn);
			return new State(stateId, flowId);
		}
		return null;
	}

	@SuppressWarnings("unused")
	private void mergeSearchRequests(SearchRequest<Map<String, Object>> one, SearchRequest<Map<String, Object>> two) {
		if (one == null || two == null)
			return;
		if (two.getFilters() != null && !two.getFilters().isEmpty()) {
			if (one.getFilters() == null || one.getFilters().isEmpty()) {
				one.setFilters(two.getFilters());
			}
			for (Entry<String, Object> entry : two.getFilters().entrySet()) {
				one.getFilters().put(entry.getKey(), entry.getValue());
			}
		}

		/*
		 * Merging NumRowsInPage not required, because the default value has been set at
		 * the model level. If we merge, then the value from the elastic search always
		 * overrides the actual SearchRequest.
		 */
//		if (two.getNumRowsInPage() != 0)
//			one.setNumRowsInPage(two.getNumRowsInPage());

		if (two.getSortCriteria() != null && !two.getSortCriteria().isEmpty()) {
			if (one.getSortCriteria() == null || one.getSortCriteria().isEmpty()) {
				one.setSortCriteria(two.getSortCriteria());
			}
			for (SortCriterion sortCriterion : two.getSortCriteria()) {
				one.getSortCriteria().add(sortCriterion);
			}
		}
		if (two.getHiddenColumns() != null && !two.getHiddenColumns().isEmpty()) {
			if (one.getHiddenColumns() == null || one.getHiddenColumns().isEmpty()) {
				one.setHiddenColumns(two.getHiddenColumns());
			}
			for (String column : two.getHiddenColumns()) {
				one.getHiddenColumns().add(column);
			}
		}
	}

	protected abstract int processCountQuery(Map<String, Object> filters, SearchResponse searchResponse,
			QueryMetadata queryMetadata);

	protected abstract List<Object> executeQuery(EnhancedSearchRequest searchRequest);

	protected void setPaginationInResponse(SearchResponse searchResponse, int maxRows) {
		searchResponse.setMaxRows(maxRows);
		int page = searchResponse.getCurrentPage();
		int numRowsInPage = searchResponse.getNumRowsInPage();
		int maxPages = Math.round((float) maxRows / numRowsInPage);
		if (0 != maxRows % numRowsInPage) {
			maxPages += 1;
		}
		page = (0 != maxRows && page > maxPages) ? maxPages : page;
		int startRow = (page - 1) * numRowsInPage + 1;
		searchResponse.setStartRow(startRow);
		searchResponse.setCurrentPage(page);
		searchResponse.setMaxPages(maxPages);
	}

	protected void constructPagination(Map<String, Object> filters, int startRow, int numRowsInPage) {
		filters.put(PAGINATION_PART, "limit " + numRowsInPage + " offset " + (startRow - 1));
	}
}
