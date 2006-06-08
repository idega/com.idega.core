/*
 * $Id: ServletWrapper.java,v 1.3 2006/06/08 15:06:04 tryggvil Exp $
 * Created on 31.5.2006 in project com.idega.slide
 *
 * Copyright (C) 2006 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.servlet;

import java.io.IOException;
import java.util.Enumeration;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServlet;


/**
 * <p>
 * Wrapper around a contained Servlet instance
 * </p>
 *  Last modified: $Date: 2006/06/08 15:06:04 $ by $Author: tryggvil $
 * 
 * @author <a href="mailto:tryggvil@idega.com">tryggvil</a>
 * @version $Revision: 1.3 $
 */
public class ServletWrapper extends HttpServlet {
	
	/**
	 * Comment for <code>serialVersionUID</code>
	 */
	private static final long serialVersionUID = 6863799972247973901L;

	private HttpServlet servlet;
	private ServletConfig servletConfig;
	
	public ServletWrapper(){
	}
	
	/**
	 * 
	 * @see javax.servlet.GenericServlet#destroy()
	 */
	public void destroy() {
		this.servlet.destroy();
	}

	/**
	 * @param arg0
	 * @return
	 * @see javax.servlet.GenericServlet#getInitParameter(java.lang.String)
	 */
	public String getInitParameter(String arg0) {
		return this.servlet.getInitParameter(arg0);
	}

	/**
	 * @return
	 * @see javax.servlet.GenericServlet#getInitParameterNames()
	 */
	public Enumeration getInitParameterNames() {
		return this.servlet.getInitParameterNames();
	}

	/**
	 * @return
	 * @see javax.servlet.GenericServlet#getServletConfig()
	 */
	public ServletConfig getServletConfig() {
		if(this.servletConfig==null){
			return this.servlet.getServletConfig();
		}
		else{
			return this.servletConfig;
		}
	}
	
	protected void setServletConfig(ServletConfig config){
		this.servletConfig=config;
	}

	/**
	 * @return
	 * @see javax.servlet.GenericServlet#getServletContext()
	 */
	public ServletContext getServletContext() {
		return this.servlet.getServletContext();
	}

	/**
	 * @return
	 * @see javax.servlet.GenericServlet#getServletInfo()
	 */
	public String getServletInfo() {
		return this.servlet.getServletInfo();
	}

	/**
	 * @return
	 * @see javax.servlet.GenericServlet#getServletName()
	 */
	public String getServletName() {
		return this.servlet.getServletName();
	}

	/**
	 * @throws ServletException
	 * @see javax.servlet.GenericServlet#init()
	 */
	public void init() throws ServletException {
		//servlet.init();
	}

	/**
	 * @param arg0
	 * @throws ServletException
	 * @see javax.servlet.GenericServlet#init(javax.servlet.ServletConfig)
	 */
	public void init(ServletConfig config) throws ServletException {
		initializeServletWrapper(config);
		this.servlet.init(config);
		super.init(config);
		init();
	}

	/**
	 * <p>
	 * TODO tryggvil describe method initializeServletWrapper
	 * </p>
	 * @param arg0
	 */
	protected void initializeServletWrapper(ServletConfig arg0) {
	}

	/**
	 * @param arg0
	 * @param arg1
	 * @see javax.servlet.GenericServlet#log(java.lang.String, java.lang.Throwable)
	 */
	public void log(String arg0, Throwable arg1) {
		try{
			this.servlet.log(arg0, arg1);
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}

	/**
	 * @param arg0
	 * @see javax.servlet.GenericServlet#log(java.lang.String)
	 */
	public void log(String arg0) {
		try{
			this.servlet.log(arg0);
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}

	/**
	 * @param arg0
	 * @param arg1
	 * @throws ServletException
	 * @throws IOException
	 * @see javax.servlet.http.HttpServlet#service(javax.servlet.ServletRequest, javax.servlet.ServletResponse)
	 */
	public void service(ServletRequest arg0, ServletResponse arg1) throws ServletException, IOException {
		this.servlet.service(arg0, arg1);
	}
	
	/**
	 * @return the servlet
	 */
	protected HttpServlet getServlet() {
		return this.servlet;
	}
	
	/**
	 * @param servlet the servlet to set
	 */
	protected void setServlet(HttpServlet servlet) {
		this.servlet = servlet;
	}
	
	public void reload() {
		destroy();
		this.servlet=null;
		initializeServletWrapper(getServletConfig());
		try {
			init(getServletConfig());
		}
		catch (ServletException e) {
			e.printStackTrace();
		}
	}
}