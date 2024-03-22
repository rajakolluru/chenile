package org.chenile.query.model;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class SearchResponse {
	private int numRowsReturned;

	private int currentPage;
	private int maxPages;
	private int numRowsInPage;
	private int startRow;
	private int endRow;
	private ResponseRow data; // use this if you want to send the output as one object graph
	private List<ResponseRow> list = Collections.emptyList(); // use this if you want to send the output as a list
	private Map<String, ColumnMetadata> columnMetadata = Collections.emptyMap();
	private int maxRows;
	private String cannedReportName;
	private List<CannedReport> availableCannedReports = Collections.emptyList();
	/**
	 * Changed from Collection<String> to Set<String> because the array was adding the old values again while updating.
	 * Example: ["id", "tenantId"] if passed twice the hidden columns was getting stored as ["id", "tenantId", "id", "tenantId"]
	 */
	private Set<String> hiddenColumns = Collections.emptySet();

	public String getCannedReportName() {
		return cannedReportName;
	}

	public void setCannedReportName(String cannedReportName) {
		this.cannedReportName = cannedReportName;
	}

	public List<CannedReport> getAvailableCannedReports() {
		return availableCannedReports;
	}

	public void setAvailableCannedReports(List<CannedReport> cannedReportNames) {
		this.availableCannedReports = cannedReportNames;
	}

	public Set<String> getHiddenColumns() {
		return hiddenColumns;
	}

	public void setHiddenColumns(Set<String> hiddenColumns) {
		this.hiddenColumns = hiddenColumns;
	}

	/**
	 * @return the numRowsReturned
	 */
	public int getNumRowsReturned() {
		return numRowsReturned;
	}

	/**
	 * @param numRowsReturned the numRowsReturned to set
	 */
	public void setNumRowsReturned(int numRowsReturned) {
		this.numRowsReturned = numRowsReturned;
	}

	/**
	 * @return the currentPage
	 */
	public int getCurrentPage() {
		return currentPage;
	}

	/**
	 * @param currentPage the currentPage to set
	 */
	public void setCurrentPage(int currentPage) {
		this.currentPage = currentPage;
	}

	/**
	 * @return the maxPages
	 */
	public int getMaxPages() {
		return maxPages;
	}

	/**
	 * @param maxPages the maxPages to set
	 */
	public void setMaxPages(int maxPages) {
		this.maxPages = maxPages;
	}

	/**
	 * @return the numRows
	 */
	public int getNumRowsInPage() {
		return numRowsInPage;
	}

	/**
	 * @param numRows the numRows to set
	 */
	public void setNumRowsInPage(int numRows) {
		this.numRowsInPage = numRows;
	}

	/**
	 * @return the startRow
	 */
	public int getStartRow() {
		return startRow;
	}

	/**
	 * @param startRow the startRow to set
	 */
	public void setStartRow(int startRow) {
		this.startRow = startRow;
	}

	/**
	 * @return the endRow
	 */
	public int getEndRow() {
		return endRow;
	}

	/**
	 * @param endRow the endRow to set
	 */
	public void setEndRow(int endRow) {
		this.endRow = endRow;
	}

	/**
	 * @return the list
	 */
	public List<ResponseRow> getList() {
		return list;
	}

	/**
	 * @param list the list to set
	 */
	public void setList(List<ResponseRow> list) {
		this.list = list;
	}

	public Map<String, ColumnMetadata> getColumnMetadata() {
		return this.columnMetadata;
	}

	public void setColumnMetadata(Map<String, ColumnMetadata> metadata) {
		this.columnMetadata = metadata;
	}

	public void setMaxRows(int maxRows) {
		this.maxRows = maxRows;
	}

	public int getMaxRows() {
		return this.maxRows;
	}

	public ResponseRow getData() {
		return data;
	}

	public void setData(ResponseRow data) {
		this.data = data;
	}

}
