package org.chenile.owiz.impl.whilechain;

import org.chenile.owiz.Command;

/**
 * Increment the counter to track how many times the loop executed
 */
public class TrackLoopExecutions implements Command <WhileContext>{
    @Override
    public void execute(WhileContext context) throws Exception {
        context.counter++;
    }
}
