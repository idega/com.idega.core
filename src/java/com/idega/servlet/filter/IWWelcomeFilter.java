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
import com.idega.business.IBOLookup;
import com.idega.core.builder.business.BuilderService;
import com.idega.core.builder.data.ICPage;
import com.idega.idegaweb.IWMainApplication;
import com.idega.presentation.IWContext;

/**
 * @author tryggvil
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class IWWelcomeFilter implements Filter {

	private static boolean isInit=false;
	
	private static boolean START_ON_WORKSPACE=true;	
	private static boolean START_ON_PAGES=false;
	
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
		
		if(!isInit){
			init(request,response);
			isInit=true;
		}
		
		
		IWMainApplication iwma = IWMainApplication.getIWMainApplication(request.getSession().getServletContext());
		
		String appUri = iwma.getApplicationContextURI();
		String requestUri = request.getRequestURI();
		String SLASH = "/";
		
		if(!appUri.endsWith(SLASH)){
			appUri =appUri+SLASH;
		}
		
		if(requestUri.equals(appUri)){
			if(START_ON_WORKSPACE){
				request.getRequestDispatcher("/workspace/").forward(request,response);
			}
			else if(START_ON_PAGES){
				request.getRequestDispatcher("/pages/").forward(request,response);
			}
		}
		else{
			chain.doFilter(srequest,sresponse);
		}

	}

	/**
	 * @param request
	 * @param response
	 */
	private void init(HttpServletRequest request, HttpServletResponse response) {
		
		IWMainApplication iwma = IWMainApplication.getIWMainApplication(request.getSession().getServletContext());
		
		try {
			BuilderService bService = (BuilderService)IBOLookup.getServiceInstance(iwma.getIWApplicationContext(),BuilderService.class);
			ICPage rootPage = bService.getRootPage();
			if(rootPage.getChildCount()>0){
				//set the filter to forward to /pages if there are any subpages
				START_ON_PAGES=true;
				START_ON_WORKSPACE=false;
			}
			/*String serverName = request.getServerName();
			int port = request.getLocalPort();
			if(port!=80){
				serverName += ":"+port;
			}
			iwma.getIWApplicationContext().getDomain().setServerName(serverName);*/
			IWContext iwc = new IWContext(request,response, request.getSession().getServletContext());
			//This sets the domain by default:
			iwc.getDomain();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	/* (non-Javadoc)
	 * @see javax.servlet.Filter#destroy()
	 */
	public void destroy() {
		// TODO Auto-generated method stub

	}

}
