package org.chenile.stm.ognl;

import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import ognl.OgnlContext;
import ognl.OgnlException;
import ognl.PropertyAccessor;

/**
 * 
 * @author raja
 * Exposes various attributes of the HttpServletRequest.
 * If it has direct request properties such as requestURI,pathTranslated  etc. then they are returned directly.
 * If the property ends with "Cookie" it is assumed that the property is expected in a cookie. So cookies are examined
 * to obtain the property value.
 * if the property name ends with Header it is assumed to be a header property and hence the property is queried as such.
 * For all other properties first they are examined to see if they have been set as parameters. If not they would be assumed to be 
 * request attributes.
 */
public class HttpServletRequestAccessor implements PropertyAccessor {

	public Object getProperty(Map context, Object target, Object name)
			throws OgnlException {
		String s = (String) name;
		HttpServletRequest request = (HttpServletRequest)target;
		
		if (name.equals("requestURI"))  return request.getRequestURI();
		if (s.endsWith("Cookie")) return getCookieProperty(request,s);
		if (s.endsWith("Header")) return getHeaderProperty(request,s);
		String ret = request.getParameter((String)name);
		if (ret !=  null) return ret;
		return request.getAttribute((String)name);
	}

	public void setProperty(Map context, Object target, Object name, Object value)
			throws OgnlException {
		HttpServletRequest request = (HttpServletRequest)target;
		request.setAttribute((String)name, value);
	}
	
	protected Object getCookieProperty(HttpServletRequest request, String name){
		String cookieName = name.substring(0,name.lastIndexOf("C"));
		for (Cookie cookie : request.getCookies()){
			if (cookieName.equals(cookie.getName()))
				return cookie.getValue();
		}

		return null;
	}
	
	protected Object getHeaderProperty(HttpServletRequest request, String name){
		String headerName = name.substring(0,name.lastIndexOf("H"));
		return request.getHeader(headerName);
	}

	public String getSourceAccessor(OgnlContext context, Object target,
			Object index) {
		return null;
	}

	public String getSourceSetter(OgnlContext context, Object target,
			Object index) {
		return null;
	}



}
