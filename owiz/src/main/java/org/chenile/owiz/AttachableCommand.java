package org.chenile.owiz;

import org.chenile.owiz.config.model.AttachmentDescriptor;
import org.chenile.owiz.config.model.CommandDescriptor;

/**
 * A command becomes an attachable command if other commands can attach themselves to it using an attachment descriptor. 
 * Attachable commands 
 *
 * @author rkollu
 * 
 * @param <InputType> .
 */

public interface AttachableCommand<InputType> extends Command<InputType> {
	public void attachCommand(AttachmentDescriptor<InputType> attachmentDescriptor, CommandDescriptor<InputType> command);
}
