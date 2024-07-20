package org.chenile.query.model;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class SearchRequest<FilterType> {
	int numRowsInPage = 10;
	int pageNum = 1;
	List<SortCriterion> sortCriteria = Collections.emptyList();
	FilterType filters;
	Map<String,Object> systemFilters;	
	String queryName;
	String cannedReportName;
	List<String> fields;

	public boolean isToDoList() {
		return toDoList;
	}

	public void setToDoList(boolean toDoList) {
		this.toDoList = toDoList;
	}

	boolean toDoList = false;
	
	public List<String> getFields() {
		return fields;
	}

	public void setFields(List<String> fields) {
		this.fields = fields;
	}

	/**
	 * Changed from Collection<String> to Set<String> because the array was adding the old values again while updating.
	 * Example: ["id", "tenantId"] if passed twice the hidden columns was` getting stored as ["id", "tenantId", "id", "tenantId"]
	 */
	private Set<String> hiddenColumns = Collections.emptySet();
	/**
	 * Be careful when setting this flag to true!
	 * If this variable is set to true, then the searchRequest will be saved in the DB as a canned report.
	 * If the canned report exists, it is over-written by this flag. (The previous contents of the canned report
	 * are not merged. They will be over-written)
	 * 
	 */
	private boolean saveChangesToCannedReport = false;
	
	private boolean publishCannedReportToEveryone = false;
	
	/**
	 * Used for elastic search query request
	 */
	private boolean isOrOperation = false;
	
	public String getCannedReportName() {
		return cannedReportName;
	}

	public void setCannedReportName(String cannedReportName) {
		this.cannedReportName = cannedReportName;
	}

	/**
	 * @return the numRowsInPage
	 */
	public int getNumRowsInPage() {
		return numRowsInPage;
	}

	/**
	 * If incoming value is set to zero from the user, reset it to the default value initialized in the instance variable.
	 * @param numRowsInPage the numRowsInPage to set
	 */
	public void setNumRowsInPage(int numRowsInPage) {
		this.numRowsInPage = (0 == numRowsInPage) ? this.numRowsInPage : numRowsInPage;
	}

	/**
	 * @return the pageNum
	 */
	public int getPageNum() {
		return pageNum;
	}

	/**
	 * @param pageNum the pageNum to set
	 */
	public void setPageNum(int pageNum) {
		this.pageNum = pageNum;
	}

	/**
	 * @return the sortCriteria
	 */
	public List<SortCriterion> getSortCriteria() {
		return sortCriteria;
	}

	/**
	 * @param sortCriteria the sortCriteria to set
	 */
	public void setSortCriteria(List<SortCriterion> sortCriteria) {
		this.sortCriteria = sortCriteria;
	}

	/**
	 * @return the filters
	 */
	public FilterType getFilters() {
		return filters;
	}

	/**
	 * @param filters the filters to set
	 */
	public void setFilters(FilterType filters) {
		this.filters = filters;
	}

	/**
	 * @return the systemFilters
	 */
	public Map<String,Object> getSystemFilters() {
		return systemFilters;
	}

	/**
	 * @param systemFilters the systemFilters to set
	 */
	public void setSystemFilters(Map<String,Object> systemFilters) {
		this.systemFilters = systemFilters;
	}

	/**
	 * @return the queryName
	 */
	public String getQueryName() {
		return queryName;
	}

	/**
	 * @param queryName the queryName to set
	 */
	public void setQueryName(String queryName) {
		this.queryName = queryName;
	}

	public Set<String> getHiddenColumns() {
		return hiddenColumns;
	}

	public void setHiddenColumns(Set<String> hiddenColumns) {
		this.hiddenColumns = hiddenColumns;
	}

	public boolean isSaveChangesToCannedReport() {
		return saveChangesToCannedReport;
	}

	public void setSaveChangesToCannedReport(boolean saveChangesToCannedReport) {
		this.saveChangesToCannedReport = saveChangesToCannedReport;
	}

	public boolean isPublishCannedReportToEveryone() {
		return publishCannedReportToEveryone;
	}

	public void setPublishCannedReportToEveryone(boolean publishCannedReportToEveryone) {
		this.publishCannedReportToEveryone = publishCannedReportToEveryone;
	}

	public boolean isOrOperation() {
		return isOrOperation;
	}

	public void setOrOperation(boolean isOrOperation) {
		this.isOrOperation = isOrOperation;
	}
}
