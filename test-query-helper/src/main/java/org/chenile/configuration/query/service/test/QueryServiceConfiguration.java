package org.chenile.configuration.query.service.test;

import org.chenile.query.service.SecuritySettings;
import org.chenile.test.query.store.QueryStoreSettings;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class QueryServiceConfiguration {

    @Bean
    QueryStoreSettings queryStoreSettings(){
        return new QueryStoreSettings();
    }

    @Bean
    SecuritySettings securitySettings(){
        return new SecuritySettings();
    }

 
}
