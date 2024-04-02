/**
 * 
 */
package org.chenile.security.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * @author 
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class CompanyDetails {
	private String tenantId;
	private String tenantType;
	private String  mobileNumber;
	private String emailId;
	private String landLine;
	private String adminName;
	private String name;
	private String shortName;
	private boolean isActive;
	private boolean isVerified;
	private boolean isInternal;
	private boolean isOnDemoMode;
	public String getTenantId() {
		return tenantId;
	}
	public void setTenantId(String tenantId) {
		this.tenantId = tenantId;
	}
	public String getTenantType() {
		return tenantType;
	}
	public void setTenantType(String tenantType) {
		this.tenantType = tenantType;
	}
	public String getMobileNumber() {
		return mobileNumber;
	}
	public void setMobileNumber(String mobileNumber) {
		this.mobileNumber = mobileNumber;
	}
	public String getEmailId() {
		return emailId;
	}
	public void setEmailId(String emailId) {
		this.emailId = emailId;
	}
	public String getLandLine() {
		return landLine;
	}
	public void setLandLine(String landLine) {
		this.landLine = landLine;
	}
	public String getAdminName() {
		return adminName;
	}
	public void setAdminName(String adminName) {
		this.adminName = adminName;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getShortName() {
		return shortName;
	}
	public void setShortName(String shortName) {
		this.shortName = shortName;
	}
	public boolean isActive() {
		return isActive;
	}
	public void setActive(boolean isActive) {
		this.isActive = isActive;
	}
	public boolean isVerified() {
		return isVerified;
	}
	public void setVerified(boolean isVerified) {
		this.isVerified = isVerified;
	}
	public boolean isInternal() {
		return isInternal;
	}
	public void setInternal(boolean isInternal) {
		this.isInternal = isInternal;
	}
	public boolean isOnDemoMode() {
		return isOnDemoMode;
	}
	public void setOnDemoMode(boolean isOnDemoMode) {
		this.isOnDemoMode = isOnDemoMode;
	}
	@Override
	public String toString() {
		return "CompanyDetails [tenantId=" + tenantId + ", tenantType=" + tenantType + ", mobileNumber=" + mobileNumber
				+ ", emailId=" + emailId + ", landLine=" + landLine + ", adminName=" + adminName + ", name=" + name
				+ ", shortName=" + shortName + ", isActive=" + isActive + ", isVerified=" + isVerified + ", isInternal="
				+ isInternal + ", isOnDemoMode=" + isOnDemoMode + "]";
	}

}
