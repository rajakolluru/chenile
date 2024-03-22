/**
 * 
 */
package org.chenile.swagger.model;

import java.io.Serializable;

/**
 * @author Deepak N
 *
 */
public class Header implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7063461517524063430L;
	private String name;
	private String description;
	private boolean required = false;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public boolean isRequired() {
		return required;
	}

	public void setRequired(boolean required) {
		this.required = required;
	}
}
