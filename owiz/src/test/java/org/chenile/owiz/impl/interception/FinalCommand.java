package org.chenile.owiz.impl.interception;

import org.chenile.owiz.Command;

public class FinalCommand implements Command<InterceptionContext> {
    private final String name;
    public FinalCommand(String name) {
        this.name = name;
    }

    @Override
    public void execute(InterceptionContext context) throws Exception {
        context.executionOrder.add(name);
    }
}
