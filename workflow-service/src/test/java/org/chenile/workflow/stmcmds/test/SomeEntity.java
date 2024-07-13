package org.chenile.workflow.stmcmds.test;

import org.chenile.utils.entity.model.AbstractExtendedStateEntity;

import java.io.Serial;
import java.util.ArrayList;
import java.util.List;


public class SomeEntity extends AbstractExtendedStateEntity {
	@Serial
	private static final long serialVersionUID = 3341478906527940984L;
	private List<String> listOfStrings = new ArrayList<String>();
	private String tenantId;

	public String getTenantId() {
		return tenantId;
	}

	public void setTenantId(String tenantId) {
		this.tenantId = tenantId;
	}

	public List<String> getListOfStrings() {
		return listOfStrings;
	}

	public void setListOfStrings(List<String> listOfStrings) {
		this.listOfStrings = listOfStrings;
	}
	
	
}
