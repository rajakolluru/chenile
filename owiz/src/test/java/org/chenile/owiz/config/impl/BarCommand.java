package org.chenile.owiz.config.impl;

import org.chenile.owiz.impl.BaseContext;
import org.chenile.owiz.impl.CommandBase;

public class BarCommand extends CommandBase<BaseContext>{

			
	@Override
	protected void doExecute(BaseContext context) throws Exception {
		context.put("visited",getConfigValue("veg"));
		// log.info("visited bar. veg = " + getConfigValue("veg"));
		
	}
}
