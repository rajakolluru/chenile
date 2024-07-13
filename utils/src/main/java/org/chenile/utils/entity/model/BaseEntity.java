package org.chenile.utils.entity.model;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

public class BaseEntity implements Serializable, ChenileEntity {
	@Serial
	private static final long serialVersionUID = -7001076357421117904L;
	private String id;
	private Date createdTime = new Date();
	private Date lastModifiedTime;
	private String lastModifiedBy;
	private String createdBy;
	private Long version;

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

	public String getLastModifiedBy() {
		return lastModifiedBy;
	}

	public void setLastModifiedBy(String lastModifiedBy) {
		this.lastModifiedBy = lastModifiedBy;
	}

	public String getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	public Long getVersion() {
		return version;
	}

	public void setVersion(Long version) {
		this.version = version;
	}

	@Override
	public String toString() {
		return "BaseEntity [id=" + id + ", createdTime=" + createdTime + ", lastModifiedTime=" + lastModifiedTime
				+ ", lastModifiedUserId=" + lastModifiedBy + ", createdUserId=" + createdBy + ", entityVersion="
				+ version + "]";
	}
}
