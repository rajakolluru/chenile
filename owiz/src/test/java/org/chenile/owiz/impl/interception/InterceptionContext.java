package org.chenile.owiz.impl.interception;

import org.chenile.owiz.impl.ChainContext;
import org.chenile.owiz.impl.ChainContextContainer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InterceptionContext implements ChainContextContainer<InterceptionContext> {
    public List<String> executionOrder = new ArrayList<>();
    ChainContext<InterceptionContext> chainContext ;
    @Override
    public void setChainContext(ChainContext<InterceptionContext> chainContext) {
       this.chainContext = chainContext;
    }

    @Override
    public ChainContext<InterceptionContext> getChainContext() {
        return this.chainContext;
    }
}
