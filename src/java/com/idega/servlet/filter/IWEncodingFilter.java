package com.idega.servlet.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.idega.presentation.IWContext;
import com.idega.util.expression.ELUtil;

/**
 * 
 * This filter should always be the first filter in the filter chain. It sets
 * the correct encoding to the request and response that has to be done before
 * anything is written to the headers or gotten from the request.
 * 
 * @author <a href="mailto:eiki@idega.is">Eirikur S. Hrafnsson</a>
 */
public class IWEncodingFilter implements Filter {

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.Filter#doFilter(javax.servlet.ServletRequest,
	 *      javax.servlet.ServletResponse, javax.servlet.FilterChain)
	 */
	public void doFilter(ServletRequest myRequest, ServletResponse myResponse,
			FilterChain chain) throws IOException, ServletException {

		HttpServletRequest request = (HttpServletRequest) myRequest;
		HttpServletResponse response = (HttpServletResponse) myResponse;

		// FIXME move the real methods from the constructor to this filter!
		// IWContext iwc = new IWContext(request,response,
		// request.getSession().getServletContext());

		IWContext.setCharactersetEncoding(request);

		// String contentType = request.getHeader("Content-Type");
		// String characterEncoding = lookupCharacterEncoding(contentType);

		request.getParameter("just forcing the getting of parameters, remove later");
		// iwc.getParameter("just forcing the getting of parameters, remove
		// later");

		RequestResponseProvider requestProvider = null;
		try {
			requestProvider = ELUtil.getInstance().getBean(RequestResponseProvider.class);
			requestProvider.setRequest(request);
			requestProvider.setResponse(response);
		} catch(Exception e) {}
		
		chain.doFilter(request, response);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.Filter#init(javax.servlet.FilterConfig)
	 */
	public void init(FilterConfig arg0) throws ServletException {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.Filter#destroy()
	 */
	public void destroy() {
	}

	/**
	 * Detect request encoding from Content-Type header
	 * 
	 * @param contentType
	 * @return - charset, if present.
	 */
	/*private String lookupCharacterEncoding(String contentType) {
		String characterEncoding = null;

		if (contentType != null) {
			int charsetFind = contentType.indexOf("charset=");
			if (charsetFind != -1) {
				if (charsetFind == 0) {
					// charset at beginning of Content-Type, curious
					characterEncoding = contentType.substring(8);
				} else {
					char charBefore = contentType.charAt(charsetFind - 1);
					if (charBefore == ';' || Character.isWhitespace(charBefore)) {
						// Correct charset after mime type
						characterEncoding = contentType
								.substring(charsetFind + 8);
					}
				}
			}

		}
		return characterEncoding;
	}*/
}