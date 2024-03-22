package org.chenile.utils.entity.model;

import java.io.Serializable;
import java.util.Date;

public class Entity implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -7001076357421117904L;
	private String id;
	private Date createdTime = new Date();
	private Date lastModifiedTime;
	private String lastModifiedUserId;
	private String createdUserId;
	private Long entityVersion;
	private String appType;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Date getCreatedTime() {
		return createdTime;
	}

	public void setCreatedTime(Date createdTime) {
		this.createdTime = createdTime;
	}

	public Date getLastModifiedTime() {
		return lastModifiedTime;
	}

	public void setLastModifiedTime(Date lastModifiedTime) {
		this.lastModifiedTime = lastModifiedTime;
	}

	public String getLastModifiedUserId() {
		return lastModifiedUserId;
	}

	public void setLastModifiedUserId(String lastModifiedUserId) {
		this.lastModifiedUserId = lastModifiedUserId;
	}

	public String getCreatedUserId() {
		return createdUserId;
	}

	public void setCreatedUserId(String createdUserId) {
		this.createdUserId = createdUserId;
	}

	public Long getEntityVersion() {
		return entityVersion;
	}

	public void setEntityVersion(Long entityVersion) {
		this.entityVersion = entityVersion;
	}

	public String getAppType() {
		return appType;
	}

	public void setAppType(String appType) {
		this.appType = appType;
	}

	@Override
	public String toString() {
		return "BaseEntity [id=" + id + ", createdTime=" + createdTime + ", lastModifiedTime=" + lastModifiedTime
				+ ", lastModifiedUserId=" + lastModifiedUserId + ", createdUserId=" + createdUserId + ", entityVersion="
				+ entityVersion + ", appType=" + appType + "]";
	}
}
