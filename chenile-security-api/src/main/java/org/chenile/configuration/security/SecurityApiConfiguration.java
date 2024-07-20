package org.chenile.configuration.security;

import org.chenile.security.service.SecurityConfigService;
import org.chenile.security.service.impl.SecurityConfigServiceImpl;
import org.chenile.security.service.impl.StmSecurityStrategyImpl;
import org.chenile.stm.STMSecurityStrategy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SecurityApiConfiguration {
    @Bean
    SecurityConfigService securityConfigService(){
        return new SecurityConfigServiceImpl();
    }
    @Bean
    STMSecurityStrategy stmSecurityStrategy(){
        return new StmSecurityStrategyImpl();
    }
}