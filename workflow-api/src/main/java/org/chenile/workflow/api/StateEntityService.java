package org.chenile.workflow.api;

import java.util.List;
import java.util.Map;

import org.chenile.stm.State;
import org.chenile.workflow.dto.StateEntityServiceResponse;
import org.chenile.workflow.model.AbstractStateEntity;

/**
 * All workflow entities (aka State entities) are managed using this interface.<br/>
 * Workflow entities contain state. State marches forward in accordance with a State Transition Diagram (STD)<br/>
 * The STD is read by a State Transition Machine. <br/>
 * All State entities are either created or processed in accordance with certain rules defined in the
 * State Transition Diagram. Read more about state machines <a href="https://chenile.org/chenile-stm.html">here.</a>
 * @param <T> the actual state entity
 */
public interface StateEntityService<T extends AbstractStateEntity> {
	/**
	 * 
	 * @param entity the entity on which the event has happened
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

	/**
	 *
	 * @param entity the state entity that needs to be created.
	 * @return the state entity along with the allowed actions on the state entity. The state entity that
	 * is returned,  will have the initial state initialized (along with other obvious attributes such
	 * as ID, created by, tenancy (if applicable) etc.
	 */
	public StateEntityServiceResponse<T> create(T entity);

	/**
	 *
	 * @param id The ID of the state entity
	 * @return the state entity along with allowed actions
	 */
	public StateEntityServiceResponse<T> retrieve(String id);
	
	public List<Map<String, String>> getAllowedActionsAndMetadata(State state);
	
	public List<Map<String, String>> getAllowedActionsAndMetadata(String id);
	

}
