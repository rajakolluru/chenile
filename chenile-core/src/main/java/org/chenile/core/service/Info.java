package org.chenile.core.service;

import java.util.ArrayList;
import java.util.List;

public class Info {
	public String version;
	public String moduleName;
	public static class ServiceInfo{
		public String name;
		public ServiceInfo() {}
		public ServiceInfo(String name) { this.name = name;}
		public List<OperationInfo> operations = new ArrayList<>();
	}
	public static class OperationInfo {
		public String name;
		public String url;
		public String method;
	}
	public List<ServiceInfo> services = new ArrayList<>();
}
