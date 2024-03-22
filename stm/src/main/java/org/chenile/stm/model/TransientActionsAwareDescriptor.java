package org.chenile.stm.model;

import org.chenile.stm.action.STMAction;
import org.chenile.stm.impl.STMFlowStoreImpl;

/**
 * Interface implemented by the descriptors that support transient actions. The descriptors include
 * {@link FlowDescriptor}, {@link StateDescriptor} and {@link STMFlowStoreImpl}
 * @author Raja Shankar Kolluru
 *
 */
public interface TransientActionsAwareDescriptor {
	public abstract void setEntryAction(STMAction<?> entryAction);
	public abstract void setExitAction(STMAction<?> exitAction);
}
