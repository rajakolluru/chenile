package org.chenile.core.context;

import java.util.Map;

/**
 * Convenient methods that avoid the need for using header names everywhere
 * @author Raja Shankar Kolluru
 *
 */
public class HeaderUtils {
	public static final String REGION_ID_KEY = "x-chenile-region-id";
	public static final String TENANT_ID_KEY = "x-chenile-tenant-id";
	public static final String USER_ID_KEY = "x-chenile-uid";
	public static final String EMPLOYEE_ID_KEY = "x-chenile-eid";
	public static final String GROUP_ID_KEY = "x-chenile-gid";
	public static final String APP_TYPE_KEY = "x-chenile-apt";
	public static final String USER_AGENT_KEY = "user-agent";
	public static final String BATCH_ID = "x-batchId";
	public static final String DEVICE_ID = "x-chenile-deviceid";
	public static final String TENANT_TYPE = "x-chenile-tenanttype";
	public static final String CHANNEL = "x-chenile-channel";
	public static final String ENTRY_POINT = "chenile-entry-point";
	public static final String TRAJECTORY_ID = "x-chenile-trajectory-id";
	public static final String MOCK_HEADER = "x-chenile-mock-mode";

	private static String convertToString(Object o) {
		return (o == null) ? null: o.toString();
	}
	public static String getRegion(Map<String, Object> headers) {
		return convertToString(headers.get(REGION_ID_KEY));
	}
	
	public static String getTenant(Map<String, Object> headers) {
		return convertToString(headers.get(TENANT_ID_KEY));
	}

	public static String getUserId(Map<String, Object> headers) {
		return convertToString(headers.get(USER_ID_KEY));
	}
	
	public static String getEmployeeId(Map<String, Object> headers) {
		return convertToString(headers.get(EMPLOYEE_ID_KEY));
	}
	public static String getGroupId(Map<String, Object> headers) {
		return convertToString(headers.get(GROUP_ID_KEY));
	}
	public static String getAppType(Map<String, Object> headers) {
		return convertToString(headers.get(APP_TYPE_KEY));
	}
	public static String getUserAgent(Map<String, Object> headers) {
		return convertToString(headers.get(USER_AGENT_KEY));
	}
	public static String getDeviceId(Map<String, Object> headers) {
		return convertToString(headers.get(DEVICE_ID));
	}
	public static String getBatchId(Map<String, Object> headers) {
		return convertToString(headers.get(BATCH_ID));
	}
	public static String getTenantType(Map<String, Object> headers) {
		return convertToString(headers.get(TENANT_TYPE));
	}
	public static String getChannel(Map<String, Object> headers) {
		return convertToString(headers.get(CHANNEL));
	}
	public static String getEntryPoint(Map<String, Object> headers) {
		return convertToString(headers.get(ENTRY_POINT));
	}

	public static void setRegion(Map<String, Object> headers, String regionId) {
		headers.put(REGION_ID_KEY,regionId);
	}
	
	public static void setTenant(Map<String, Object> headers, String tenantId) {
		headers.put(TENANT_ID_KEY,tenantId);
	}

	public static void setUserId(Map<String, Object> headers, String userId) {
		headers.put(USER_ID_KEY,userId);
	}
	
	public static void setEmployeeId(Map<String, Object> headers, String empId) {
		headers.put(EMPLOYEE_ID_KEY,empId);
	}
	public static void setGroupId(Map<String, Object> headers, String groupId) {
		headers.put(GROUP_ID_KEY,groupId);
	}
	public static void setAppType(Map<String, Object> headers, String appType) {
		headers.put(APP_TYPE_KEY,appType);
	}
	public static void setUserAgent(Map<String, Object> headers, String userAgent) {
		headers.put(USER_AGENT_KEY,userAgent);
	}
	public static void setDeviceId(Map<String, Object> headers, String deviceId) {
		headers.put(DEVICE_ID,deviceId);
	}
	public static void setBatchId(Map<String, Object> headers, String batchId) {
		headers.put(BATCH_ID,batchId);
	}
	public static void setTenantType(Map<String, Object> headers, String tenantType) {
		headers.put(TENANT_TYPE,tenantType);
	}
	public static void setChannel(Map<String, Object> headers, String channel) {
		headers.put(CHANNEL,channel);
	}
	public static void setEntryPoint(Map<String, Object> headers, String entryPoint) {
		headers.put(ENTRY_POINT,entryPoint);
	}
}
