package org.chenile.workflow.service.stmcmds;

import com.fasterxml.jackson.core.type.TypeReference;
import org.chenile.core.context.ChenileExchange;
import org.chenile.owiz.Command;
import org.chenile.stm.impl.STMActionsInfoProvider;
import org.chenile.stm.model.EventInformation;
import org.chenile.workflow.service.impl.StateEntityServiceImpl;

import java.lang.reflect.Type;

/**
 * Selects the body type of the {@link ChenileExchange} based on the event Id passed
 * This should be used as the body type selector for the {@link StateEntityServiceImpl#process(org.chenile.stm.StateEntity, String, Object)}
 * method. Will work with all subclasses.
 * Assumptions:<br/>
 * <ol>
 * <li>Second parameter for the process method will be mapped to a header parameter called "eventId".
 * <li>The body type has been defined in the stm in a states/event-information section. eventId must be mapped to
 * meta attribute called "meta-bodyType"
 * </ol>
 * <p>
 * @author Raja Shankar Kolluru
 *
 */
public class StmBodyTypeSelector implements Command<ChenileExchange>{
	
	private final STMActionsInfoProvider stmActionsInfoProvider;
	public StmBodyTypeSelector(STMActionsInfoProvider stmActionsInfoProvider) {
		this.stmActionsInfoProvider = stmActionsInfoProvider;
	}
	
	@Override
	public void execute(ChenileExchange exchange) throws Exception {
		String eventId = exchange.getHeader("eventID",String.class);
		EventInformation eventInformation = stmActionsInfoProvider.getEventInformation(eventId);
		if (null != eventInformation) {
			String bodyTypeClass = eventInformation.getMetadata().get("bodyType");
			Class<?> bodyType = Class.forName(bodyTypeClass);
			exchange.setBodyType(new TypeReference<Object>() {

				@Override
				public Type getType() {
					return bodyType;
				}
				
			});
		}
	}

}
