package org.chenile.query.service.impl;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.chenile.base.exception.ErrorNumException;
import org.chenile.base.response.ResponseMessage;
import org.chenile.base.response.WarningAware;
import org.chenile.owiz.OrchExecutor;
import org.chenile.query.service.EntityQueryService;
import org.chenile.query.service.commands.QueryChain;
import org.chenile.query.service.commands.model.SearchContext;

public class EntityQueryServiceImpl implements EntityQueryService {

	private OrchExecutor<SearchContext> queryExecutor;
	public EntityQueryServiceImpl(OrchExecutor<SearchContext> orchExecutor) {
		this.queryExecutor = orchExecutor;
	}
	@Override
	public Map<String,Object> query(Map<String,Object> headers,Map<String,Object> request) {
		SearchContext context = new SearchContext();
		context.headers = headers;
		Map<String,Object> response = new ConcurrentHashMap<>();
		context.req = request;
		context.resp = response;
		try {
			queryExecutor.execute(context);
		} catch (Exception e) {			
			processError(context,e);
		}		
		return response;
	}
	
	protected void processError(SearchContext context, Exception e) {
		if (e instanceof ErrorNumException) {
			ErrorNumException ene = (ErrorNumException)e;
			// this came from the root of the tree which will not be the actual 
			// exception thrown. Look for something deeper
			List<ResponseMessage> warnings = WarningAware.obtainWarnings(context);
			// if the root is merely propagating the exception that occurred below, ignore it
			// else this is an exception that happened at the root itself and needs to be thrown
			if (ene.getSubErrorNum() != QueryChain.PROPAGATING_EXCEPTION_ERRORCODE) {
				throw ene;
			}
			else if (warnings.size() > 0) {
				ErrorNumException e1 = new ErrorNumException(warnings.get(0));
				e1.setErrors(warnings);
				throw e1;
			}else {
				throw ene;
			}
		}
		else {
			throw new ErrorNumException(500, "Error occurred in computing the entity", e);
		}
	}

}
