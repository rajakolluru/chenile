package org.chenile.owiz;

/**
 * The execution interface of the orchestrator.
 * This is the one that gets typically utilized by the consumers to execute the orchestration chain of
 * commands.
 *
 */
public interface OrchExecutor<InputType> {
	public void execute(InputType context) throws Exception;
	public void execute(String flowId, InputType context) throws Exception;
}
