package org.chenile.owiz.impl.parallelchain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

import org.chenile.base.response.ResponseMessage;
import org.chenile.base.response.WarningAware;

public class ParallelChainContext implements WarningAware{
	public List<String> list = new ArrayList<>(); // keeps track of execution order of the commands
	public Map<String, CountDownLatch> latches = new HashMap<>();
	public List<ResponseMessage> warningMessages = new ArrayList<>();
	public ParallelChainContext() {
		// init latches 2, 3 and 4
		latches.put("Command2", new CountDownLatch(1));
		latches.put("Command3", new CountDownLatch(1));
		latches.put("Command4", new CountDownLatch(1));
	}
	@Override
	public List<ResponseMessage> getWarningMessages() {		
		return warningMessages;
	}
	@Override
	public void addWarningMessage(ResponseMessage m) {
		warningMessages.add(m);		
	}
	@Override
	public void removeAllWarnings() {
		list = new ArrayList<>();		
	}
}
