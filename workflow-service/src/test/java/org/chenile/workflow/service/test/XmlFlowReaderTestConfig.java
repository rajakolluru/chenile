package org.chenile.workflow.service.test;

import jakarta.annotation.PostConstruct;
import org.chenile.stm.impl.STMFlowStoreImpl;
import org.chenile.stm.impl.XmlFlowReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.test.context.ActiveProfiles;

@Configuration
@PropertySource("classpath:org/chenile/workflow/service/test/TestWorkflowService.properties")
@SpringBootApplication(scanBasePackages = { "org.chenile.configuration" })
@ActiveProfiles("unittest")
public class XmlFlowReaderTestConfig {

    private static final String FLOW_DEFINITION_FILE = "org/chenile/workflow/service/test/issues.xml";


    @Autowired
    @Qualifier("issueFlowStore")
    STMFlowStoreImpl flowStore;

    @PostConstruct
    public void afterContext() throws Exception {
        XmlFlowReader flowReader = new XmlFlowReader(flowStore);
        flowReader.setFilename(FLOW_DEFINITION_FILE);
    }
}
