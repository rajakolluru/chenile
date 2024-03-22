package org.chenile.query.service.commands.model;

import java.util.List;
import java.util.Map;

import org.chenile.base.response.ResponseMessage;
import org.chenile.base.response.WarningAware;
import org.chenile.utils.tenant.commands.HeadersAwareContext;

public class SearchContext implements HeadersAwareContext, WarningAware{
	public Map<String,Object> headers;

	@Override
	public Map<String,Object> getHeaders() {
		return headers;
	}
	
	public Map<String,Object> req;
	public Map<String,Object> resp;

	@Override
	public List<ResponseMessage> getWarningMessages() {
		return WarningAware.obtainWarnings(this.resp);
	}
	@Override
	public void addWarningMessage(ResponseMessage m) {
		WarningAware.addWarningMessage(this.resp, m);		
	}
	@Override
	public void removeAllWarnings() {
		WarningAware.removeAllWarnings(this.resp);
	}

}