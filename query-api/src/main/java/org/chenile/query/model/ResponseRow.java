package org.chenile.query.model;

import java.io.Serializable;
import java.util.List;

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
	public List<AllowedActionInfo> getAllowedActions() {
		return allowedActions;
	}
	/**
	 * @param allowedActions the allowedActions to set
	 */
	public void setAllowedActions(List<AllowedActionInfo> allowedActions) {
		this.allowedActions = allowedActions;
	}
	private List<AllowedActionInfo> allowedActions;
	
	@Override
	public String toString() {
		return "ResponseRow [row=" + row + ", allowedActions=" + allowedActions + "]";
	}
	
}
