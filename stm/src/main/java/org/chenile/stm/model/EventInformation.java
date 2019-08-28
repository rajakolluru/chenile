package org.chenile.stm.model;

import java.util.Collections;

import java.util.HashMap;
import java.util.Map;

import org.chenile.stm.action.STMTransitionAction;

/**
 * An abstraction to extract actions and their meta data and share them across transitions.
 * An event and a transition are two different things. 
 * Different transitions can potentially generate the same event.
 * This captures all the event information in one place. In this way, the same event information
 * does not have to be repeated for every transition.
 * @author Raja Shankar Kolluru
 *
 */
public class EventInformation {

	protected String eventId;
	protected STMTransitionAction<?> transitionAction;

	public String getEventId() {
		return eventId;
	}

	public void setEventId(String eventId) {
		this.eventId = eventId;
	}

	public STMTransitionAction<?> getTransitionAction() {
		return transitionAction;
	}

	public void setTransitionAction(STMTransitionAction<?> transitionAction) {
		this.transitionAction = transitionAction;
	}

	public void addMetaData(String name, String value) {
		metadata.put(name, value);
	}

	public Map<String, String> getMetadata() {
		return Collections.unmodifiableMap(metadata);
	}

	protected Map<String,String> metadata = new HashMap<String, String>();

	public EventInformation() {
		super();
	}

}