package org.chenile.filewatch.handler;

import org.chenile.base.exception.ErrorNumException;
import org.chenile.base.exception.ServerException;
import org.chenile.core.context.EventLog;
import org.chenile.core.context.EventLog.StatusEnum;
import org.chenile.core.event.EventLogger;
import org.chenile.filewatch.errorcodes.ErrorCodes;
import org.springframework.beans.factory.annotation.Autowired;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Uses the {@link EventLogger} to log events
 * @author Raja Shankar Kolluru
 *
 */
public class FileWatchEventLogger {
	@Autowired private EventLogger eventLogger;
	private ObjectMapper om = new ObjectMapper();
	
	public void logError(int subErrorNum,String message,ErrorNumException... e) {
		logEvent(StatusEnum.FAIL,subErrorNum,message,e);
	}
	
	public void logWarning(int subErrorNum,String message,ErrorNumException... e) {
		logEvent(StatusEnum.WARNING,subErrorNum,message,e);
	}
	
	public void logError(int subErrorNum, String message, Throwable... t) {
		ServerException e = null;
		if ( t != null && t.length > 0)
			e = new ServerException(subErrorNum,message,t[0]);
		logError(subErrorNum,message, e);
	}
	
	public void logSuccess(Object o) {
		try {
			String m = null;
			if (!(m instanceof String))
				m = om.writeValueAsString(o);
			logEvent(StatusEnum.SUCCESS,0,m);
		}catch(Exception e) {
			logError(ErrorCodes.CANNOT_SERIALIZE_RESPONSE_TO_JSON.getSubError(),
			"Cannot serialize the response " + o + " to JSON");
		}		
	}
	
	private void logEvent(StatusEnum status,int subErrorNum,String message,ErrorNumException... e) {
		EventLog eventLog = new EventLog();
		eventLog.setApp("chenile-filewatch");
		eventLog.setStatus(status);
		eventLog.setEventId("chenile-filewatch");
		eventLog.setMessage(message);
		eventLog.setSubErrorNum(subErrorNum);
		if (e != null && e.length > 0) {
			eventLog.setException(e[0]);
		}
		eventLogger.log(eventLog);
	}
}
