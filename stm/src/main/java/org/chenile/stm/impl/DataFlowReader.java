package org.chenile.stm.impl;

import java.util.List;
import java.util.Map;

import org.chenile.stm.STMSecurityStrategy;
import org.chenile.stm.StateEntity;
import org.chenile.stm.StmDataProvider;
import org.chenile.stm.action.STMAction;
import org.chenile.stm.action.STMAutomaticStateComputation;
import org.chenile.stm.action.STMTransitionAction;
import org.chenile.stm.action.StateEntityRetrievalStrategy;
import org.chenile.stm.dto.FlowDescriptionDTO;
import org.chenile.stm.dto.StateAttributesDTO;
import org.chenile.stm.dto.StateDescriptionDTO;
import org.chenile.stm.dto.StatesDescriptionDTO;
import org.chenile.stm.dto.TransitionDescriptionDTO;
import org.chenile.stm.exception.STMException;
import org.chenile.stm.model.AutomaticStateDescriptor;
import org.chenile.stm.model.FlowDescriptor;
import org.chenile.stm.model.StateDescriptor;
import org.chenile.stm.model.StateTagDescriptor;
import org.chenile.stm.model.Transition;

public class DataFlowReader extends FlowReaderBase {

	private StmDataProvider stmDataProvider;

	public DataFlowReader(StmDataProvider dataProvider,STMFlowStoreImpl stmFlowStoreImpl) throws Exception {
		super(stmFlowStoreImpl);
		this.stmDataProvider = dataProvider;
	}

	public void init(String flowId) throws Exception {		

		if (stmDataProvider == null)
			return;
		StatesDescriptionDTO stmInfo = stmDataProvider.fillStmData(flowId);

		Map<String, StateTagDescriptor> statetags = stmFlowStoreImpl.actionTagsMap;
		List<FlowDescriptionDTO> flowsInfo = stmInfo.getFlowDescriptions();

		if(!flowsInfo.isEmpty()){
			if (stmFlowStoreImpl.flows.isEmpty()) {
				stmFlowStoreImpl.setEntryAction(stmInfo.getEntryAction(), true);
				stmFlowStoreImpl.setExitAction(stmInfo.getExitAction(), true);

				for (FlowDescriptionDTO flowInfo : flowsInfo) {
					FlowDescriptor flowDescriptor = new FlowDescriptor();
					setFlowDescriptorInfo(statetags, flowInfo, flowDescriptor);
					stmFlowStoreImpl.addFlow(flowDescriptor);
				}
			} else {
				for (FlowDescriptionDTO flowInfo : flowsInfo) {
					if (stmFlowStoreImpl.flows.containsKey(flowInfo.getFlowName())) {
						FlowDescriptor flowDescriptor = stmFlowStoreImpl.flows
								.get(flowInfo.getFlowName());
						setFlowDescriptorInfo(statetags, flowInfo, flowDescriptor);
					} else {
						FlowDescriptor flowDescriptor = new FlowDescriptor();
						setFlowDescriptorInfo(statetags, flowInfo, flowDescriptor);
						stmFlowStoreImpl.addFlow(flowDescriptor);
					}
				}
			}
		}
	}

	private FlowDescriptor setFlowDescriptorInfo(
			Map<String, StateTagDescriptor> statetags,
			FlowDescriptionDTO flowInfo, FlowDescriptor flowDescriptor)
					throws Exception {

		flowDescriptor.setDefault(flowInfo.getIsDefault());
		flowDescriptor.setEntryAction(setEntryExitAction(
				flowInfo.getEntryAction(), true));
		flowDescriptor.setExitAction(setEntryExitAction(
				flowInfo.getExitAction(), true));
		flowDescriptor.setId(flowInfo.getFlowName());
		flowDescriptor
		.setRetrievalStrategy(setRetrievalStrategyforFlow(flowInfo
				.getRetrievalStrategy()));
		flowDescriptor.setSkipEntryExitActionsForAutoStates(flowInfo
				.getSkipEntryExitActionsForAutoStates());
		flowDescriptor.setStmSecurityStrategy(setSecurityStrategyforFlow(flowInfo.getStmSecurityStrategy()));

		Map<String, StateDescriptor> states = flowDescriptor.getStates();
		for (StateDescriptionDTO stateInfo : flowInfo.getStateDescriptions()) {
			StateDescriptor stateDescriptor;
			if (states.containsKey(stateInfo.getStateName())) {
				stateDescriptor = states.get(stateInfo.getStateName());
			} else {
				stateDescriptor = statetags.get(stateInfo.getStateType())
						.getDescriptorClass().getDeclaredConstructor().newInstance();
				stateDescriptor.setId(stateInfo.getStateName());
				stateDescriptor.setFinalState(stateInfo.getIsFinalState());
				stateDescriptor.setFlowId(flowInfo.getFlowName());
				stateDescriptor.setExitAction(setEntryExitAction(
						stateInfo.getExitAction(), true));
				stateDescriptor.setInitialState(stateInfo.getIsInitialState());
				stateDescriptor.setEntryAction(setEntryExitAction(
						stateInfo.getEntryAction(), true));
				setCommonStateProperties(stateDescriptor, stateInfo, statetags);
			}
			setTransitionsInfo(stateDescriptor, stateInfo, statetags, states,
					flowInfo.getStateDescriptions());
			flowDescriptor.addsd(stateDescriptor);
		}
		return flowDescriptor;
	}

	private void setTransitionsInfo(StateDescriptor stateDescriptor,
			StateDescriptionDTO stateInfo,
			Map<String, StateTagDescriptor> statetags,
			Map<String, StateDescriptor> receivedStates,
			List<StateDescriptionDTO> dbSDList) throws STMException {

		for (TransitionDescriptionDTO transitionInfo : stateInfo
				.getTransitionDescriptions()) {
			Transition transition = new Transition();
			transition.setEventId(transitionInfo.getEventName());
			transition.setNewStateId(transitionInfo.getNewStateId());
			transition.setNewFlowId(transitionInfo.getNewFlowId());
			transition.setRetrievalTransition(transitionInfo
					.getIsRetrievalTransition());
			transition.setStateId(stateInfo.getStateName());
			transition
			.setTransitionAction((STMTransitionAction<?>) stmFlowStoreImpl
					.makeAction(transitionInfo.getTransitionAction(),true));
			transition.setAclString(transitionInfo.getAcls());
			transition.setInvokableOnlyFromStm(transitionInfo.getIsInvokableOnlyFromStm());
			processMetaAttributesForTransition(transition, transitionInfo);
			stateDescriptor.addTransition(transition);
		}
	}

	private void setCommonStateProperties(StateDescriptor stateDescriptor,
			StateDescriptionDTO stateInfo,
			Map<String, StateTagDescriptor> statetags) throws STMException{
		boolean isManualState = statetags.get(stateInfo.getStateType())
				.isManualState();
		stateDescriptor.setManualState(isManualState);
		if (stateDescriptor instanceof AutomaticStateDescriptor)
			setActionDescriptorProperties(
					(AutomaticStateDescriptor) stateDescriptor,
					stateInfo.getComponentName(), stateInfo, statetags);
		processMetaAttributesForState(stateDescriptor, stateInfo);
	}

	private void processMetaAttributesForState(StateDescriptor stateDescriptor,
			StateDescriptionDTO stateInfo) {
		Map<String, String> metaDataMap = stateInfo.getMetaData();
		metaDataMap.forEach((key, value) -> {
			stateDescriptor.addMetaData(key, value);
		});
	}

	private void processMetaAttributesForTransition(Transition transition,
			TransitionDescriptionDTO transitionInfo) {
		Map<String, String> metaDataMap = transitionInfo.getMetaData();
		metaDataMap.forEach((key, value) -> {
			transition.addMetaData(key, value);
		});
	}

	private void setActionDescriptorProperties(
			AutomaticStateDescriptor stateDescriptor, String componentName,
			StateDescriptionDTO stateInfo,
			Map<String, StateTagDescriptor> statetags) throws STMException{

		if (componentName == null) {
			componentName = statetags.get(stateInfo.getStateType())
					.getComponentName();
		}
		String enableInlineScripts = "true";
		boolean enable = true;
		if (enableInlineScripts != null)
			enable = Boolean.getBoolean(enableInlineScripts);
		stateDescriptor.setComponentName(componentName);
		stateDescriptor
		.setComponent((STMAutomaticStateComputation<?>) stmFlowStoreImpl
				.makeAction(stateDescriptor.getComponentName(), enable));

		for (StateAttributesDTO element : stateInfo.getStateAttributes()) {
			stateDescriptor.getXmlComponentProperties().put(element.getKey(),
					element.getValue());
			stateDescriptor.addComponentProperty(element.getKey(),
					element.getValue());
		}

	}

	private STMAction<?> setEntryExitAction(String entryExitActionOfFlow,
			Boolean enableInlineScripts) throws STMException{
		STMAction<?> transitionAction = (STMAction<?>) stmFlowStoreImpl
				.makeAction(entryExitActionOfFlow, enableInlineScripts);
		return transitionAction;
	}

	private StateEntityRetrievalStrategy<? extends StateEntity> setRetrievalStrategyforFlow(
			String retrievalStrategyOfFlow) throws STMException{
		@SuppressWarnings("unchecked")
		StateEntityRetrievalStrategy<StateEntity> retrievalStrategy = (StateEntityRetrievalStrategy<StateEntity>) stmFlowStoreImpl
		.makeComponent(retrievalStrategyOfFlow);
		return retrievalStrategy;
	}


	private STMSecurityStrategy setSecurityStrategyforFlow(
			String securityStrategyOfFlow)  throws STMException{
		STMSecurityStrategy securityStrategy = (STMSecurityStrategy) stmFlowStoreImpl
				.makeComponent(securityStrategyOfFlow);
		return securityStrategy;
	}

	public StmDataProvider getStmDataProvider() {
		return stmDataProvider;
	}

	public void setStmDataProvider(StmDataProvider stmDataProvider) {
		this.stmDataProvider = stmDataProvider;
	}

}
