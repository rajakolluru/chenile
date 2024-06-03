package org.chenile.owiz.impl;

import org.chenile.owiz.Command;


/**
 * This command does nothing. Useful for setting up chains that have "cul-de-sacs".
 * @param <InputType>
 */
public class DoNothing<InputType> implements Command<InputType> {

	public void execute(InputType context) throws Exception {		
	}

}
