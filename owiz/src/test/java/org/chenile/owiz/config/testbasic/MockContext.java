package org.chenile.owiz.config.testbasic;

import java.util.ArrayList;
import java.util.List;

public class MockContext {
	private String module;
	
	List<String> logs = new ArrayList<String>();
	public void log(String x){
		logs.add(x);
	}
	
	public List<String> getLogs(){
		return logs;
	}
	
	public String getLog(int index){
		return logs.get(index);
	}

	public String getModule() {
		return module;
	}

	public void setModule(String module) {
		this.module = module;
	}
	
	
}
