package org.chenile.core.context;


import org.springframework.security.core.Authentication;

import java.util.HashMap;
import java.util.Map;

public class ContextContainer {
	private ThreadLocal<Context> contexts = new ThreadLocal<Context>();
	
	private static class Context extends HashMap<String,String>{
		
		private static final long serialVersionUID = -8834996563220573087L;
		public String userId = "";
		public String regionId = "";
		public String groupId = "";
		public String employeeId = "";

		public String appType = "";
		public String tenantType = "";
		public String tenant = "";

		public boolean isActive = false;
		public boolean isVerified = false;
		public boolean isOnDemand = false;
		public boolean isInternal = false;

		public String trajectory = "";
		public String userAgent = "";
		public String batchId = "";
		private String deviceId = "";

		private Authentication authentication=null;
	}

	private Context getContext() {
		Context context = contexts.get();
		if (context == null) {
			context = new Context();
			contexts.set(context);
		}
		return context;
	}

	public void setAuthentication(Authentication authenticationContext){
		getContext().authentication = authenticationContext;
	}

	public Authentication getAuthentication(){
		return getContext().authentication;
	}

	public void put(String key, String value){
		getContext().put(key,value);
	}

	public String get(String key){
		return getContext().get(key);
	}
	public String getProtectedHeader(String headerName){
		return get("x-p-" + headerName);
	}

	public String getHeader(String headerName){
		return get("x-" + headerName);
	}


	public void setUserId(String userId) {
		if (userId == null)
			userId = "";
		put(HeaderUtils.USER_ID_KEY,userId);
		//getContext().userId = userId;
	}

	public String getUser() {
		String user = get(HeaderUtils.USER_ID_KEY);
		return (user == null) ? "" : user;
	}

	public void setRegion(String regionId) {
		if (regionId == null)
			regionId = "";
		put(HeaderUtils.REGION_ID_KEY,regionId);
		//getContext().regionId = tenantId;
	}

	public String getRegion() {
		String tenant = get(HeaderUtils.REGION_ID_KEY);
		return (tenant == null) ? "" : tenant;
	}


	/**
	 * @return the groupId
	 */
	public String getGroupId() {
		String groupId = get(HeaderUtils.GROUP_ID_KEY);
		return (groupId == null) ? "" : groupId;
	}

	/**
	 * @param groupId
	 *            the groupId to set
	 */
	public void setGroupId(String groupId) {
		if (groupId == null)
			groupId = "";
		put(HeaderUtils.GROUP_ID_KEY,groupId);
		//getContext().groupId = groupId;
	}

	/**
	 * @return the employeeId
	 */
	public String getEmployeeId() {
		String employeeId = get(HeaderUtils.EMPLOYEE_ID_KEY);
		return (employeeId == null) ? "" : employeeId;
	}

	/**
	 * @param employeeId
	 *            the employeeId to set
	 */
	public void setEmployeeId(String employeeId) {
		if (employeeId == null)
			employeeId = "";
		put(HeaderUtils.EMPLOYEE_ID_KEY,employeeId);
		//getContext().employeeId = employeeId;
	}

	/**
	 * @return the appType
	 */
	public String getAppType() {
		String appType = get(HeaderUtils.APP_TYPE_KEY);
		return (appType == null) ? "" : appType;
	}

	/**
	 * @param appType
	 *            the appType to set
	 */
	public void setAppType(String appType) {
		if (appType == null)
			appType = "";
		put(HeaderUtils.APP_TYPE_KEY,appType);
		//getContext().appType = appType;
	}

	/**
	 * @return the tenantType
	 */
	public String getTenantType() {

		return get(HeaderUtils.TENANT_TYPE);
	}

	/**
	 * @param tenantType
	 *            the tenantType to set
	 */
	public void setTenantType(String tenantType) {
		if (tenantType == null)
			tenantType = "";
		put(HeaderUtils.TENANT_TYPE,tenantType);
		//getContext().tenantType = tenantType;
	}

	/**
	 * @return the isActive
	 */
	public boolean isActive() {
		return getContext().isActive;
	}

	/**
	 * @param isActive
	 *            the isActive to set
	 */
	public void setActive(Boolean isActive) {
		if (isActive == null)
			isActive = true;
		getContext().isActive = isActive;
	}

	/**
	 * @return the isVerified
	 */
	public boolean isVerified() {
		return getContext().isVerified;
	}

	/**
	 * @param isVerified
	 *            the isVerified to set
	 */
	public void setVerified(Boolean isVerified) {
		if (isVerified == null)
			isVerified = true;
		getContext().isVerified = isVerified;
	}

	/**
	 * @return the isOnDemand
	 */
	public boolean isOnDemand() {
		return getContext().isOnDemand;
	}

	/**
	 * @param isOnDemand
	 *            the isOnDemand to set
	 */
	public void setOnDemand(Boolean isOnDemand) {
		if (isOnDemand == null)
			isOnDemand = true;
		getContext().isOnDemand = isOnDemand;
	}

	/**
	 * @return the isInternal
	 */
	public boolean isInternal() {
		return getContext().isInternal;
	}

	/**
	 * @param isInternal
	 *            the isInternal to set
	 */
	public void setInternal(Boolean isInternal) {
		if (isInternal == null)
			isInternal = true;
		getContext().isInternal = isInternal;
	}

	/**
	 * @return the trajectory
	 */
	public String getTrajectory() {
		return get(HeaderUtils.TRAJECTORY_ID);
	}

	/**
	 * @param trajectory
	 *            the trajectory to set
	 */
	public void setTrajectory(String trajectory) {
		if (trajectory == null)
			trajectory = "";
		put(HeaderUtils.TRAJECTORY_ID,trajectory);
		//getContext().trajectory = trajectory;
	}

	/**
	 * @return the userAgent
	 */

	public String getUserAgent() {
		String userAgent = get(HeaderUtils.USER_AGENT_KEY);
		return (null == userAgent) ? "" : userAgent;
	}

	/**
	 * @param userAgent
	 *            the userAgent to set
	 */
	public void setUserAgent(String userAgent) {
		if (userAgent == null)
			userAgent = "";
		put(HeaderUtils.USER_AGENT_KEY,userAgent);
		//getContext().userAgent = userAgent;
	}

	private String getBatchId() {
		String batchId = get(HeaderUtils.BATCH_ID);
		return (null == batchId) ? "" : batchId;
	}
	private void setBatchId(String batchId) {
		if (batchId == null)
			batchId = "";
		put(HeaderUtils.BATCH_ID,batchId);
		//getContext().batchId = batchId;

	}


	public String getTenant() {
		String tenant = get(HeaderUtils.TENANT_ID_KEY);
		return (null == tenant) ? "" : tenant;
	}
	public void setTenant(String tenant) {
		if (tenant == null)
			tenant = "";
		put(HeaderUtils.TENANT_ID_KEY,tenant);
		//getContext().tenant = tenant;

	}
	/**
	 *
	 * @return the deviceId
	 */
	private String getDeviceId() {
		String deviceId = get(HeaderUtils.DEVICE_ID);
		return (null == deviceId) ? "" : deviceId;
	}
	/**
	 * @param deviceId the deviceId to set
	 */
	private void setDeviceId(String deviceId) {
		if (deviceId == null)
			deviceId = "";
		put(HeaderUtils.DEVICE_ID,deviceId);
		//getContext().deviceId = deviceId;

	}

	public Map<String, String> toMap() {
		/*map.put(HeaderUtils.REGION_ID_KEY, getRegion());
		map.put(HeaderUtils.USER_ID_KEY, getUser());
		map.put(HeaderUtils.EMPLOYEE_ID_KEY, getEmployeeId());
		map.put(HeaderUtils.GROUP_ID_KEY, getGroupId());
		map.put(HeaderUtils.APP_TYPE_KEY, getAppType());
		map.put(HeaderUtils.USER_AGENT_KEY, getUserAgent());
		map.put(HeaderUtils.BATCH_ID, getBatchId());
		map.put(HeaderUtils.DEVICE_ID, getDeviceId());
		map.put(HeaderUtils.TENANT_TYPE, getTenantType());
		map.put(HeaderUtils.TENANT_ID_KEY, getTenant());*/
        return new HashMap<>(getContext());
	}

	public void fromSimpleMap(SimpleMap map) {
		setTenant(map.getValue(HeaderUtils.TENANT_ID_KEY));
		setRegion(map.getValue(HeaderUtils.REGION_ID_KEY));
		setUserId(map.getValue(HeaderUtils.USER_ID_KEY));
		setEmployeeId(map.getValue(HeaderUtils.EMPLOYEE_ID_KEY));
		setGroupId(map.getValue(HeaderUtils.GROUP_ID_KEY));
		setAppType(map.getValue(HeaderUtils.APP_TYPE_KEY));
		setUserAgent(map.getValue(HeaderUtils.USER_AGENT_KEY));
		setBatchId(map.getValue(HeaderUtils.BATCH_ID));
		setDeviceId(map.getValue(HeaderUtils.DEVICE_ID));
		setTenantType(map.getValue(HeaderUtils.TENANT_TYPE));
	}

	public interface SimpleMap {
		public String getValue(String key);
	}

	public void fromMap(Map<String, String> map) {
		fromSimpleMap(new SimpleMap() {
			public String getValue(String key) {
				return map.get(key);
			}
		});
	}

}
