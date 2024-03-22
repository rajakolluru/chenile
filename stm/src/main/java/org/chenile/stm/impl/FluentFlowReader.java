package org.chenile.stm.impl;

import org.chenile.stm.model.EventInformation;
import org.chenile.stm.model.FlowDescriptor;
import org.chenile.stm.ognl.OgnlScriptingStrategy;

public class FluentFlowReader extends FlowReaderBase{

	public FluentFlowReader(STMFlowStoreImpl stmFlowStoreImpl) {
		super(stmFlowStoreImpl);
		stmFlowStoreImpl.setScriptingStrategy(new OgnlScriptingStrategy());
	}
	
	public FluentFlowReader addEventInformation(EventInformation eventInformation){		
		stmFlowStoreImpl.addEventInformation(eventInformation);
		return this;
	}
	

	public FlowDescriptor newFlow(String id) {
		FlowDescriptor fd = new FlowDescriptor();
		fd.setId(id);
		stmFlowStoreImpl.addFlow(fd);
		return fd;
	}
	
	

}
