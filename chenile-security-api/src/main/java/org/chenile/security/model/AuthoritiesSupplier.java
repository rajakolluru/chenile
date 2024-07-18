package org.chenile.security.model;

import org.chenile.core.context.ChenileExchange;

/**
 * Implement this interface to supply a list of authorities that we would like to validate
 * against the Principal to see if this service can be accessed. This is not mandatory to implement.
 * The service can instead give us a Lambda that returns the authprities from {@link ChenileExchange}
 */
public interface AuthoritiesSupplier {
    public String[] getAuthorities(ChenileExchange exchange);
}
