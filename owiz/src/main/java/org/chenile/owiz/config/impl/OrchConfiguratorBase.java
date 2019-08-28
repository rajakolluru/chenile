package org.chenile.owiz.config.impl;

import java.util.LinkedHashMap;
import java.util.Map;

import org.chenile.owiz.AttachableCommand;
import org.chenile.owiz.BeanFactoryAdapter;
import org.chenile.owiz.Command;
import org.chenile.owiz.OrchestrationAware;
import org.chenile.owiz.config.OrchConfigurator;
import org.chenile.owiz.config.model.AttachmentDescriptor;
import org.chenile.owiz.config.model.CommandDescriptor;
import org.chenile.owiz.config.model.FlowDescriptor;
import org.chenile.owiz.exception.OwizConfigException;

/**
 * A base implementation of OrchConfigurator that stores all the relevant information in internal variables and provides 
 * the services as expected by OrchConfigurator. Sub classes are expected to populate the information after reading it
 * from various sources (such as a database or xml file) 
 * @author rkollu
 *
 * @param <InputType> .
 */

public class OrchConfiguratorBase<InputType> implements OrchConfigurator<InputType>{

	private Map<String,FlowDescriptor<InputType>> flowCatalog = new LinkedHashMap<String, FlowDescriptor<InputType>>();
	private FlowDescriptor<InputType> defaultFlow;
	private BeanFactoryAdapter beanFactoryAdapter;

	public OrchConfiguratorBase() {
		super();
	}


	public Command<InputType> obtainFirstCommand(String flowId) {
		CommandDescriptor<InputType> firstCommand = obtainFirstCommandInfo(flowId);
		return (firstCommand == null)? null : firstCommand.getCommand();
	}

	public CommandDescriptor<InputType> obtainCommandInfo(String flowId, String commandId) {
		Map<String,CommandDescriptor<InputType>> commandCatalog = obtainCommandCatalog(flowId);
		return commandCatalog.get(commandId);
	}

	public CommandDescriptor<InputType> obtainFirstCommandInfo(String flowId) {
		FlowDescriptor<InputType> flowDescriptor = flowCatalog.get(flowId);
		return flowDescriptor.obtainFirstCommandInfo();
	}

	public Map<String,CommandDescriptor<InputType>> obtainCommandCatalog(String flowId) {
		FlowDescriptor<InputType> flowDescriptor = flowCatalog.get(flowId);
		return flowDescriptor.getCommandCatalog();
	}

	public FlowDescriptor<InputType> obtainFlow(String flowId) {
		return flowCatalog.get(flowId);
	}

	public CommandDescriptor<InputType> obtainCommandInfo(String commandId) {
		return (defaultFlow == null)? null : obtainCommandInfo(defaultFlow.getId(),commandId);
	}

	public Command<InputType> obtainFirstCommand() {
		return (defaultFlow == null)? null : obtainFirstCommand(defaultFlow.getId());
	}

	public CommandDescriptor<InputType> obtainFirstCommandInfo() {
		return (defaultFlow == null)? null : obtainFirstCommandInfo(defaultFlow.getId());
	}

	public void addFlow(FlowDescriptor<InputType> fd) {
		flowCatalog.put(fd.getId(),fd);
		if (defaultFlow == null){
			defaultFlow = fd;
			defaultFlow.setDefaultFlow(true);
			return;
		}
		if (fd.isDefaultFlow()){
			if (defaultFlow != null){
				defaultFlow.setDefaultFlow(false);
			}
			defaultFlow = fd;
		}
	}

	protected void processFlows() throws OwizConfigException {
		for (FlowDescriptor<InputType> fd : flowCatalog.values()){
			try {
				processChain(fd.getCommandCatalog());
			} catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
				throw new OwizConfigException("Error while process flow config in OrchConfiguratorBase",e);
			} 
		}
	}

	private void processChain(Map<String,CommandDescriptor<InputType>> commandCatalog) throws OwizConfigException, InstantiationException, IllegalAccessException, ClassNotFoundException {
		// create or lookup the commands for all the descriptors.
		// attach parent command descriptors to every command descriptor
		for (CommandDescriptor<InputType> cd: commandCatalog.values()){
			cd.setCommand(makeCommand(cd));
			for(AttachmentDescriptor<InputType> ad : cd.getAttachmentDescriptors()){
				ad.setParent(commandCatalog.get(ad.getParentId()));
			}
		}
		for (CommandDescriptor<InputType> cd: commandCatalog.values()){
			attachCommandToParent(cd,commandCatalog);
		}
	}

	private void attachCommandToParent(CommandDescriptor<InputType> cd, Map<String,CommandDescriptor<InputType>> commandCatalog)
			throws OwizConfigException {
				for(AttachmentDescriptor<InputType> ad : cd.getAttachmentDescriptors()){
				
					CommandDescriptor<InputType> cdp = commandCatalog.get(ad.getParentId());
					if (cdp == null) 
						throw new OwizConfigException("Undefined parent " + ad.getParentId() + " for command " + cd.getId());
					ad.setParent(cdp);
					Command<InputType> parentCommand = cdp.getCommand();
					if (!(parentCommand instanceof AttachableCommand<?>)) 
						throw new OwizConfigException("Parent " + cdp.getId() + " is not an attachable command!");
					// now attach the command to its parent. parent must be castable to AttachableCommand.
					AttachableCommand<InputType> ac = (AttachableCommand<InputType>) parentCommand;
					ac.attachCommand(ad, cd);
				}
			}

	@SuppressWarnings("unchecked")
	protected Command<InputType> makeCommand(CommandDescriptor<InputType> cd) throws OwizConfigException, InstantiationException, IllegalAccessException, ClassNotFoundException {
		Command<InputType> command = lookupCommand(cd.getComponentName());
		if (command != null && command instanceof OrchestrationAware<?>){
			OrchestrationAware<InputType> oa = (OrchestrationAware<InputType>) command;
			oa.setOrchConfigurator(this);
			oa.setCommandDescriptor(cd);
		}
		return command;
	}

	/**
	 * This method uses the bean factory adapter to look up various bean factories. Hence over-riding this class 
	 * would not be required. Instead provide a bean factory adapter that does the look up. 
	 * @param componentName .
	 * @return Command .
	 * @throws ClassNotFoundException 
	 * @throws IllegalAccessException 
	 * @throws InstantiationException 
	 * @throws OwizConfigException .
	 */
	@SuppressWarnings("unchecked")
	protected Command<InputType> lookupCommand(String componentName) throws OwizConfigException, InstantiationException, IllegalAccessException, ClassNotFoundException {
		Object obj = null;
		if (beanFactoryAdapter !=  null )
			obj = beanFactoryAdapter.lookup(componentName);
		if (obj == null)
		   obj = Class.forName(componentName).newInstance();
		return  (Command<InputType>) obj;
	}

	public String toXml() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("<flows>\n");
		for (FlowDescriptor<InputType> fd: flowCatalog.values()){
			buffer.append(fd.toXml());
		}
		buffer.append("</flows>\n");
		return buffer.toString();
	}

	public void setBeanFactoryAdapter(BeanFactoryAdapter beanFactoryAdapter) {
		this.beanFactoryAdapter = beanFactoryAdapter;
	}

	public BeanFactoryAdapter getBeanFactoryAdapter() {
		return beanFactoryAdapter;
	}

}
