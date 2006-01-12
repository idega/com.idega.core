package com.idega.servlet.filter;

import java.io.IOException;
import java.util.logging.Logger;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.idega.presentation.IWContext;

/**
 * 
 * This filter should always be the first filter in the filter chain. It sets the correct encoding to the request
 * and response that has to be done before anything is written to the headers or gotten from the request.
 * @author <a href="mailto:eiki@idega.is">Eirikur S. Hrafnsson</a>
 */
public class IWEncodingFilter implements Filter {

	private static Logger log = Logger.getLogger(IWEncodingFilter.class.getName());
	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.Filter#doFilter(javax.servlet.ServletRequest,
	 *      javax.servlet.ServletResponse, javax.servlet.FilterChain)
	 */
	public void doFilter(ServletRequest myRequest, ServletResponse myResponse,
			FilterChain chain) throws IOException, ServletException {

		HttpServletRequest request = (HttpServletRequest)myRequest;
		HttpServletResponse response = (HttpServletResponse)myResponse;
		
		//FIXME move the real methods from the constructor to this filter!
		//IWContext iwc = new IWContext(request,response, request.getSession().getServletContext());
		IWContext.setCharactersetEncoding(request);
		request.getParameter("just forcing the getting of parameters, remove later");
		//iwc.getParameter("just forcing the getting of parameters, remove later");
		
		chain.doFilter(request, response);
	}
	/* (non-Javadoc)
	 * @see javax.servlet.Filter#init(javax.servlet.FilterConfig)
	 */
	public void init(FilterConfig arg0) throws ServletException {	
	}
	/* (non-Javadoc)
	 * @see javax.servlet.Filter#destroy()
	 */
	public void destroy() {
	}
}