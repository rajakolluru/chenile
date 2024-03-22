/**
 * 
 */
package org.chenile.workflow.param;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonInclude;


@JsonInclude
public class MinimalPayload implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -2314712304952305692L;
	private String comment;

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

}
