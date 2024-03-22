package org.chenile.owiz;

import org.chenile.owiz.impl.ParallelChain;

/**
 * Implement this interface if a command can be bypassed.
 * By doing so, we can economize on resources dedicated to this command if it needs
 * to be bypassed. Some implementations use this feature (see {@link ParallelChain} for example)
 * @author Raja Shankar Kolluru
 *
 */
public interface BypassableCommand<InputType> {
	public boolean bypass(InputType context) ;
}
