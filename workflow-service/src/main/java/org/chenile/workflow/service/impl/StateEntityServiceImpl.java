package org.chenile.workflow.service.impl;

import java.util.List;
import java.util.Map;

import org.chenile.base.exception.ErrorNumException;
import org.chenile.base.exception.NotFoundException;
import org.chenile.stm.STM;
import org.chenile.stm.State;
import org.chenile.stm.exception.STMException;
import org.chenile.stm.impl.STMActionsInfoProvider;
import org.chenile.utils.entity.service.EntityStore;
import org.chenile.workflow.api.StateEntityService;
import org.chenile.workflow.dto.StateEntityServiceResponse;
import org.chenile.workflow.model.AbstractStateEntity;


public class StateEntityServiceImpl<T extends AbstractStateEntity> implements StateEntityService<T> {
	
	private STM<T> stm;
	protected EntityStore<T> entityStore;
	private STMActionsInfoProvider stmActionsInfoProvider;
	
	public StateEntityServiceImpl(STM<T> stm,STMActionsInfoProvider stmActionsInfoProvider,EntityStore<T> entityStore) {
		this.stm = stm;
		this.stmActionsInfoProvider = stmActionsInfoProvider;
		this.entityStore = entityStore;
	}
	
	protected T processEntity(T entity, String event, Object payload)  {
		try {
			if (event == null)
				return stm.proceed(entity);
			else
				return stm.proceed(entity, event, payload);
		} catch (Exception e) {
			if (e instanceof STMException && 
					(((STMException)e).getMessageId() == STMException.INVALID_EVENTID ||
					((STMException)e).getMessageId() == STMException.INVALID_TRANSITION)) {
				throw new ErrorNumException(422, 6001, new Object[] {event,entity.getCurrentState().getStateId()});
			}else {
				throw new ErrorNumException(500,6002,new Object[] {e.getMessage(), event},e);
			}
		}
	}
	
	@Override
	public StateEntityServiceResponse<T> create(T entity) {
		T ret = processEntity(entity,null,null);
		return makeStateEntityResponse(ret);
	}

	@Override
	public StateEntityServiceResponse<T> process(T entity, String event, Object payload) {
		T ret = processEntity(entity, event,payload);
		return makeStateEntityResponse(ret);
	}

	@Override
	public StateEntityServiceResponse<T> processById(String id,  String event, Object payload) {
		T entity = entityStore.retrieve(id);
		if (entity == null) {
			throw new NotFoundException(6003, new Object[] {id});
		}
		T ret = processEntity(entity, event,payload);
		return makeStateEntityResponse(ret);
	}
	
	
//	@Override
	public List<Map<String, String>> getAllowedActionsAndMetadata(State state) {
		return stmActionsInfoProvider.getAllowedActionsAndMetadata(state);
	}
	
	@Override
	public List<Map<String, String>> getAllowedActionsAndMetadata(String id) {
		T entity = (T) entityStore.retrieve(id);
		if (entity == null) {
			throw new NotFoundException(6003, new Object[] {id});
		}
		return getAllowedActionsAndMetadata(entity.getCurrentState());
	}
	
	protected StateEntityServiceResponse<T> makeStateEntityResponse(T entity){
		StateEntityServiceResponse<T> sesr = new StateEntityServiceResponse<T>();
		sesr.setMutatedEntity(entity);
		State state = entity.getCurrentState();
		sesr.setAllowedActionsAndMetadata(getAllowedActionsAndMetadata(state));
		
		sesr.setSlaGettingLateInHours(StateEntityHelper.getGettingLateTimeInHours(stmActionsInfoProvider,state));
		sesr.setSlaLateInHours(StateEntityHelper.getLateTimeInHours(stmActionsInfoProvider,state));
		return sesr;
	}
	
	@Override
	public StateEntityServiceResponse<T> retrieve(String id) {
		T entity = (T) entityStore.retrieve(id);
		if (entity == null) {
			throw new NotFoundException(6003, new Object[] {id});
		}
		return makeStateEntityResponse(entity);
	}

	
}
