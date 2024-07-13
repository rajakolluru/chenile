package org.chenile.workflow.service.stmcmds;

import org.chenile.stm.StateEntity;
import org.chenile.stm.action.STMAction;

public class GenericExitAction<T extends StateEntity> implements STMAction<T> {
//	@Autowired
//	private AuditLogger auditLogger ;


	//public void setAuditLogger(AuditLogger auditLogger) {
//		this.auditLogger = auditLogger;
	// }

	@Override
	public void execute(T entity) throws Exception {
//		auditLogger.doAudit(entity.getClass().getName(), entity.getId(), "EXIT-ACTION",entity.getCurrentState().getStateId(), "");		
	}

}
