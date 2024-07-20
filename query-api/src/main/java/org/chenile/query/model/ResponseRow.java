package org.chenile.query.model;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class ResponseRow implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1223056280474051780L;
	private Object row;
	/**
	 * @return the rowInfo
	 */
	public Object getRow() {
		return row;
	}
	/**
	 * @param row the row to set
	 */
	public void setRow(Object row) {
		this.row = row;
	}
	/**
	 * @return the allowedActions
	 */
	public List<Map<String,String>> getAllowedActions() {
		return allowedActions;
	}
	/**
	 * @param allowedActions the allowedActions to set
	 */
	public void setAllowedActions(List<Map<String,String>> allowedActions) {
		this.allowedActions = allowedActions;
	}
	private List<Map<String,String>> allowedActions;
	
	@Override
	public String toString() {
		return "ResponseRow [row=" + row + ", allowedActions=" + allowedActions + "]";
	}
	
}
