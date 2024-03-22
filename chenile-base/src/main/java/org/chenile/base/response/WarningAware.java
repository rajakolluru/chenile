package org.chenile.base.response;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Extract and set warnings to response objects (either objects or maps)
 * It allows for checking of warnings to objects and also to add warnings to objects which are 
 * capable of accommodating warnings.
 * Currently, all objects capable of accommodating warnings will either implement this interface
 * or be a map (in which case a warningMessages key is created in the map)
 * @author Raja Shankar Kolluru
 *
 */
public interface WarningAware {
	public static String WARNING_MESSAGES_KEY = "warningMessages";

	public List<ResponseMessage> getWarningMessages();
	public void addWarningMessage(ResponseMessage m);
	public void removeAllWarnings();
	@SuppressWarnings("unchecked")
	public static List<ResponseMessage> obtainWarnings(Object data){
		if (data instanceof WarningAware ) {
			WarningAware wa = (WarningAware)data;
			if (wa.getWarningMessages() != null) {
				return wa.getWarningMessages();
			}
		}else if (data instanceof Map<?, ?>) {
			Object m = ((Map<String,Object>)data).get(WarningAware.WARNING_MESSAGES_KEY);
			if (m != null && m instanceof List) {
			     return (List<ResponseMessage>)m;
			}
		}
		return null;
	}
	/**
	 * Adds a warning message to the context. 
	 * This method can add warnings to {@link WarningAware} data types or map data types
	 * @param data
	 * @param message
	 */
	@SuppressWarnings("unchecked")
	public static void addWarningMessage(Object data, ResponseMessage message) {
		if (data instanceof WarningAware ) {
			WarningAware wa = (WarningAware)data;
			wa.addWarningMessage(message);
		}else if (data instanceof Map<?, ?>) {
			Object o = ((Map<String,Object>)data).get(WarningAware.WARNING_MESSAGES_KEY);
			if (o != null && !(o instanceof List)) {
				return;
			}
			
			List<ResponseMessage> list ;			
			if (o == null ) {
				list = new ArrayList<>();
				((Map<String,Object>)data).put(WarningAware.WARNING_MESSAGES_KEY,list);
			}else {
				list = ((List<ResponseMessage>)o);
			}
			list.add(message);
			
		}
	}
	
	@SuppressWarnings("unchecked")
	public static void removeAllWarnings(Object data) {
		if (data instanceof WarningAware ) {
			WarningAware wa = (WarningAware)data;
			wa.removeAllWarnings();
		}else if (data instanceof Map<?, ?>) {
			((Map<String,Object>)data).remove(WarningAware.WARNING_MESSAGES_KEY);
		}
	}
	public static void addWarningMessages(Object data, List<ResponseMessage> warnings) {
		for(ResponseMessage m: warnings) {
			addWarningMessage(data,m);
		}		
	}
}
