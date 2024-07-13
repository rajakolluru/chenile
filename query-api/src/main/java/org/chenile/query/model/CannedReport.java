package org.chenile.query.model;


import org.chenile.utils.entity.model.BaseEntity;

public class CannedReport extends BaseEntity {
	/**
	 * 
	 */
	private static final long serialVersionUID = -8860701548977932604L;
	private String queryName;
	private String cannedReportName;
	private SearchRequest<?> searchRequest;
	private String userId;
	private boolean applicableToAll;

	public String getCannedReportName() {
		return cannedReportName;
	}

	public void setCannedReportName(String cannedReportName) {
		this.cannedReportName = cannedReportName;
	}

	public SearchRequest<?> getSearchRequest() {
		return searchRequest;
	}

	public void setSearchRequest(SearchRequest<?> searchRequest) {
		this.searchRequest = searchRequest;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public boolean isApplicableToAll() {
		return applicableToAll;
	}

	public void setApplicableToAll(boolean applicableToAll) {
		this.applicableToAll = applicableToAll;
	}

	public String getQueryName() {
		return queryName;
	}

	public void setQueryName(String queryName) {
		this.queryName = queryName;
	}
}
