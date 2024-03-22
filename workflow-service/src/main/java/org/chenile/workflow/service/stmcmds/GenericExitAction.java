package org.chenile.workflow.service.stmcmds;

import org.chenile.stm.action.STMAction;
import org.chenile.workflow.model.AbstractStateEntity;

public class GenericExitAction<T extends AbstractStateEntity> implements STMAction<T> {
//	@Autowired
//	private AuditLogger auditLogger ;

	/**
	 * @param auditLogger the auditLogger to set
	 */
	//public void setAuditLogger(AuditLogger auditLogger) {
//		this.auditLogger = auditLogger;
	// }

	@Override
	public void execute(T entity) throws Exception {
//		auditLogger.doAudit(entity.getClass().getName(), entity.getId(), "EXIT-ACTION",entity.getCurrentState().getStateId(), "");		
	}

}
