package org.chenile.core.model;

import org.chenile.core.context.LogRecord;

/**
 * Implement this interface if you are able to persist a log record. This is done when a request
 * is asynchronous i.e. the response is merely logged instead of being returned to the caller as is
 * the case with a synchronous request. <br/>
 * Records can be logged using multiple mechanisms such as file, event topics, MQ etc.
 */
public interface LogWriter {
    public void write(LogRecord record);
}
