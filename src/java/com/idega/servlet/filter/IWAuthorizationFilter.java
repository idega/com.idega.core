/*
 * Created on 31.7.2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
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
import com.idega.core.accesscontrol.business.LoginBusinessBean;
import com.idega.presentation.IWContext;

/**
 * @author tryggvil
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class IWAuthorizationFilter extends BaseFilter implements Filter {

	/* (non-Javadoc)
	 * @see javax.servlet.Filter#init(javax.servlet.FilterConfig)
	 */
	public void init(FilterConfig arg0) throws ServletException {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see javax.servlet.Filter#doFilter(javax.servlet.ServletRequest, javax.servlet.ServletResponse, javax.servlet.FilterChain)
	 */
	public void doFilter(ServletRequest srequest, ServletResponse sresponse,
			FilterChain chain) throws IOException, ServletException {
		
		
		HttpServletRequest request = (HttpServletRequest)srequest;
		HttpServletResponse response = (HttpServletResponse)sresponse;
		
		boolean hasPermission = getIfUserHasPermission(request,response);
		if(!hasPermission){
			String newUrl = getNewLoginUri(request);
			response.sendRedirect(newUrl);
		}
		else{
			chain.doFilter(srequest,sresponse);
		}
		//chain.doFilter(srequest,sresponse);

	}
	
	protected boolean getIfUserHasPermission(HttpServletRequest request,HttpServletResponse response){
		String uri = getURLMinusContextPath(request);
		if(uri.startsWith(NEW_WORKSPACE_URI_MINUSSLASH)){
			IWContext iwc = new IWContext(request,response,request.getSession().getServletContext());
			if(!LoginBusinessBean.isLoggedOn(iwc)){
				return false;
			}
		}
		return true;
	}

	/* (non-Javadoc)
	 * @see javax.servlet.Filter#destroy()
	 */
	public void destroy() {
		// TODO Auto-generated method stub

	}

}
