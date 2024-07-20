package org.chenile.workflow.api;

import org.chenile.stm.impl.STMActionsInfoProvider;

import java.util.HashMap;
import java.util.Map;

/**
 * Keeps the different components attached to various work flows in a registry.
 * This will be required when queries are made that fetch these workflow entities.
 * All workflow services register their components in this registry
 */
public abstract class WorkflowRegistry {
    public static Map<String,STMActionsInfoProvider> registry = new HashMap<>();
    public static STMActionsInfoProvider getSTMActionInfoProvider(String workflowName){
        return registry.get(workflowName);
    }
    public static void addSTMActionsInfoProvider(String workflowName,STMActionsInfoProvider provider){
        registry.put(workflowName,provider);
    }
}
