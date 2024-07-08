package org.chenile.core.interceptors;

import org.chenile.base.response.GenericResponse;
import org.chenile.core.context.ChenileExchange;
import org.chenile.core.context.HeaderUtils;
import org.chenile.core.context.LogRecord;
import org.chenile.core.model.LogWriter;

/**
 * Logs the output from the response object. This is required if the service is asynchronous i.e. its
 * response is not emitted back but needs to be used to keep track of success and failure by
 * logging the response into some medium (such as files, queues, topics etc.) <br/>
 * The original entry point (that constructed the ChenileExchange) can give us a callback to invoke
 * or can ask us to log into a file. That can then be used to transmit the status to the original
 * caller or displayed in a UI.<br/>
 */
public class LogOutput extends BaseChenileInterceptor {

	@Override
	protected void doPostProcessing(ChenileExchange chenileExchange) {
		LogRecord record = makeLogRecord(chenileExchange);
		// execute registered loggers
		LogWriter logWriter = chenileExchange.getLogWriter();
		logWriter.write(record);
	}

	private LogRecord makeLogRecord(ChenileExchange exchange) {
		LogRecord record = new LogRecord();
		try {
			GenericResponse<?> resp = (GenericResponse<?>) exchange.getResponse();
			record.success = resp.isSuccess();
			record.response = resp;
			record.responseMessages = resp.getErrors();
		}catch(Exception e){
			e.printStackTrace();
		}

		record.serviceName = exchange.getServiceDefinition().getName();
		record.moduleName = exchange.getServiceDefinition().getModuleName();
		record.operationName = exchange.getOperationDefinition().getName();

		record.request = exchange.getBody();
		record.originalSource = exchange.getHeader(HeaderUtils.ENTRY_POINT,String.class);
		record.originalSourceReference = exchange.getOriginalSourceReference();
		record.exception = exchange.getException();
		copyParamHeaders(record,exchange);
		return record;
	}

	private void copyParamHeaders(LogRecord record, ChenileExchange exchange) {
		exchange.getOperationDefinition().getParams().forEach(
				(pd) -> {
					Object obj = exchange.getHeader(pd.getName());
					if (obj != null){
						record.headers.put(pd.getName(),obj);
					}
				}
		);
	}

	@Override
	protected boolean bypassInterception(ChenileExchange exchange) {
        return exchange.getLogWriter() == null;
    }
}