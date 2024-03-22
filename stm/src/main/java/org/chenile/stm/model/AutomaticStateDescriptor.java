package org.chenile.stm.model;

import java.util.ArrayList;
/**
 * This descriptor is used to describe automatic state transitions. It contains a component that would be 
 * invoked for computing the event that would then determine the transition to make from this state.
 */
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import org.chenile.stm.action.STMAutomaticStateComputation;
import org.chenile.stm.action.scriptsupport.BaseCustomComponentPropertiesAction;
import org.chenile.stm.impl.ComponentPropertiesHelper;

public class AutomaticStateDescriptor extends StateDescriptor {
	private STMAutomaticStateComputation<?> component;
	private String componentName;
	private Map<String,Object> componentProperties = new HashMap<String, Object>();
	private Map<String,String> xmlComponentProperties = new HashMap<String, String>();

	public Map<String, Object> getComponentProperties() {
		return componentProperties;
	}


	public void setComponentName(String componentName) {
			this.componentName = componentName;
	}
	
	public AutomaticStateDescriptor componentName(String componentName) {
		this.componentName = componentName;
		return this;
	}
	
	public String getComponentName(){
		return componentName;
	}
	
	public STMAutomaticStateComputation<?> getComponent() {
		return component;
	}
	
	public void setComponent(STMAutomaticStateComputation<?> action){
		this.component = action;
	}
	
	
	public AutomaticStateDescriptor component(STMAutomaticStateComputation<?> action){
		this.component = action;
		// if this component happens to need a componentPropertiesHelper
		// we should supply the same. 
		setComponentHelper(action);
		return this;
	}
	
	
	
	
	private void setComponentHelper(STMAutomaticStateComputation<?> action) {
		if (action instanceof BaseCustomComponentPropertiesAction) {
			BaseCustomComponentPropertiesAction<?> bcpa = (BaseCustomComponentPropertiesAction<?>)action;
			ComponentPropertiesHelper cph = new ComponentPropertiesHelper();
			cph.setFlowConfigurator(this.getFlow().getFlowStore());
			bcpa.setComponentPropertiesHelper(cph);
		}		
	}


	@Override
	public String toString() {
		return "ActionStateDescriptor [component=" + component + ", id=" + id + ", initialState="
				+ initialState + "] \n";
	}
	
	public Map<String,String> getXmlComponentProperties() {
		return xmlComponentProperties;
	}
	
	public void addXmlComponentProperty(String name,String value){
		String newKey = name;
		int ind = name.lastIndexOf("/");
		if (ind != -1 )
			newKey = name.substring(ind + 1);
		xmlComponentProperties.put(newKey,value);
		processProperty(newKey,value);
	}
	
	private void processProperty(String newKey, String s){

		Object value = s;
		if (newKey.toLowerCase().endsWith("list")){
			// this is assumed to be a list of strings rather than a plain string.
			// assumed to be pipe character (|) separated.
			List<String> list = new ArrayList<String>();
			StringTokenizer st = new StringTokenizer(s,"|;");
			while (st.hasMoreTokens()){
				list.add(st.nextToken());
			}
			value = list;
		}else if (newKey.toLowerCase().endsWith("map")){
			Map<String,String> map = new HashMap<String, String>();
			StringTokenizer st = new StringTokenizer(s,"|;");
			while (st.hasMoreTokens()){
				String s1 = st.nextToken();
				int ind = s1.indexOf("=");
				map.put(s1.substring(0,ind), s1.substring(ind + 1));
			}
			value = map;
		}
		addComponentProperty(newKey,value);
		}
	
	public AutomaticStateDescriptor addComponentProperty(String name,Object obj){
		componentProperties.put(name, obj);
		return this;
	}

	public AutomaticStateDescriptor property(String name,Object obj){
		componentProperties.put(name, obj);
		return this;
	}
	
}
