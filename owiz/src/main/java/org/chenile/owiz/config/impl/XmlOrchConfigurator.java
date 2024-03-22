package org.chenile.owiz.config.impl;

import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.digester.Digester;
import org.apache.commons.digester.ExtendedBaseRules;
import org.chenile.owiz.BeanFactoryAdapter;
import org.chenile.owiz.config.model.CustomAttachmentTagDescriptor;
import org.chenile.owiz.config.model.CustomCommandTagDescriptor;
import org.chenile.owiz.config.model.FlowDescriptor;
import org.chenile.owiz.exception.OwizConfigException;
import org.chenile.owiz.impl.Chain;
import org.xml.sax.SAXException;

/**
 * Chief class for the framework. It configures the flow by reading a set of XML files. The files would be combined
 * to obtain the flow.
 * <p>This class supports a DSL language that allows the consumer to define command and attach tags. The properties of 
 * both command and the attach tag can be defaulted. 
 * @author Raja Shankar Kolluru
 *
 * @param <InputType> .
 */
public class XmlOrchConfigurator<InputType> extends OrchConfiguratorBase<InputType>  {

	// Apache commons digester is used for sac processing of the file.
	private Digester digester;
	public static final String FLOW_STACK = "flowStack";
	protected static IDGenerator idGenerator = new IDGenerator();
	
	// all the custom tags for command and attachments are stored in these maps.
	private  Map<String,CustomCommandTagDescriptor> commandTagMap = new HashMap<String, CustomCommandTagDescriptor>();
	private  Map<String,CustomAttachmentTagDescriptor> attachmentTagMap = new HashMap<String, CustomAttachmentTagDescriptor>();
		
	public XmlOrchConfigurator() {
		setupDefaultTags();
	}
	
	public XmlOrchConfigurator(List<String> files,BeanFactoryAdapter factory) throws OwizConfigException {
		this();
		setBeanFactoryAdapter(factory);
		for(String file:files){
			setFilename(file);			
		}
	}
	
	protected void setupDefaultTags() {
		// pre-defined commands and attach tags.
		commandTagMap.put("command",new CustomCommandTagDescriptor("command"));
		CustomCommandTagDescriptor cctd = new CustomCommandTagDescriptor("chain");
		cctd.setComponentName(Chain.class.getName());
		commandTagMap.put("chain",cctd);
		
		attachmentTagMap.put("attach-to", new CustomAttachmentTagDescriptor("attach-to"));
	}
		
	@SuppressWarnings("rawtypes")
	public void initDigester(){
		if (digester != null) return;
		digester = new Digester();
		digester.setNamespaceAware(true);
		digester.setRules(new ExtendedBaseRules()); // to make sure that we match wild cards such as ? and *
		
		digester.addObjectCreate("flows/add-command-tag", CustomCommandTagDescriptor.class);
		digester.addSetProperties("flows/add-command-tag");
		digester.addSetNext("flows/add-command-tag","addCommandTag");
		
		digester.addObjectCreate("flows/add-attach-tag", CustomAttachmentTagDescriptor.class);
		digester.addSetProperties("flows/add-attach-tag");
		digester.addSetNext("flows/add-attach-tag","addAttachmentTag");
		
		digester.addRule("flows/flow", new CreateOrUseExistingRule<FlowDescriptor>(FlowDescriptor.class,"obtainFlow",
							FLOW_STACK,"addFlow"));
		
		digester.addRule("flows/flow/*", new CommandAttachmentDelegatorRule<InputType>(idGenerator,
				commandTagMap, attachmentTagMap));
		digester.addSetProperties("flows/flow");
	}
	
	public void addCommandTag(CustomCommandTagDescriptor cctd){
		commandTagMap.put(cctd.getTag(), cctd);
	}
	
	public void addAttachmentTag(CustomAttachmentTagDescriptor catd){
		attachmentTagMap.put(catd.getTag(),catd);
	}
		
	public void setFilename(String ...names) throws OwizConfigException{
		initDigester();
		for (String name: names){
			try {
				processFile(name);
			} catch (IOException | SAXException e) {
				throw new OwizConfigException("Error while process File name :"+name,e);
			}
		}
		processFlows();
	}
		
	protected void processFile(String name) throws OwizConfigException, IOException, SAXException{
		Enumeration<URL> urls = getClass().getClassLoader().getResources(name);
		while(urls.hasMoreElements()) {
			URL url = urls.nextElement();
			digester.push(this);
			digester.parse(url);
		}
	}
	 
	public void completeInit() throws OwizConfigException{
		processFlows();
	}
}
