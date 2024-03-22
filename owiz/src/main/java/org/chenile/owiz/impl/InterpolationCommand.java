package org.chenile.owiz.impl;

import java.util.List;

import org.chenile.base.exception.ServerException;
import org.chenile.owiz.Command;

/**
 * This is a "dummy" command which needs to be attached to a parent command that 
 * supports this type of command. 
 * This command will not be executed. Instead, the {@link #fetchCommands(Object)} will be 
 * called to obtain the actual list of commands that will be executed.
 * Hence this command serves to interpolate the list of commands by adding more to the list.
 * It must be used with caution since this can change the runtime execution command list
 * @author Raja Shankar Kolluru
 *
 */
public abstract class InterpolationCommand<InputType> extends CommandBase<InputType>{

	/**
	 * This method will throw an exception since it should never be executed by the 
	 * chain that contains it. 
	 */
	@Override
	protected final void doExecute(InputType context) throws Exception {
		throw new ServerException(901, new Object[] {getId(),getParentId()});		
	}
	
	protected abstract List<Command<InputType>> fetchCommands(InputType context);
	
}
