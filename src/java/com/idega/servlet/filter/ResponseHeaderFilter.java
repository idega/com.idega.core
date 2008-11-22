package com.idega.servlet.filter;

import java.io.IOException;
import java.util.Enumeration;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;

/**
 * A filter that adds any header you define in web.xml to any URI the filter is registered to.
 * Example settings:
 * 	<filter>
 *	  <filter-name>ResponseHeaderFilter</filter-name>
 *	  <filter-class>com.idega.servlet.filter.ResponseHeaderFilter</filter-class>
 *	  <init-param>
 *	    <param-name>Cache-Control</param-name>
 *	    <param-value>max-age=3600</param-value>
 *	  </init-param>
 *	</filter>
 *
 *	<filter-mapping>
 *	  <filter-name>ResponseHeaderFilter</filter-name>
 *	  <url-pattern>/idegaweb/bundles/*</url-pattern>
 *	</filter-mapping>
 * 
 * @author eiki
 *
 */
public class ResponseHeaderFilter implements Filter {
	FilterConfig fc;

	public void doFilter(ServletRequest req, ServletResponse res,FilterChain chain) throws IOException, ServletException {
	
		HttpServletResponse response = (HttpServletResponse) res;
		// set the provided HTTP response parameters
		for (Enumeration e = fc.getInitParameterNames(); e.hasMoreElements();) {
			String headerName = (String) e.nextElement();
			response.addHeader(headerName, fc.getInitParameter(headerName));
		}
		// pass the request/response on
		chain.doFilter(req, response);
	}

	public void init(FilterConfig filterConfig) {
		this.fc = filterConfig;
	}

	public void destroy() {
		this.fc = null;
	}
	
}