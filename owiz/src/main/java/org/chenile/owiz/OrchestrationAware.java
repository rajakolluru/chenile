package org.chenile.owiz;

import org.chenile.owiz.config.OrchConfigurator;
import org.chenile.owiz.config.model.CommandDescriptor;

/**
 * Applicable for commands that choose to be aware of their orchestration configuration.
 * @author Raja Shankar Kolluru
 *
 */

public interface OrchestrationAware<InputType> {
	public void setOrchConfigurator(OrchConfigurator<InputType> orchConfigurator);
	public void setCommandDescriptor(CommandDescriptor<InputType> commandDescriptor);
}
