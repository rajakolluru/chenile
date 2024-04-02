/**
 * 
 */
package org.chenile.security.model;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * @author Deepak N
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ComponentDetails implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3962641755660645404L;
	private String componentId;
	private String permission;
	private String roleId;
	private String componentName;
	private String roleName;

	public String getComponentId() {
		return componentId;
	}

	public void setComponentId(String componentId) {
		this.componentId = componentId;
	}

	public String getPermission() {
		return permission;
	}

	public void setPermission(String permission) {
		this.permission = permission;
	}

	public String getRoleId() {
		return roleId;
	}

	public void setRoleId(String roleId) {
		this.roleId = roleId;
	}

	public String getComponentName() {
		return componentName;
	}

	public void setComponentName(String componentName) {
		this.componentName = componentName;
	}

	public String getRoleName() {
		return roleName;
	}

	public void setRoleName(String roleName) {
		this.roleName = roleName;
	}

	@Override
	public String toString() {
		return "ComponentDetails [componentId=" + componentId + ", permission=" + permission + ", roleId=" + roleId
				+ ", componentName=" + componentName + ", roleName=" + roleName + "]";
	}
}
