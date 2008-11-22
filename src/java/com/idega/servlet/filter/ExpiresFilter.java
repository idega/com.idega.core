package com.idega.servlet.filter;

import java.io.IOException;
import java.util.Date;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;

import com.idega.idegaweb.DefaultIWBundle;

/**
 * A filter for adding Expires and Cache-Control parameters to force browsers to
 * cache content. Any URI you register the filter to will get the expires header
 * added to it with the expiry time (seconds) you set in web.xml
 * Only adds the header if you are not loading bundles from Eclipse workspace
 * 
 * 	<filter> 
 *	  <filter-name>ExpiresFilter</filter-name> 
 *	  <filter-class>com.idega.servlet.filter.ExpiresFilter</filter-class> 
 *	  <init-param> 
 *	   <param-name>expires</param-name> 
 *	   <param-value>1800</param-value> 
 *	  </init-param> 
 *	</filter>
 *	
 *	<filter-mapping>
 *	  <filter-name>ExpiresFilter</filter-name>
 *	  <url-pattern>/idegaweb/bundles/*</url-pattern>
 *	</filter-mapping>
 * 
 * @author Eirikur S. Hrafnsson
 * 
 */
public class ExpiresFilter implements Filter {


	private static final String MAX_AGE_EQUALS = "max-age=";
	public static final String CACHE_CONTROL_HEADER_NAME = "Cache-Control";
	public static final String EXPIRES_HEADER_NAME = "Expires";
	private static final String EXPIRES = "expires";
	private static long expiresTime = 0L;
	private static boolean initialize = true;
	
	public ExpiresFilter() {
	}

	public void init(FilterConfig filterconfig) throws ServletException {
		if (initialize) {
			if(System.getProperty(DefaultIWBundle.SYSTEM_BUNDLES_RESOURCE_DIR)==null){
				String s = filterconfig.getInitParameter(EXPIRES);
				if (s != null){
					expiresTime = Long.parseLong(s);
				}
			}
		}
		initialize = false;	
	}


	public void doFilter(ServletRequest servletrequest,ServletResponse servletresponse, FilterChain filterchain) throws ServletException, IOException {
		HttpServletResponse httpservletresponse = (HttpServletResponse) servletresponse;
		
		if(expiresTime>0){
			Date date = new Date();
			httpservletresponse.setDateHeader(EXPIRES_HEADER_NAME, date.getTime()+ expiresTime * 1000L);
			httpservletresponse.setHeader(CACHE_CONTROL_HEADER_NAME, (new StringBuilder()).append(MAX_AGE_EQUALS).append(expiresTime).toString());
		}
		
		filterchain.doFilter(servletrequest, servletresponse);		
	}

	public void destroy() {
		
		
	}

}
