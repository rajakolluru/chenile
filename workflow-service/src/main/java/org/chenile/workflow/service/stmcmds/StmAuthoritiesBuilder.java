package org.chenile.workflow.service.stmcmds;

import org.chenile.core.context.ChenileExchange;
import org.chenile.stm.impl.STMActionsInfoProvider;
import org.chenile.stm.model.EventInformation;

import java.util.function.Function;

/**
 * This class returns the applicable authorities for the
 * {@link org.chenile.workflow.service.impl.StateEntityServiceImpl#processById(String, String, Object)}
 * method. The authorities are extracted from the states definition. <br/>
 * The {@link STMActionsInfoProvider} is required for this.
 */
public class StmAuthoritiesBuilder {
    private final STMActionsInfoProvider stmActionsInfoProvider;
    public StmAuthoritiesBuilder(STMActionsInfoProvider stmActionsInfoProvider) {
        this.stmActionsInfoProvider = stmActionsInfoProvider;
    }

    public Function<ChenileExchange, String[]> build() throws Exception {
        return (exchange) -> {
            String eventId = exchange.getHeader("eventID", String.class);
            EventInformation eventInformation = stmActionsInfoProvider.getEventInformation(eventId);
            if (null != eventInformation) {
                String acls = eventInformation.getMetadata().get("acls");
                if (acls == null || acls.isEmpty()) return null;
                return acls.split(",");
            }
            return null;
        };
    }
}
