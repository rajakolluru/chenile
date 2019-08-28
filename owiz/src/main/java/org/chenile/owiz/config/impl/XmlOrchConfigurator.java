package org.chenile.owiz.config.impl;

import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.digester.Digester;
import org.apache.commons.digester.ExtendedBaseRules;
import org.apache.commons.digester.Rule;
import org.chenile.owiz.BeanFactoryAdapter;
import org.chenile.owiz.config.model.AttachmentDescriptor;
import org.chenile.owiz.config.model.CommandDescriptor;
import org.chenile.owiz.config.model.CustomAttachmentTagDescriptor;
import org.chenile.owiz.config.model.CustomCommandTagDescriptor;
import org.chenile.owiz.config.model.FlowDescriptor;
import org.chenile.owiz.exception.OwizConfigException;
import org.xml.sax.Attributes;
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
	protected static final String FLOW_STACK = "flowStack";
	// supports automatic ID generation for commands.
	private static int generatedIdSequence = 1;
	
	// all the custom tags for command and attachments are stored in these maps.
	private static Map<String,CustomCommandTagDescriptor> commandTagMap = new HashMap<String, CustomCommandTagDescriptor>();
	private static Map<String,CustomAttachmentTagDescriptor> attachmentTagMap = new HashMap<String, CustomAttachmentTagDescriptor>();
		
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
		commandTagMap.put("router",new CustomCommandTagDescriptor("router"));
		commandTagMap.put("chain",new CustomCommandTagDescriptor("chain"));
		
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
		
		digester.addRule("flows/flow/*", new CommandAttachmentDelegatorRule<InputType>());
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
		
	public static String generateId(){
		return "generatedID" + generatedIdSequence++;
	}
	
	@SuppressWarnings("rawtypes")
	private static HandleCommand handleCommand;
	@SuppressWarnings("rawtypes")
	private static HandleAttachment handleAttachment;

	/**
	 * Custom digester rule to process either the command or attach Descriptor.
	 * We don't know if the tag is a command or an attachment. This rule finds that out
	 * and delegates it to the respective rules for handling commands or attachments.
	 * @author raja
	 *
	 */
	
	private static class CommandAttachmentDelegatorRule<InputType> extends Rule {	
		public CommandAttachmentDelegatorRule() {
			handleCommand = new HandleCommand<InputType>();
			handleAttachment = new HandleAttachment<InputType>();
		}
		@Override
		public void setDigester(Digester digester) {
			super.setDigester(digester);
			handleCommand.setDigester(digester);
			handleAttachment.setDigester(digester);
		}
		@Override
		public void end(String namespace, String xmlElementName) throws Exception {
			super.end(namespace, xmlElementName);
			if (commandTagMap.containsKey(xmlElementName))
				handleCommand.end(namespace,xmlElementName);
			else if (attachmentTagMap.containsKey(xmlElementName))
				handleAttachment.end(namespace,xmlElementName);
			else
				throw new OwizConfigException("Tag name " + xmlElementName + " has not been configured!");
		}
		@Override
		public void begin(String namespace, String xmlElementName, Attributes attributes)
				throws OwizConfigException {
	
			if (commandTagMap.containsKey(xmlElementName))
				handleCommand.begin(namespace,xmlElementName,attributes);
			else if (attachmentTagMap.containsKey(xmlElementName))
				handleAttachment.begin(namespace,xmlElementName,attributes);
			else
				throw new OwizConfigException("Tag name " + xmlElementName + " has not been configured!");			
		}		
	}
	
	private static class HandleCommand<InputType> extends Rule {

		@Override
		public void begin(String namespace, String xmlElementName, Attributes attributes)
				throws OwizConfigException {
			
			CustomCommandTagDescriptor cctd = commandTagMap.get(xmlElementName);
			
			CommandDescriptor<InputType> commandDescriptor = new CommandDescriptor<InputType>();
			commandDescriptor.setCustomCommandTagDescriptor(cctd);
			digester.push(commandDescriptor);
			String componentName = cctd.getComponentName();
			
			// add the  attributes to the componentProperties
			for (int i = 0; i < attributes.getLength(); i++) {
	            String name = attributes.getLocalName(i);
	            if ("".equals(name)) {
	                name = attributes.getQName(i);
	            }
	            String value = attributes.getValue(i);
	            commandDescriptor.addComponentProperty(name, value);
			}
			// default the component name and ID if not supplied. 
			// Id would be generated while component name would be defaulted from the tag definition. 
			if(componentName != null && commandDescriptor.getComponentName() == null){
				commandDescriptor.setComponentName(componentName);
			}
			if (commandDescriptor.getId() == null)
				commandDescriptor.setId(generateId());
			// Ensure that additional attributes of the command tag are set in the command descriptor
			// as component properties. These would be available to the commands.
			
			for (String key: cctd.keySet() ){
				String value = cctd.get(key);
				commandDescriptor.addComponentProperty(key, value);
			}
			@SuppressWarnings("unchecked")
			FlowDescriptor<InputType> flowDescriptor = (FlowDescriptor<InputType>)
						digester.peek(FLOW_STACK);
			
			flowDescriptor.addCommand(commandDescriptor);
		}

		@Override
		public void end(String namespace, String xmlElementName) throws Exception {
			super.end(namespace, xmlElementName);
			@SuppressWarnings("unchecked")
			CommandDescriptor<InputType> commandDescriptor = (CommandDescriptor<InputType>) digester.peek();
				
			// Before popping the command out of the stack, we should ensure that every command (other than the first command)
			// is attached to another command. Otherwise the command will never be accessed by the framework during runtime!!
			// This is only possible if the commands are nested or if there is an attach-to clause under the command.
			// Otherwise we should throw an OwizConfigException. 
			// If the containing command has the notion of a default attachment descriptor, then that should be picked up
			// and the command must be attached to its parent using the default attachment descriptor.
			// If there is no default attachment descriptor then the "attach-to" tag is used by default.
			
			if (!commandDescriptor.isFirst() && commandDescriptor.getAttachmentDescriptors().size() == 0){
				Object o = digester.peek(1);
				// if it is not a top level command attach it to the command above it. 
				// if there is a default attachment type to the parent command.
				if (null == o || !(o instanceof CommandDescriptor))
					throw new OwizConfigException("Command " + commandDescriptor.getComponentName() + " and Id = " + commandDescriptor.getId()
							+ " will not be accessible by the framework since it is not attached to any other command. Did you " +
							"forget he attach-to tag or did you not nest it under its parent?");
				
				@SuppressWarnings("unchecked")
				CommandDescriptor<InputType> parent = (CommandDescriptor<InputType>)o;
				CustomCommandTagDescriptor parentCctd = parent.getCustomCommandTagDescriptor();
				String attachTag = parentCctd.getDefaultAttachmentTag();
				attachTag = (attachTag == null)? "attach-to" : attachTag;
	
				handleAttachment.begin(namespace,attachTag,null);
				
			}
			digester.pop();
		}		
	}
	
	private static class HandleAttachment<InputType> extends Rule {
		@Override
		public void begin(String namespace, String xmlElementName, Attributes attributes)
				throws OwizConfigException {
				CustomAttachmentTagDescriptor catd = attachmentTagMap.get(xmlElementName);
				
				@SuppressWarnings("unchecked")
				CommandDescriptor<InputType> commandDescriptor = (CommandDescriptor<InputType>) digester.peek();
				
				AttachmentDescriptor<InputType> ad = new AttachmentDescriptor<InputType>();	
				commandDescriptor.addAttachmentDescriptor(ad);
				if (null != attributes){
					for (int i = 0; i < attributes.getLength(); i++) {
			            String name = attributes.getLocalName(i);
			            if ("".equals(name)) {
			                name = attributes.getQName(i);
			            }
			            String value = attributes.getValue(i);
			            ad.put(name, value);
					}
				}
				// parentId must not be mandatory. automatically attach it to the containing command.
				if (ad.getParentId() == null){
					@SuppressWarnings("unchecked")
					CommandDescriptor<InputType> cd = (CommandDescriptor<InputType>) digester.peek(1);
					
					ad.setParentId(cd.getId());
				}
				// Ensure that additional attributes of the attach tag are set in the attachment descriptor
				// as  properties. These would be available to the containing commands as attachment attributes.
				
				for (String key: catd.keySet() ){
					String value = catd.get(key);
					ad.put(key, value);
				}
				// Ensure that the properties of the command are also available in the attachment descriptor
				// This will make the syntax much more succinct. 
				Map<String,String> props = commandDescriptor.getProperties();
				for (String key: props.keySet()){
					ad.put(key, props.get(key));
				}
				
		}		
	}
}
