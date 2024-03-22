package org.chenile.owiz.impl;

import java.util.List;

import org.chenile.owiz.AttachableCommand;
import org.chenile.owiz.Command;

/**
 * <p>A variation of the Chain for AOP like surround processing. In this implementation, the filter
 * chain manufactures a chain of commands with each command having to continue the chain explicitly
 * or return. This allows commands to implement surround processing. Normal chain does not allow "wrapping"
 * functionality which is required for surround interception.</p>
 * <p>The InputType passed must have a way of continuing the chain by wrapping the ChainContext. Hence it needs to 
 * implement {@link ChainContextContainer}. All commands called by the chain can then assume that the context 
 * implements the interface. The {@link ChainContext} 
 * can then be extracted from the InputType and the doContinue() method can then be called by the command.</p>
 * 
 * @author Raja Shankar Kolluru
 *
 * @param <InputType> .
 */

public class FilterChain<InputType extends ChainContextContainer<InputType>> extends Chain<InputType> implements 
			Command<InputType>, AttachableCommand<InputType> {

	
	@Override
	protected void doExecute(InputType context) throws Exception {
		List<Command<InputType>> list = obtainExecutionCommands(context);
		ChainContext<InputType> chainContext = new ChainContext<InputType>(list,context);
		context.setChainContext(chainContext);
		chainContext.doContinue();
	}
	

}
