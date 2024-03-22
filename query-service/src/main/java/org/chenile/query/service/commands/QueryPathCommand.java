package org.chenile.query.service.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.chenile.base.exception.ErrorNumException;
import org.chenile.base.response.ErrorType;
import org.chenile.base.response.ResponseMessage;
import org.chenile.owiz.Command;
import org.chenile.owiz.config.model.AttachmentDescriptor;
import org.chenile.owiz.config.model.CommandDescriptor;
import org.chenile.owiz.config.model.CustomCommandTagDescriptor;
import org.chenile.owiz.impl.ParallelChain;
import org.chenile.query.service.commands.model.SearchContext;

/**
 * The parent of all the commands in the query path. The convention is that the 
 * query path will be mirrored by the commands. Consider for example a profile model
 * object:
 * <code>
 * profile
 * 	  - tax
 * 		  tax1
 * 		  tax2
 *    - shipping
 *  	  shipping1
 *  	  shipping2
 * </code>
 * The profile is the root element. tax and shipping are first level elements and
 * tax1,tax2,shipping1, shipping2 are second level elements.
 * We expect the commands generating this structure to be mirroring 
 * this hierarchy. So we expect that the commands XML will look like
 * <code>
 * 	<root>
 * 		<tax>
 * 			<tax1/>
 * 			<tax2/>
 * 		</tax>
 * 		<shipping>
 * 			<shipping1/>
 * 			<shipping2/>
 * 		</shipping>
 * 	</root>
 * </code>
 * 
 * The entire hierarchy of commands will derive from QueryPathCommand. 
 * root,tax and shipping are {@link QueryChain} commands
 * tax1,tax2, shipping1 and shipping2 are instances of {@link RTCommand} 
 * @author Raja Shankar Kolluru
 *
 */
public abstract class QueryPathCommand extends ParallelChain<SearchContext> {
	
	private static final String ROOT_ELEMENT = "root";
	protected List<String> pathComponents ; // this is stored to avoid a re-compute everytime
	
	/**
	 * 
	 * @return the fully qualified path of all the commands. So for example, for tax1 command above
	 * the fully qualified path looks like ["root","tax","tax1"]
	 */
	protected List<String> getFullyQualifiedPath() {
		if (pathComponents != null) return pathComponents;
		pathComponents = new ArrayList<>();
		for (AttachmentDescriptor<?> ad: commandDescriptor.getAttachmentDescriptors()) {
			CommandDescriptor<?> parent = ad.getParent();
			Command<?> parentCommand = parent.getCommand();
			if (parentCommand instanceof QueryPathCommand) {
				List<String> parentPath = ((QueryPathCommand)parentCommand).getFullyQualifiedPath();
				if (parentPath != null) {
					pathComponents.addAll(parentPath);
				}
				break;
			}
		}
		if (!isRootElement())
			pathComponents.add(getLastPathSegment());
		return pathComponents;
	}
	
	protected ErrorNumException generateException(int httpStatusCode, int subErrorNum, String message) {	
		ResponseMessage responseMessage = new ResponseMessage();
		responseMessage.setSeverity(ErrorType.ERROR);
		String name = getFullyQualifiedPathAsString();
		responseMessage.setField(name);
		responseMessage.setCode(httpStatusCode);
		responseMessage.setSubErrorCode(subErrorNum);
		responseMessage.setDescription(message);
		return new ErrorNumException(responseMessage);
	}
	
	protected boolean isRootElement() {
		String lps = getLastPathSegment();
		if (lps != null && lps.equals(ROOT_ELEMENT))
			return true;
		else
			return false;
	}
	
	protected String getLastPathSegment() {
		CustomCommandTagDescriptor td = commandDescriptor.getCustomCommandTagDescriptor();
		if (td == null) return null;
		return td.getTag();
	}
	
	@SuppressWarnings("unchecked")
	protected Map<String,Object> getAt(Map<String,Object> element,int at, String suffix) throws Exception{
		List<String> pc = getFullyQualifiedPath();
		for (int index = 0; index <= at; index++) {		
			element = (Map<String,Object>) element.get(pc.get(index) + suffix); // all intermediary nodes are maps.
			// the leaf node is a search request or search response
			// trap class cast exceptions or object mapper exceptions and send back a 400
			if (element == null) return null; 
		}
		return element;
	}
	
	protected Map<String,Object> obtainParentInRequest(Map<String,Object> element) throws Exception{
		List<String> pc = getFullyQualifiedPath();
		return getAt(element,pc.size()-2, getRequestSuffix());
	}
	
	protected Map<String,Object> obtainParentInResponse(Map<String,Object> element) throws Exception{
		List<String> pc = getFullyQualifiedPath();
		return getAt(element,pc.size()-2, getResponseSuffix());
	}

	protected void appendResponse(Map<String, Object> resp, Object sr) throws Exception{
		List<String> pc = getFullyQualifiedPath();
		Map<String,Object> t = obtainParentInResponse(resp);
		t.put(pc.get(pc.size()-1) + getResponseSuffix(),sr);		
	}
	
	protected Map<String,Object> obtainLastRequest(Map<String,Object> element) throws Exception{
		List<String> pc = getFullyQualifiedPath();
		return getAt(element,pc.size()-1, getRequestSuffix());
	}
	
	protected Map<String,Object> obtainLastResponse(Map<String,Object> element) throws Exception{
		List<String> pc = getFullyQualifiedPath();
		return getAt(element,pc.size()-1, getResponseSuffix());
	}
	

	@Override
	public boolean bypass(SearchContext context) {
		Object tr;
		try {
			tr = obtainLastRequest(context.req);
			if (tr == null) {
				return true;
			}
		} catch (Exception e) {
			return true;
		}
		
		return false;
	}
	
	protected String getRequestSuffix() {
		return "";
	}
	
	protected String getResponseSuffix() {
		return "";
	}
	
	protected String getFullyQualifiedPathAsString() {	
		StringBuffer buf = new StringBuffer("root");
		for (String s:getFullyQualifiedPath() ) {
			buf.append("." + s);
		}
		return buf.toString();
	}
	
	
}
