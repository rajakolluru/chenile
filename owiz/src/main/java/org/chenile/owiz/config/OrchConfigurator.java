package org.chenile.owiz.config;

import org.chenile.owiz.Command;
import org.chenile.owiz.config.model.CommandDescriptor;

/**
 * Reads the configuration of the Orchestration possibly from multiple sources and makes it available for anyone to use it.
 * The signatures support a notion of flow Id which is considered optional. If the entire application is considered 
 * as one flow then flow Id is not required. (i.e. every request starts with the same command and culminates in the correct
 * sequence of commands being executed)
 * <p>However, if different flows in the application start with different commands then the notion of flow Id can be useful.
 * 
 * @author Raja Shankar Kolluru
 *
 */
public interface OrchConfigurator<InputType> {
	
	/**
	 * Obtain the first command for the given flow Id. Then the execute method can be called to obtain the results.
	 * @param flowId is the name of the flow for which the first command is to be obtained
	 * @return the first command for the passed flow Id.
	 */
	public Command<InputType> obtainFirstCommand(String flowId);
	/**
	 * This method is useful for insrumentation purposes.
	 * @param flowId is the name of the flow for which the first command info is to be obtained
	 * @return the meta data about the flow.
	 */
	public CommandDescriptor<InputType> obtainFirstCommandInfo(String flowId);
	/**
	 * This method is useful for insrumentation purposes.
	 * @param flowId is the name of the flow to which the command belongs 
	 * @param commandId is the name of the command for which the information is to be obtained
	 * @return the command configuration information for the combination of flow and command Ids. 
	 */
	public CommandDescriptor<InputType> obtainCommandInfo(String flowId,String commandId);
	
	
	public Command<InputType> obtainFirstCommand();
	public CommandDescriptor<InputType> obtainFirstCommandInfo();
	public CommandDescriptor<InputType> obtainCommandInfo(String commandId);
	/**
	 * Useful for debugging purposes.
	 * @return an xml representation of the entire flow (or multiple flows if more than one are configured)
	 */
	public String toXml();
}
