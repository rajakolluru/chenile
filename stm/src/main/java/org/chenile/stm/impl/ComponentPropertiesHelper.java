package org.chenile.stm.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.chenile.stm.STMFlowStore;
import org.chenile.stm.State;
import org.chenile.stm.StateEntity;
import org.chenile.stm.model.AutomaticStateDescriptor;
import org.chenile.stm.model.StateDescriptor;
import org.chenile.stm.ognl.OgnlScriptingStrategy;

/**
 * A helper class to obtain the properties for the current action (as passed in the flow context). This class is aware
 * of the current scripting strategy and uses it to transform the incoming property. 
 * <p>This is useful to support component properties whose values are based on the flow context passed to the STM.
 * @author Raja Shankar Kolluru
 *
 */
public final class ComponentPropertiesHelper {
	
	private ScriptingStrategy scriptingStrategy = new OgnlScriptingStrategy();
	private STMFlowStore flowConfigurator;

	public ScriptingStrategy getScriptingStrategy() {
		return scriptingStrategy;
	}

	public void setScriptingStrategy(ScriptingStrategy scriptingStrategy) {
		this.scriptingStrategy = scriptingStrategy;
	}
	
	private Map<String,Object> getComponentProperties(StateEntity stateEntity){
		
		State state = stateEntity.getCurrentState();
		
		StateDescriptor currentState = flowConfigurator.getStateInfo(state);
		
		if (currentState == null || !(currentState instanceof AutomaticStateDescriptor))
			return null;
		return ((AutomaticStateDescriptor)currentState).getComponentProperties();
	}

	public String getComponentProperty(
			StateEntity stateEntity,
			String componentPropertyName,
			boolean inlineScriptsTranslated)  throws Exception{
		
		Map<String,Object> properties = getComponentProperties(stateEntity);
		String ret = null;
		if (properties != null) ret = (String)properties.get(componentPropertyName);
		return inlineScriptsTranslated? transform(ret,stateEntity) : ret;
	}
	
	@SuppressWarnings("unchecked")
	public List<String> getListComponentProperty(StateEntity stateEntity, String propertyName, boolean inlineScriptsTranslated) throws Exception{
		Map<String,Object> properties = getComponentProperties(stateEntity);
		List<String> list =  (properties != null)? (List<String>)properties.get(propertyName) : null;
		if (list == null) return null;
		
		List<String> newList = new ArrayList<String>();
		for (String s : list){
			newList.add(inlineScriptsTranslated? transform(s,stateEntity) : s);
		}
		return newList;
	}
	
	@SuppressWarnings("unchecked")
	public Map<String,String> getMapComponentProperty(StateEntity stateEntity, String propertyName, boolean inlineScriptsTranslated) throws Exception{
		Map<String,Object> properties = getComponentProperties(stateEntity);
		Map<String,String> map  =  (properties != null)? (Map<String,String>)properties.get(propertyName) : null;
		if (map == null) return null;
		Map<String,String> newMap = new HashMap<String, String>();
		for (String key: map.keySet()){
			String value = inlineScriptsTranslated? transform(map.get(key),stateEntity) : map.get(key);
			newMap.put(key, value);
		}
		return newMap;
	}
	
	/**
	 * Transforms the string by replacing the placeholders (of the form ${xxx}) with the values 
	 * obtained by substituting stuff from the state entity.
	 * @param s the string to substitute
	 * @param stateEntity the context from which the place holders are picked up.
	 * @return the transformed string.
	 * @throws Exception
	 */
	public String transform(String s, StateEntity stateEntity) throws Exception{
		if (s == null) return s;
		Pattern pattern = Pattern.compile("\\$\\{([^}]*)\\}");
		Matcher m = pattern.matcher(s);
		StringBuffer sb = new StringBuffer();
		while(m.find() ){
				String expression = m.group(1);
				Object replacement = scriptingStrategy.executeGenericScript(expression,stateEntity);
				String rep = (replacement == null)? "" : replacement.toString();
				m.appendReplacement(sb, rep);
			}
		m.appendTail(sb);
		return sb.toString();
	}

	public void setFlowConfigurator(STMFlowStore flowConfigurator) {
		this.flowConfigurator = flowConfigurator;
	}
}

