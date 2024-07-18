package org.chenile.security.service.impl;

import org.chenile.core.context.ChenileExchange;
import org.chenile.security.model.AuthoritiesSupplier;
import org.chenile.security.model.SecurityConfig;
import org.chenile.security.service.SecurityConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import java.util.function.Function;

public class SecurityConfigServiceImpl implements SecurityConfigService {
    @Autowired
    ApplicationContext applicationContext;
    public String[] getGuardingAuthorities(ChenileExchange exchange){
        SecurityConfig config = getSecurityConfig(exchange);
        if (config == null) return null;
        if (config.authorities().length != 0){
            return config.authorities();
        }
        if (!config.authoritiesSupplier().isEmpty()){
            Object supplier = applicationContext.getBean(config.authoritiesSupplier());
            return executeAuthoritiesSupplier(supplier,exchange);
        }
        return null;
    }
    /**
     *
     * @param obj The object
     * @param exchange the exchange
     * @return the authorities if available
     */
    @SuppressWarnings("unchecked")
    private String[] executeAuthoritiesSupplier(Object obj, ChenileExchange exchange){
        String[] auths = null;
        if (obj instanceof AuthoritiesSupplier as){
            auths = as.getAuthorities(exchange);
        }
        if (obj instanceof Function<?,?> f1){
            Function<ChenileExchange,String[]> f2 = (Function<ChenileExchange,String[]>)f1;
            auths = f2.apply(exchange);
        }
        return auths;
    }

    private SecurityConfig getSecurityConfig(ChenileExchange exchange){
        return exchange.getExtensionByAnnotation(SecurityConfig.class);
    }
}
