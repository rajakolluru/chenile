package org.chenile.query.model;

import org.chenile.utils.entity.model.BaseEntity;

import java.util.Map;

/**
 * @author Raja Shankar Kolluru
 * All queries in the system must be configured with this information
 */

public class QueryMetadata extends BaseEntity {
    /**
     *
     */
    private static final long serialVersionUID = -5311145579935980818L;

    private String id;
    private String name; // the externally visible name for this query
    private String flowColumn = "flowId";
    private String stateColumn = "stateId";
    private String lateColumn = null;
    private String tendingLateColumn = null;
    /**
     *  Denotes if the output of the query is associated with a workflow
     *  this is used to determine the applicable actions.
     */
    private String workflowName;
    private boolean toDoList = false;
    private boolean flexiblePropnames = false;
    private boolean paginated = false;
    private String[] acls = {};
    private Map<String, ColumnMetadata> columnMetadata = null;
    private boolean sortable;

    public String getStateColumn() {
        return stateColumn;
    }

    public void setStateColumn(String stateColumn) {
        this.stateColumn = stateColumn;
    }

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

    public boolean isSortable() {
        return this.sortable;
    }

    public void setSortable(boolean sortable) {
        this.sortable = sortable;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isToDoList() {
        return toDoList;
    }

    public void setToDoList(boolean toDoList) {
        this.toDoList = toDoList;
    }

    public String getFlowColumn() {
        return this.flowColumn;
    }

    public void setFlowColumn(String flowColumn) {
        this.flowColumn = flowColumn;
    }

    public String getLateColumn() {
        return this.lateColumn;
    }

    public String getTendingLateColumn() {
        return tendingLateColumn;
    }

    public void setTendingLateColumn(String tendingLateColumn) {
        this.tendingLateColumn = tendingLateColumn;
    }

    public void setLateColumn(String lateColumn) {
        this.lateColumn = lateColumn;
    }
}
