package org.chenile.samples.security;

import org.chenile.core.context.ChenileExchange;

import java.util.Collection;

/**
 * Implement this interface to supply a list of authorities that we would like to validate
 * against the Principal to see if this service can be accessed.
 */
public interface AuthoritiesSupplier {
    public String[] getAuthorities(ChenileExchange exchange);
}
