package org.chenile.workflow.dto;

import java.util.List;
import java.util.Map;

import org.chenile.workflow.model.AbstractStateEntity;

public class StateEntityServiceResponse<T extends AbstractStateEntity> {
	protected T mutatedEntity;
	private int slaGettingLateInHours;
	private int slaLateInHours;
	private List<Map<String, String>> allowedActionsAndMetadata;

	/**
	 * @return the mutatedEntity
	 */
	public T getMutatedEntity() {
		return mutatedEntity;
	}

	/**
	 * @param mutatedEntity the mutatedEntity to set
	 */
	public void setMutatedEntity(T mutatedEntity) {
		this.mutatedEntity = mutatedEntity;
	}

	public int getSlaGettingLateInHours() {
		return slaGettingLateInHours;
	}

	public void setSlaGettingLateInHours(int slaGettingLateInHours) {
		this.slaGettingLateInHours = slaGettingLateInHours;
	}

	public int getSlaLateInHours() {
		return slaLateInHours;
	}

	public void setSlaLateInHours(int slaLateInHours) {
		this.slaLateInHours = slaLateInHours;
	}

	public List<Map<String, String>> getAllowedActionsAndMetadata() {
		return allowedActionsAndMetadata;
	}

	public void setAllowedActionsAndMetadata(List<Map<String, String>> allowedActionsAndMetadata) {
		this.allowedActionsAndMetadata = allowedActionsAndMetadata;
	}

}
