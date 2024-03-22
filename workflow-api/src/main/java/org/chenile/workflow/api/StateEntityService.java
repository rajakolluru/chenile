package org.chenile.workflow.api;

import java.util.List;
import java.util.Map;

import org.chenile.stm.State;
import org.chenile.workflow.dto.StateEntityServiceResponse;
import org.chenile.workflow.model.AbstractStateEntity;

public interface StateEntityService<T extends AbstractStateEntity> {
	/**
	 * 
	 * @param enetity the entity on which the event has happened
	 * @param event - the name of the event that happened on the entity
	 * @param payload - Additional parameters for the event (event specific)
	 * @return a StateEntityServiceResponse with the mutated entity
	 */
	public StateEntityServiceResponse<T> process(T entity, String event, Object payload);
	
	/**
	 * 
	 * @param id - the ID of the event. This is useful if the entire entity is not passed by the front end
	 * @param event - Name of the event that has happened on the entity
	 * @param payload - Additional parameters for the event (event specific)
	 * @return a StateEntityServiceResponse with the mutated entity
	 */
	public StateEntityServiceResponse<T> processById(String id, String event, Object payload);
	
	public StateEntityServiceResponse<T> create(T entity);

	public StateEntityServiceResponse<T> retrieve(String id);
	
	public List<Map<String, String>> getAllowedActionsAndMetadata(State state);
	
	public List<Map<String, String>> getAllowedActionsAndMetadata(String id);
	

}
