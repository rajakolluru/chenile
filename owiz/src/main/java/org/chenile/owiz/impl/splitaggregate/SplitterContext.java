package org.chenile.owiz.impl.splitaggregate;

import java.util.List;
import java.util.Map;

public interface SplitterContext {
	
	public List<IndividualSplitContext> obtainList();
	public void  addToAggregate(IndividualSplitContext context);
	public void addErrorToAggregate(IndividualSplitContext context, Object err);
	public Map<String,Object> getAggregatedContext();
}