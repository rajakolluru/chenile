package org.chenile.workflow.service.stmcmds;

import java.util.HashMap;
import java.util.Map;

import org.chenile.owiz.BeanFactoryAdapter;
import org.chenile.owiz.Command;
import org.chenile.owiz.OrchExecutor;
import org.chenile.owiz.config.impl.XmlOrchConfigurator;
import org.chenile.owiz.impl.OrchExecutorImpl;
import org.chenile.stm.STMInternalTransitionInvoker;
import org.chenile.stm.State;
import org.chenile.stm.action.STMTransitionAction;
import org.chenile.stm.model.Transition;
import org.chenile.workflow.model.AbstractStateEntity;
import org.chenile.workflow.service.stmcmds.dto.TransitionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

public class BaseTransitionAction<T extends AbstractStateEntity> implements STMTransitionAction<T> {
	//@Autowired
	//private AuditLogger auditLogger ;
	@Autowired
	private ApplicationContext applicationContext;

	@Override
	public final void doTransition(T entity, Object transitionParam, State startState, String eventId, State endState,
			STMInternalTransitionInvoker<?> stm,Transition transition) throws Exception {
		Map<String, String> metadata = transition.getMetadata();
		TransitionContext<T> context = new TransitionContext<T>(entity, eventId, transitionParam,
				startState,endState,transition);
		if (metadata != null && metadata.get("command") != null) {
			@SuppressWarnings("unchecked")
			Command<TransitionContext<T>> command = (Command<TransitionContext<T>>)applicationContext.getBean(metadata.get("command"));
			command.execute(context);
			return;
		}else if (metadata != null &&metadata.get("orchExecutor") != null) {
			@SuppressWarnings("unchecked")
			OrchExecutor<TransitionContext<T>> command = (OrchExecutor<TransitionContext<T>>)applicationContext.getBean(metadata.get("orchExecutor"));
			command.execute(context);
			return;
		}else if(metadata != null &&metadata.get("orchestratedCommandsConfiguration") != null) {
			processMicroactions(metadata.get("orchestratedCommandsConfiguration"),context);
			return;
		}else {
			transition(entity,transitionParam,startState, eventId,endState, stm);
			return;
		}
	}
				
	protected void processMicroactions(String microActionsXml, TransitionContext<T> context) throws Exception{
		OrchExecutor<TransitionContext<T>> orchExecutor = obtainOrchExecutor(microActionsXml);
		orchExecutor.execute(context);
	}
	
	protected Map<String,OrchExecutor<TransitionContext<T>>> orchMap = new HashMap<String, OrchExecutor<TransitionContext<T>>>();
	
	protected OrchExecutor<TransitionContext<T>> obtainOrchExecutor(String microActionsXml) {
		if (orchMap.get(microActionsXml) != null)
			return orchMap.get(microActionsXml);
		XmlOrchConfigurator<TransitionContext<T>> xmlOrchConfigurator = new XmlOrchConfigurator<TransitionContext<T>>();
		xmlOrchConfigurator.setBeanFactoryAdapter(new BeanFactoryAdapter() {
			@Override
			public Object lookup(String componentName) {
				return applicationContext.getBean(componentName);
			}
		});
		xmlOrchConfigurator.setFilename(microActionsXml);
		OrchExecutorImpl<TransitionContext<T>> orchExecutor = new OrchExecutorImpl<TransitionContext<T>>();
		orchExecutor.setOrchConfigurator(xmlOrchConfigurator);
		orchMap.put(microActionsXml, orchExecutor);
		return orchExecutor;
	}

	// Override this method instead of the doTransition method above. 
	public void transition(T entity, Object transitionParam, State startState,String eventId, State endState,
			STMInternalTransitionInvoker<?> stm) throws Exception {
	}

}
