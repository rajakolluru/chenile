package org.chenile.owiz.impl.ognl;

import org.chenile.owiz.impl.CommandBase;

public class PrintFT extends CommandBase<FindFulfillmentTypeContext>{

    @Override
    public void doExecute(FindFulfillmentTypeContext context) throws Exception {
        String prefix = getConfigValue("at");
        System.out.println(prefix + " Fulfillment type = " + context.getFulfillmentType());
    }
}
