package org.chenile.query.service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang.StringUtils;
import org.chenile.base.exception.NotFoundException;
import org.chenile.base.exception.ServerException;
import org.chenile.core.context.ContextContainer;
import org.chenile.query.model.ColumnMetadata;
import org.chenile.query.model.ColumnMetadata.ColumnType;
import org.chenile.query.model.QueryMetadata;
import org.chenile.query.model.SearchRequest;
import org.chenile.query.model.SearchResponse;
import org.chenile.query.model.SortCriterion;
import org.chenile.query.service.error.ErrorCodes;
import org.chenile.stm.State;
import org.chenile.stm.StateEntity;
import org.springframework.beans.factory.annotation.Autowired;

@SuppressWarnings("unchecked")
public abstract class AbstractSearchServiceImpl implements SearchService<Map<String, Object>> {

	protected class EnhancedSearchRequest {
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
			throw new NotFoundException(ErrorCodes.QUERY_ID_NOT_FOUND.ordinal(),
					"Query " + searchRequest.getQueryName() + " not found");

		// construct enhanced search request
		EnhancedSearchRequest esr = new EnhancedSearchRequest(searchRequest);
		if (queryMetadata.getAcls() != null && queryMetadata.getAcls().length > 0) {
			// if (!securityService.isAllowed(contextContainer.getUser(),
			// queryMetadata.getAcls()))
			// throw new RuntimeException("User " + contextContainer.getUser() + " not
			// authorized to execute query " + searchRequest.getQueryName());
		}

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

		Map<String, Object> filters = searchInput.originalSearchRequest.getFilters();
		if (filters == null || cmdmap == null)
			return;
		for (String name : filters.keySet()) {
			ColumnMetadata cmd = cmdmap.get(name);
			if (cmd == null || !cmd.isFilterable()) { // ignore stuff in filters which is not filterable
				System.err.println(
						"Warning: Filter name " + name + " is not filterable but has been passed as a filter!");
				continue;
			}
			Object value = filters.get(name);
			if (null != value && value.toString().length() != 0) {
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
	}

	protected void constructBetweenQuery(Map<String, Object> enhancedFilters, String name, Object value,
			ColumnMetadata columnMetadata) {
		if (null == columnMetadata)
			return;
		if (!(value instanceof List)) {
			throw new ServerException(
					"Value: " + value + " should be an array for the range operation to be executed.");
		}
		;

		ColumnType columnType = columnMetadata.getColumnType();

		// No between operation for Checkbox and DropDown Type
		if (columnType == ColumnType.CheckBox || columnType == ColumnType.DropDown)
			return;

		List<Object> list = (List<Object>) value;
		if (null == list || list.isEmpty())
			return;

		int size = list.size();
		if (1 == size) {
			// If list contains only one element then add the same element again so that the
			// between query contains two elements.
			list.add(list.get(0));
		}

		Object first = list.get(0);
		Object second = list.get(1);
		String firstStr = (null == first) ? "" : first.toString().trim();
		String secondStr = (null == second) ? "" : second.toString().trim();

		if ("".equals(firstStr) && "".equals(secondStr))
			return;
		if ("".equals(firstStr) && !"".equals(secondStr)) {
			first = second;
		} else if (!"".equals(firstStr) && "".equals(secondStr)) {
			second = first;
		}

		List<Object> filterList = new ArrayList<>(2);

		if (columnType == ColumnType.Text) {
			filterList.add("%" + first + "%");
			filterList.add("%" + second + "%");
		} else if (columnType == ColumnType.Number) {
			filterList.add(first);
			filterList.add(second);
		} else {
			Calendar firstDate = Calendar.getInstance();
			firstDate.setTime(new Date(Long.valueOf(first.toString())));
			firstDate.set(Calendar.HOUR_OF_DAY, 0);
			firstDate.set(Calendar.MINUTE, 0);
			firstDate.set(Calendar.SECOND, 0);
			filterList.add(firstDate.getTime());

			Calendar secondDate = Calendar.getInstance();
			secondDate.setTime(new Date(Long.valueOf(second.toString())));
			secondDate.set(Calendar.HOUR_OF_DAY, 23);
			secondDate.set(Calendar.MINUTE, 59);
			secondDate.set(Calendar.SECOND, 59);
			filterList.add(secondDate.getTime());
		}
		enhancedFilters.put(name, filterList);
	}

	protected void constructContainsQuery(Map<String, Object> enhancedFilters, String name, Object value) {
		if (value instanceof List || value instanceof String[]) {
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
		if (propNamesList.size() > 0) {
			filters.put("propNames", propNamesList);
			filters.put("propValues", propValuesList);
		}
	}

	protected void buildOrderByClause(Map<String, Object> filters, List<SortCriterion> sortCriteria,
			QueryMetadata queryMetadata) {
		if (!queryMetadata.isSortable())
			return;
		String orderby = "order by 1 ASC";
		if (sortCriteria == null || sortCriteria.size() == 0) {
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

	/**
	 * @deprecated
	 * @param obj
	 * @return
	 */
	protected List<String> getAllowedActionsForWorkflowEntity(Object obj) {
		if (obj == null)
			return null;
		State state = extractStateFromObject(obj);
		if (state == null)
			return null;
		return null;
		// return stmActionsInfoProvider.getAllowedActions(state);
	}

	protected State extractStateFromObject(Object obj) {
		if (obj instanceof StateEntity) {
			return ((StateEntity) obj).getCurrentState();
		} else if (obj instanceof Map<?, ?>) {
			Map<String, Object> map = (Map<String, Object>) obj;
			String flowId = (String) map.get("FLOWID");
			String stateId = (String) map.get("STATEID");
			return new State(stateId, flowId);
		}
		return null;
	}

	/**
	 * Merge the second search request to the first search request. The first search
	 * request will be enhanced
	 * 
	 * @param one
	 * @param two
	 */
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

		/**
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
		int maxPages = Math.round(maxRows / numRowsInPage);
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
