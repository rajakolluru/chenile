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
public class EmployeeDetails implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8524175501979358578L;
	private String name;
	private String emailId;
	private String reportingToID;
	private String reportingToName;
	private boolean isActive;
	private String profilePic;
	private String gender;
	private String mobileNumber;
	private EmployeeValues department;
	private EmployeeValues designation;
	private List<RoleAcl> roles;
	private String userId;
	private boolean isDeleted;
	private int slNo;
	private Map<String, Object> personalDetails;
	private Map<String, Object> contactDetails;
	private Map<String, Object> others;
	private Map<String, Object> bankDetails;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getEmailId() {
		return emailId;
	}

	public void setEmailId(String emailId) {
		this.emailId = emailId;
	}

	public String getReportingToID() {
		return reportingToID;
	}

	public void setReportingToID(String reportingToID) {
		this.reportingToID = reportingToID;
	}

	public String getReportingToName() {
		return reportingToName;
	}

	public void setReportingToName(String reportingToName) {
		this.reportingToName = reportingToName;
	}

	public boolean isActive() {
		return isActive;
	}

	public void setActive(boolean isActive) {
		this.isActive = isActive;
	}

	public String getProfilePic() {
		return profilePic;
	}

	public void setProfilePic(String profilePic) {
		this.profilePic = profilePic;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public String getMobileNumber() {
		return mobileNumber;
	}

	public void setMobileNumber(String mobileNumber) {
		this.mobileNumber = mobileNumber;
	}

	public EmployeeValues getDepartment() {
		return department;
	}

	public void setDepartment(EmployeeValues department) {
		this.department = department;
	}

	public EmployeeValues getDesignation() {
		return designation;
	}

	public void setDesignation(EmployeeValues designation) {
		this.designation = designation;
	}

	public List<RoleAcl> getRoles() {
		return roles;
	}

	public void setRoles(List<RoleAcl> roles) {
		this.roles = roles;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public boolean isDeleted() {
		return isDeleted;
	}

	public void setDeleted(boolean isDeleted) {
		this.isDeleted = isDeleted;
	}

	public int getSlNo() {
		return slNo;
	}

	public void setSlNo(int slNo) {
		this.slNo = slNo;
	}

	public Map<String, Object> getPersonalDetails() {
		return personalDetails;
	}

	public void setPersonalDetails(Map<String, Object> personalDetails) {
		this.personalDetails = personalDetails;
	}

	public Map<String, Object> getContactDetails() {
		return contactDetails;
	}

	public void setContactDetails(Map<String, Object> contactDetails) {
		this.contactDetails = contactDetails;
	}

	public Map<String, Object> getOthers() {
		return others;
	}

	public void setOthers(Map<String, Object> others) {
		this.others = others;
	}

	public Map<String, Object> getBankDetails() {
		return bankDetails;
	}

	public void setBankDetails(Map<String, Object> bankDetails) {
		this.bankDetails = bankDetails;
	}

	@Override
	public String toString() {
		return "EmployeeDetails [name=" + name + ", emailId=" + emailId + ", reportingToID=" + reportingToID
				+ ", reportingToName=" + reportingToName + ", isActive=" + isActive + ", profilePic=" + profilePic
				+ ", gender=" + gender + ", mobileNumber=" + mobileNumber + ", department=" + department
				+ ", designation=" + designation + ", roles=" + roles + ", userId=" + userId + ", isDeleted="
				+ isDeleted + ", slNo=" + slNo + ", personalDetails=" + personalDetails + ", contactDetails="
				+ contactDetails + ", others=" + others + ", bankDetails=" + bankDetails + "]";
	}
}
