package org.chenile.core.model;

/**
 * Bind a parameter of an Operation to a message.
 * <li>BODY - binds the payload of the message to the parameter</li>
 * <li>HEADER - binds a specified header to the parameter</li>
 * <li>HEADERS - binds the parameter to all the headers that have been received in the message.
 * This allows the service to receive all the headers as a parameter.</li>
 * <li>MULTI-PART - If there are multi part payloads for this message, this allows the parameter
 * to be computed from a named part.</li>
 */
public  enum HttpBindingType {
	BODY, HEADER, HEADERS, MULTI_PART;
}