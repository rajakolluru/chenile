package org.chenile.query.model;

import java.util.List;

public class ColumnMetadata {
	public enum ColumnType {
		CheckBox, Number, DropDown, Text, Date, DateTime
	}

	private String name;
	private String dropDownQuery;
	private boolean filterable;
	private boolean sortable;
	private boolean likeQuery;
	private List<String> dropDownValues;
	private ColumnType columnType;
	private boolean containsQuery;
	private boolean display = true;
	private String columnName; 
	/**
	 * Used for Date range.
	 */
	private boolean betweenQuery;
	/**
	 * This field is used to indicate whether the display column should be custom.
	 * This is usually `true` when the data is present inside the nested JSON.
	 */
	private boolean customRender = false;
	/**
	 * Used for grouping of columns in the front-end
	 */
	private String group = "";
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the filterable
	 */
	public boolean isFilterable() {
		return filterable;
	}

	/**
	 * @param filterable the filterable to set
	 */
	public void setFilterable(boolean filterable) {
		this.filterable = filterable;
	}

	/**
	 * @return the sortable
	 */
	public boolean isSortable() {
		return sortable;
	}

	/**
	 * @param sortable the sortable to set
	 */
	public void setSortable(boolean sortable) {
		this.sortable = sortable;
	}

	/**
	 * @return the columnType
	 */
	public ColumnType getColumnType() {
		return columnType;
	}

	/**
	 * @param columnType the columnType to set
	 */
	public void setColumnType(ColumnType columnType) {
		this.columnType = columnType;
	}

	public List<String> getDropDownValues() {
		return dropDownValues;
	}

	public void setDropDownValues(List<String> dropDownValues) {
		this.dropDownValues = dropDownValues;
	}

	public boolean isLikeQuery() {
		return likeQuery;
	}

	public void setLikeQuery(boolean likeQuery) {
		this.likeQuery = likeQuery;
	}

	public String getDropDownQuery() {
		return dropDownQuery;
	}

	public void setDropDownQuery(String dropDownQuery) {
		this.dropDownQuery = dropDownQuery;
	}

	public void setContainsQuery(boolean containsQuery) {
		this.containsQuery = containsQuery;
	}

	public boolean isContainsQuery() {
		return containsQuery;
	}

	public boolean isDisplay() {
		return display;
	}

	public void setDisplay(boolean display) {
		this.display = display;
	}

	public String getColumnName() {
		return columnName;
	}

	public void setColumnName(String columnName) {
		this.columnName = columnName;
	}

	public boolean isBetweenQuery() {
		return betweenQuery;
	}

	public void setBetweenQuery(boolean betweenQuery) {
		this.betweenQuery = betweenQuery;
	}

	public boolean isCustomRender() {
		return customRender;
	}

	public void setCustomRender(boolean customRender) {
		this.customRender = customRender;
	}

	public String getGroup() {
		return group;
	}

	public void setGroup(String group) {
		this.group = group;
	}
}
