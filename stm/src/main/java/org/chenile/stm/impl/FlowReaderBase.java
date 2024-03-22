package org.chenile.stm.impl;

import org.chenile.stm.FlowReader;

public class FlowReaderBase implements FlowReader {
	protected STMFlowStoreImpl stmFlowStoreImpl ;
	
	protected FlowReaderBase(STMFlowStoreImpl stmFlowStoreImpl){
		this.stmFlowStoreImpl = stmFlowStoreImpl;
	}
	
}
