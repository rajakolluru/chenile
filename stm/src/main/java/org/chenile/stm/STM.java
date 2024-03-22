package org.chenile.stm;


/**
 * 
 * An implementation of a state transition machine. This would allow the caller to proceed from one state to the
 * other.  The implementation is designed to support two types of states:
 * Auto state  - a state whose transitions can be computed automatically by calling a method in a class.
 * Manual state - a state that requires some triggering event (ex: user input in a form, user responding to an email etc.) to
 * progress further. 
 * <p>The STM also supports the notion of an initial state  
 * <p>
 *  @author Raja Shankar Kolluru
 */
public interface STM<StateEntityType extends StateEntity> {

	public static final String SUCCESS = "__SUCCESS__";
	public static final String FAILURE = "__FAILURE__";
	
	/**
	 * This method allows the user to specify no starting state and feed it to the STM. This would start with the default flow and the 
	 * initial state in it. It would then recursively process thru all
	 * "auto states" and finally returns the new state to which it transitioned. 
	 * <p>The new state is either an end state or a manual state (i.e. a state that requires a user triggered action to determine the transitioning event)</p>
	 * <p>This proceed() method supports the following kinds of behavior:</p>
	 * If the state passed is null, then the initial state of the default flow is chosen. 
	 * @param stateEntity - the parameter required by the transition action
	 * @return  the transitioned state. 
	 * @throws Exception
	 */
	public abstract StateEntityType proceed(StateEntityType stateEntity)  throws Exception;
	
	/**
	 * This method allows the user to specify a starting state and feed it to the STM. This would recursively process thru all
	 * "auto states" and finally returns the new state to which it transitioned. 
	 * <p>The new state is either an end state or a manual state (i.e. a state that requires a user triggered action to determine the transitioning event)
	 * <p>This proceed() method supports the following kinds of behavior:
	 * If the state passed is null, then the initial state of the default flow is chosen.
	 *  
	 * @param stateEntity - the entity with a state that needs to be processed by the STM
	 * @param actionParam - the parameter required by the transition action
	 * @return  the transitioned state. 
	 * @throws Exception
	 */
	public abstract StateEntityType proceed(StateEntityType stateEntity, Object actionParam)  throws Exception;
	/**
	 * This method allows the user to specify a starting state and feed it to the STM. This would recursively process thru all
	 * "action states" and finally returns the new state to which it transitioned. 
	 * <p>The new state is either an end state or a view state (i.e. a state that requires a user triggered action to determine the transitioning event)
	 * <p>This proceed() method uses the specified state in the specified flow.
	 * @param stateEntityType - the entity with a state that needs to be processed by the STM
	 * @param startingEventId - the event that needs to start the flow. This would have been most probably obtained by the user.
	 * @param actionParam - the parameter required by the transition action
	 * @return  the transitioned state. 
	 * @throws Exception
	 */
	
	public abstract StateEntityType proceed(StateEntityType stateEntityType, String startingEventId, 
			Object actionParam) throws Exception;
	
	/**
	 * Initialization method. The flow needs to be read by a flow configurator which needs to be set into the STM.
	 * @param stmFlowStore
	 */
	public abstract void setStmFlowStore(STMFlowStore stmFlowStore);
}