package org.chenile.workflow.service.impl;

import org.chenile.stm.State;
import org.chenile.stm.impl.STMActionsInfoProvider;

public abstract class StateEntityHelper {
	
	protected static final String SLA_GETTING_LATE_IN_HOURS = "sla-getting-late-in-hours";
	protected static final String SLA_LATE_IN_HOURS = "sla-late-in-hours";

	public static int getSla(STMActionsInfoProvider stmActionsInfoProvider, State state, String slaType) {
		String s = stmActionsInfoProvider.getMetadata(state, slaType);
		if (s == null) return 0;
		return Integer.parseInt(s);
	}
	
	public static int getGettingLateTimeInHours(STMActionsInfoProvider stmActionsInfoProvider,State state) {
		return getSla(stmActionsInfoProvider,state,SLA_GETTING_LATE_IN_HOURS);
	}
	
	public static int getLateTimeInHours(STMActionsInfoProvider stmActionsInfoProvider,State state) {
		return getSla(stmActionsInfoProvider,state,SLA_LATE_IN_HOURS);
	}
}