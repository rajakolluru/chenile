package org.chenile.owiz;

/**
 * A command is designed to be a unit of work. Commands implement one responsibility and should be designed to be
 * "chained" together to accomplish specific use cases.
 * @author Raja Shankar Kolluru
 *
 */
public interface Command<InputType>{
	public void execute(InputType context) throws Exception;
}
