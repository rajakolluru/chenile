package org.chenile.http.handler;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.chenile.base.exception.ErrorNumException;
import org.chenile.base.exception.ServerException;
import org.chenile.base.response.ErrorType;
import org.chenile.base.response.GenericResponse;
import org.chenile.core.context.ChenileExchange;
import org.chenile.core.entrypoint.ChenileEntryPoint;
import org.chenile.core.errorcodes.ErrorCodes;
import org.chenile.core.model.ChenileServiceDefinition;
import org.chenile.core.model.OperationDefinition;
import org.springframework.http.HttpStatus;
import org.springframework.web.HttpRequestHandler;
import org.springframework.web.servlet.HandlerMapping;

import com.fasterxml.jackson.databind.ObjectMapper;

public class HttpEntryPoint implements HttpRequestHandler {

	private ChenileServiceDefinition serviceDefinition;
	private OperationDefinition operationDefinition;
	private ChenileEntryPoint chenileEntryPoint;

	public HttpEntryPoint(ChenileServiceDefinition serviceDefinition, OperationDefinition operationDefinition, ChenileEntryPoint chenileEntryPoint) {
		this.serviceDefinition = serviceDefinition;
		this.operationDefinition = operationDefinition;
		this.chenileEntryPoint = chenileEntryPoint;
	}

	@Override
	public void handleRequest(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse)
			throws ServletException, IOException {
		try {
			ChenileExchange exchange = new ChenileExchange();
			exchange.setServiceDefinition(serviceDefinition);
			exchange.setOperationDefinition(operationDefinition);
			exchange.setHeaders(getHeaders(httpServletRequest));
			setBody(httpServletRequest, exchange);

			chenileEntryPoint.execute(exchange);
			
			httpServletResponse.setStatus(getSuccessHttpStatus(httpServletRequest.getMethod().toUpperCase()).value());
			httpServletResponse.setContentType(operationDefinition.getProduces().toString());
			switch(operationDefinition.getProduces()) {
				case JSON :
				case HTML:
				case TEXT: 
					processResponse(exchange,httpServletRequest,httpServletResponse);
					break;
				case PDF:
					processResponsePdf(exchange,httpServletRequest,httpServletResponse);
					break;
			}
			httpServletResponse.flushBuffer();
			
		} catch (Throwable exception) {
			exception.printStackTrace();
			ErrorNumException errorNumException;
			GenericResponse<Object> genericResponse;

			if (exception instanceof ErrorNumException) {
				errorNumException = (ErrorNumException) exception;
				genericResponse = new GenericResponse<Object>(errorNumException, ErrorType.WARNING);
			} else {
				errorNumException = new ErrorNumException(500, exception.getMessage());
				genericResponse = new GenericResponse<Object>(errorNumException, ErrorType.ERROR);
			}
			httpServletResponse.setStatus(errorNumException.getErrorNum());
			httpServletResponse.getWriter().write(new ObjectMapper().writeValueAsString(genericResponse));
			httpServletResponse.flushBuffer();
		}
	}

	private void processResponsePdf(ChenileExchange exchange, HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse) throws Exception {
		OperationDefinition od = exchange.getOperationDefinition();
		if (!(exchange.getResponse() instanceof File)) {
			throw new ServerException(ErrorCodes.MISCONFIGURATION.getSubError(), 
					od.getName() + " MisConfiguration: Produces PDF must return a response of type File");
		}
		File file = (File)exchange.getResponse();
		// display the pdf in the browser itself. They can always save it if they want
		httpServletResponse.setHeader("Content-disposition","inline; filename='"  + file.getName() + " '");
		httpServletResponse.setContentLength((int) file.length());

		FileInputStream fileInputStream = null;		
		try {
			fileInputStream = new FileInputStream(file);
	
			OutputStream responseOutputStream = httpServletResponse.getOutputStream();
			int bytes;
			while ((bytes = fileInputStream.read()) != -1) {
				responseOutputStream.write(bytes);
			}
		}finally {
			if (fileInputStream != null)
				fileInputStream.close();
		}
	}

	private void processResponse(ChenileExchange exchange,  HttpServletRequest httpServletRequest, 
			HttpServletResponse httpServletResponse) throws Exception {
		Object response = exchange.getResponse();
		GenericResponse<Object> genericResponse = new GenericResponse<Object>(response);
		httpServletResponse.getWriter().write(new ObjectMapper().writeValueAsString(genericResponse));	
	}
	
	public HttpStatus getSuccessHttpStatus(String method) {
		switch (method) {
		case "POST":
			return HttpStatus.CREATED;
		default:
			return HttpStatus.OK;
		}

	}

	private void setBody(HttpServletRequest httpServletRequest, ChenileExchange exchange)
			throws IOException {
		String body = httpServletRequest.getReader().lines().collect(Collectors.joining(System.lineSeparator()));
		if (body == null) {
			
		}
		exchange.setBody(body);
	}

	@SuppressWarnings("unchecked")
	private Map<String, Object> getHeaders(HttpServletRequest httpServletRequest) {
		Map<String, Object> headers = new HashMap<>();
		Map<String,Object> pathParams = (Map<String, Object>) httpServletRequest.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
		if (null != pathParams) {
			headers.putAll(pathParams);
		}
		headers.putAll(Collections.list(httpServletRequest.getParameterNames()).stream()
				.collect(Collectors.toMap(parameterName -> parameterName, httpServletRequest::getParameterValues)));
		Enumeration<String> headerNames = httpServletRequest.getHeaderNames();

		if (headerNames != null) {
			while (headerNames.hasMoreElements()) {
				String headerName = headerNames.nextElement();
				headers.put(headerName, httpServletRequest.getHeader(headerName));
			}
		}
		return headers;
	}
}