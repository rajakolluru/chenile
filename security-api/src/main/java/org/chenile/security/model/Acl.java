/**
 * 
 */
package org.chenile.security.model;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * @author Raja Shankar Kolluru
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Acl implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2424358592222114887L;
	private String acl;
	private String description;

	public String getAcl() {
		return acl;
	}

	public void setAcl(String acl) {
		this.acl = acl;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

}
