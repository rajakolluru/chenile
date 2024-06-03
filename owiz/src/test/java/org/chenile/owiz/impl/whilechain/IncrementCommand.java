package org.chenile.owiz.impl.whilechain;

import org.chenile.owiz.Command;

/**
 * The index incrementer.
 */
public class IncrementCommand implements Command <WhileContext>{
    @Override
    public void execute(WhileContext context) throws Exception {
        context.index++;
    }
}
