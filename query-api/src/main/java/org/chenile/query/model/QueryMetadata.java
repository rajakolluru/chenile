package org.chenile.query.model;

import java.util.Map;

import org.chenile.utils.entity.model.Entity;

/**
 * 
 * @author Raja Shankar Kolluru
 * All queries in the system must be configured with this information
 */

public class QueryMetadata extends Entity {
	/**
	 * 
	 */
	private static final long serialVersionUID = -5311145579935980818L;

	private String id;
	
	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * @return the workflowName
	 */
	public String getWorkflowName() {
		return workflowName;
	}

	/**
	 * @param workflowName the workflowName to set
	 */
	public void setWorkflowName(String workflowName) {
		this.workflowName = workflowName;
	}

	/**
	 * @return the flexiblePropnames
	 */
	public boolean isFlexiblePropnames() {
		return flexiblePropnames;
	}

	/**
	 * @param flexiblePropnames the flexiblePropnames to set
	 */
	public void setFlexiblePropnames(boolean flexiblePropnames) {
		this.flexiblePropnames = flexiblePropnames;
	}

	public boolean isPaginated() {
		return paginated;
	}

	public void setPaginated(boolean paginated) {
		this.paginated = paginated;
	}

	public String[] getAcls() {
		return acls;
	}

	public void setAcls(String[] acls) {
		this.acls = acls;
	}
	
	/**
	 * @return the columnMetadata
	 */
	public Map<String, ColumnMetadata> getColumnMetadata() {
		return columnMetadata;
	}

	/**
	 * @param columnMetadata the columnMetadata to set
	 */
	public void setColumnMetadata(Map<String, ColumnMetadata> columnMetadata) {
		this.columnMetadata = columnMetadata;
	}

	// Denotes if the output of the query is associated with a workflow
	// this is used to determine the applicable actions.
	private String workflowName ;
	
	private boolean flexiblePropnames = false;
	private boolean paginated = false;
	private String[] acls = {};
	private Map<String, ColumnMetadata> columnMetadata = null;
	private boolean sortable;

	public boolean isSortable() {
		return this.sortable;
	}

	public void setSortable(boolean sortable) {
		this.sortable = sortable;
	}

	
}
