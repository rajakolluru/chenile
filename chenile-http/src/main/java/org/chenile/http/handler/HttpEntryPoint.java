package org.chenile.http.handler;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import jakarta.validation.constraints.NotNull;
import org.chenile.base.exception.ServerException;
import org.chenile.base.response.ResponseMessage;
import org.chenile.base.response.WarningAware;
import org.chenile.core.context.ChenileExchange;
import org.chenile.core.context.HeaderUtils;
import org.chenile.core.entrypoint.ChenileEntryPoint;
import org.chenile.core.errorcodes.ErrorCodes;
import org.chenile.core.model.ChenileServiceDefinition;
import org.chenile.core.model.OperationDefinition;
import org.chenile.http.Constants;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;
import org.springframework.lang.NonNullApi;
import org.springframework.web.HttpRequestHandler;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.i18n.AcceptHeaderLocaleResolver;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class HttpEntryPoint implements HttpRequestHandler {

	private ChenileServiceDefinition serviceDefinition;
	private OperationDefinition operationDefinition;
	private ChenileEntryPoint chenileEntryPoint;
	private LocaleResolver localeResolver = new AcceptHeaderLocaleResolver();

	public HttpEntryPoint(ChenileServiceDefinition serviceDefinition, OperationDefinition operationDefinition, ChenileEntryPoint chenileEntryPoint) {
		this.serviceDefinition = serviceDefinition;
		this.operationDefinition = operationDefinition;
		this.chenileEntryPoint = chenileEntryPoint;
	}
	
	@Override
	public void handleRequest(@NonNull HttpServletRequest httpServletRequest,
							  @NonNull HttpServletResponse httpServletResponse)
			throws ServletException, IOException {
		ChenileExchange exchange = new ChenileExchange();
		exchange.setServiceDefinition(serviceDefinition);
		exchange.setHeader(HeaderUtils.ENTRY_POINT, Constants.HTTP_ENTRY_POINT);
		exchange.setOperationDefinition(operationDefinition);
		exchange.setHeaders(getHeaders(operationDefinition,httpServletRequest));
		exchange.setMultiPartMap(getMultiPartMap(httpServletRequest));
		exchange.setLocale(localeResolver.resolveLocale(httpServletRequest));
		setBody(httpServletRequest, exchange);
		
		try {
			chenileEntryPoint.execute(exchange);
			if (exchange.getException() == null) {
				processSuccess(exchange,httpServletRequest,httpServletResponse);
			}else {
				processFailure(exchange,httpServletRequest,httpServletResponse);
			}		
		} catch (Throwable exception) {
			exchange.setException(exception);
			processFailure(exchange,httpServletRequest,httpServletResponse);
		}		
	}
	
	
	public static Map<String,MultipartFile> getMultiPartMap(HttpServletRequest httpServletRequest) {
		if (!(httpServletRequest instanceof MultipartHttpServletRequest))
			return null;
		return ((MultipartHttpServletRequest)httpServletRequest).getFileMap();
	}

	private void processSuccess(ChenileExchange exchange, HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse) throws Exception{
		int status = exchange.getHttpResponseStatusCode();
		httpServletResponse.setStatus(status);
		httpServletResponse.setContentType(operationDefinition.getProduces().toString());
		switch(operationDefinition.getProduces()) {
			case JSON :
			case HTML:
			case TEXT: 
				processResponse(exchange,httpServletRequest,httpServletResponse);
				break;
			case PDF:
				processResponsePdf(exchange,httpServletRequest,httpServletResponse,status);
				break;
		}
		httpServletResponse.flushBuffer();		
	}

	private void processFailure(ChenileExchange exchange, HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse) throws ServletException,IOException {
		httpServletResponse.setContentType(operationDefinition.getProduces().toString());
		httpServletResponse.setStatus(exchange.getHttpResponseStatusCode());
		httpServletResponse.getWriter().write(om.writeValueAsString(exchange.getResponse()));
		httpServletResponse.flushBuffer();
	}
	

	private void processResponsePdf(ChenileExchange exchange, HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse, int httpStatusCode) throws ServletException,IOException {
		OperationDefinition od = exchange.getOperationDefinition();
		if (!(exchange.getResponse() instanceof File file)) {
			throw new ServerException(ErrorCodes.PDF_MISCONFIGURATION.getSubError(),
					new Object[]{od.getName()});
		}
        // display the pdf in the browser itself. They can always save it if they want
		httpServletResponse.setHeader("Content-disposition","inline; filename='"  + file.getName() + " '");
		httpServletResponse.setContentLength((int) file.length());

        try (FileInputStream fileInputStream = new FileInputStream(file)) {

            OutputStream responseOutputStream = httpServletResponse.getOutputStream();
            int bytes;
            while ((bytes = fileInputStream.read()) != -1) {
                responseOutputStream.write(bytes);
            }
        }
	}
	
	private static final ObjectMapper om ;
	static {
		// Ensure that object mapper does not copy out nulls and does not fail if the 
		// bean has no properties i.e. empty beans
		om = new ObjectMapper();
		om.setSerializationInclusion(Include.NON_EMPTY);
		om.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
		om.enable(SerializationFeature.INDENT_OUTPUT);
	}

	private void processResponse(ChenileExchange exchange,  HttpServletRequest httpServletRequest, 
			HttpServletResponse httpServletResponse) throws Exception {
		Object response = exchange.getResponse();
		processWarningsIfAny(exchange,httpServletResponse);		
		httpServletResponse.getWriter().write(om.writeValueAsString(response));	
	}
	
	private void processWarningsIfAny(ChenileExchange exchange, HttpServletResponse httpServletResponse) {
		List<ResponseMessage>x =  exchange.getResponseMessages();
		if (x == null) return;
		
		for (ResponseMessage message: x) {
			httpServletResponse.setHeader(HttpHeaders.WARNING,message.getDescription());
		}
	}

	public HttpStatus getSuccessHttpStatus(Object response) {
		List<ResponseMessage> x = WarningAware.obtainWarnings(response);
		if (x == null || x.isEmpty())
			return HttpStatus.valueOf(operationDefinition.getSuccessHttpStatus());
		else 
			return HttpStatus.valueOf(operationDefinition.getWarningHttpStatus());
	}

	private void setBody(HttpServletRequest httpServletRequest, ChenileExchange exchange)
			throws IOException {
		String body = httpServletRequest.getReader().lines().collect(Collectors.joining(System.lineSeparator()));
        exchange.setBody(body);
	}
	
	// @SuppressWarnings("unchecked")
	public static Map<String, Object> getHeaders(OperationDefinition od, 
			HttpServletRequest httpServletRequest) {
		Map<String, Object> headers = new HashMap<>();
		String pathInfo = httpServletRequest.getRequestURI();
		headers.putAll(extractPathVariables(od.getUrl(),pathInfo));

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

	public static Map<String, Object> extractPathVariables(String url, String pathInfo) {
		Map<String,Object> pathVars = new HashMap<>();
		while (url.contains("/{")) {
			int startIndex = url.indexOf("/{");
			int endIndex = url.indexOf("}");
			String pathVarName = url.substring(startIndex+2,endIndex);
			url = url.substring(endIndex+1);
			// In the pathInfo the position will be startIndex +1 since we don't 
			// expect to see { there.
			startIndex = startIndex + 1;
			endIndex = pathInfo.indexOf("/", startIndex);
			if (endIndex == -1) endIndex = pathInfo.length();
			String pathVarValue = pathInfo.substring(startIndex, endIndex);
			pathInfo = pathInfo.substring(endIndex);
			pathVars.put(pathVarName, pathVarValue);
		}
		return pathVars;
	}
}