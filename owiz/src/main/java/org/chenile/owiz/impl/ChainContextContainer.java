package org.chenile.owiz.impl;

/**
 * The context that is required to be passed to filter chain. Filter chain needs chain Context to work.
 * Hence we need to make sure that the context passed contains a ChainContext
 * @author Raja Shankar Kolluru
 *
 */

public interface ChainContextContainer<InputType> {
	public void setChainContext(ChainContext<InputType> chainContext);
	public ChainContext<InputType> getChainContext();
}
