/**
 * 
 */
package org.chenile.security.model;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * @author Deepak N
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class EmployeeRoles implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6462864251238251655L;
	private String employeeId;
	private Map<String, Object> defaultComponents;
	private List<ComponentDetails> permissions;
	private List<ComponentDetails> permissionDetails;

	public String getEmployeeId() {
		return employeeId;
	}

	public void setEmployeeId(String employeeId) {
		this.employeeId = employeeId;
	}

	public Map<String, Object> getDefaultComponents() {
		return defaultComponents;
	}

	public void setDefaultComponents(Map<String, Object> defaultComponents) {
		this.defaultComponents = defaultComponents;
	}

	public List<ComponentDetails> getPermissions() {
		return permissions;
	}

	public void setPermissions(List<ComponentDetails> permissions) {
		this.permissions = permissions;
	}

	public List<ComponentDetails> getPermissionDetails() {
		return permissionDetails;
	}

	public void setPermissionDetails(List<ComponentDetails> permissionDetails) {
		this.permissionDetails = permissionDetails;
	}

	@Override
	public String toString() {
		return "EmployeeRoles [employeeId=" + employeeId + ", defaultComponents=" + defaultComponents + ", permissions="
				+ permissions + ", permissionDetails=" + permissionDetails + ", getClass()=" + getClass()
				+ ", hashCode()=" + hashCode() + ", toString()=" + super.toString() + "]";
	}
}
