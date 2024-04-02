/**
 * 
 */
package org.chenile.security.model;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * @author Deepak N
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class EmployeeValues implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8214391714416935723L;
	private String key;
	private String value;
	private String employeeId;
	private List<Acl> acls;

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getEmployeeId() {
		return employeeId;
	}

	public void setEmployeeId(String employeeId) {
		this.employeeId = employeeId;
	}

	public List<Acl> getAcls() {
		return acls;
	}

	public void setAcls(List<Acl> acls) {
		this.acls = acls;
	}
}
