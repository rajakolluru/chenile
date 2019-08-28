package org.chenile.owiz.impl.splitaggregate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.chenile.owiz.impl.splitaggregate.IndividualSplitContext;
import org.chenile.owiz.impl.splitaggregate.SplitterContext;

public class MockSplitterContext implements SplitterContext {

	List<IndividualSplitContext> list = new ArrayList<IndividualSplitContext>();
	public void setList(List<IndividualSplitContext> list) {
		this.list = list;
	}

	Map<String,Object> retObjects = new HashMap<String, Object>();
	
	public List<IndividualSplitContext> obtainList() {
		return list;
	}

	public void addToAggregate(IndividualSplitContext context) {
		retObjects.put(context.getKey(),context.getValue());
	}

	public Map<String, Object> getAggregatedContext() {
		return retObjects;
	}

	public void addErrorToAggregate(IndividualSplitContext context, Object err) {
		retObjects.put(context.getKey(), err);
	}

}