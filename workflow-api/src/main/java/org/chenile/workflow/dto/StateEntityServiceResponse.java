package org.chenile.workflow.dto;

import java.util.List;
import java.util.Map;

public class StateEntityServiceResponse<T> {
	protected T mutatedEntity;
	private List<Map<String, String>> allowedActionsAndMetadata;
	public T getMutatedEntity() {
		return mutatedEntity;
	}
	public void setMutatedEntity(T mutatedEntity) {
		this.mutatedEntity = mutatedEntity;
	}
	public List<Map<String, String>> getAllowedActionsAndMetadata() {
		return allowedActionsAndMetadata;
	}
	public void setAllowedActionsAndMetadata(List<Map<String, String>> allowedActionsAndMetadata) {
		this.allowedActionsAndMetadata = allowedActionsAndMetadata;
	}
}
