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
public class RoleAcl extends EmployeeValues implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -3824364152360014613L;
	private List<Acl> acls;

	public List<Acl> getAcls() {
		return acls;
	}

	public void setAcls(List<Acl> acls) {
		this.acls = acls;
	}
}
