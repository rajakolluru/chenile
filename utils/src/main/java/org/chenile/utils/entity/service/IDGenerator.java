package org.chenile.utils.entity.service;

import org.chenile.core.context.ContextContainer;
import org.springframework.beans.factory.annotation.Autowired;

import javax.naming.Context;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Generates IDs given a request context.
 */
public abstract class IDGenerator {
    private static final ThreadLocal<Map<String,Integer>> mapThreadLocal = new ThreadLocal<>();

    /**
     * This method generates a new unique ID for the given prefix. The ID is based out
     * of the notion of a request ID and is guaranteed to be unique.
     * @param prefix Prefix that needs to be used.
     * @return the ID
     */
    public static String generateID(String prefix){
        ContextContainer contextContainer = ContextContainer.CONTEXT_CONTAINER;
        String requestId = contextContainer.getRequestId();
        return requestId + prefix + obtainCounter(prefix);
    }

    private static int obtainCounter(String prefix){
        int counter = obtainIdMap().computeIfAbsent(prefix, (pfx)-> 0);
        obtainIdMap().put(prefix,++counter);
        return counter;
    }

    private static Map<String,Integer> obtainIdMap(){
        Map<String,Integer> idMap = mapThreadLocal.get();
        if (idMap == null) {
            idMap = new HashMap<>();
            mapThreadLocal.set(idMap);
        }
        return idMap;
    }
}
