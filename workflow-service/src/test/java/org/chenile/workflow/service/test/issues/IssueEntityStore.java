package org.chenile.workflow.service.test.issues;

import java.util.HashMap;
import java.util.Map;

import org.chenile.utils.entity.service.EntityStore;

public class IssueEntityStore implements EntityStore<Issue>{
	private Map<String, Issue> theStore = new HashMap<>();
	public static int counter = 1;
	@Override
	public void store(Issue entity) {
		if (entity.getId() == null) {
			entity.setId(counter++ + "");
		}
		theStore.put(entity.getId(), entity);		
	}

	@Override
	public Issue retrieve(String id) {
		return theStore.get(id);
	}

}
