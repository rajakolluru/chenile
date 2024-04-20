package org.chenile.owiz.impl.interception;

import org.chenile.owiz.Command;
import org.chenile.owiz.impl.ChainContext;

public class Interceptor implements Command<InterceptionContext> {
    private final String name;
    private boolean repeat = false;
    public Interceptor(String name){
        this.name = name;
    }
    @Override
    public void execute(InterceptionContext context) throws Exception {
        ChainContext.SavePoint savePoint = null;
        context.executionOrder.add("pre" + name);
        if(repeat){
            savePoint = context.chainContext.savePoint();
            context.chainContext.doContinue();
            context.chainContext.resumeFromSavedPoint(savePoint);
        }else
            context.chainContext.doContinue();
        context.executionOrder.add("post" + name);
    }

    public void setRepeat(boolean repeat) {
        this.repeat = repeat;
    }
}
