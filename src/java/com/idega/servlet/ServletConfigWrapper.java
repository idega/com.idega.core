/*
 * $Id: ServletConfigWrapper.java,v 1.3 2009/01/07 11:39:06 tryggvil Exp $
 * Created on 31.5.2006 in project com.idega.slide
 *
 * Copyright (C) 2006 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.servlet;

import java.util.Enumeration;
import java.util.Hashtable;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;


/**
 * <p>
 * TODO tryggvil Describe Type ServletConfigWrapper
 * </p>
 *  Last modified: $Date: 2009/01/07 11:39:06 $ by $Author: tryggvil $
 * 
 * @author <a href="mailto:tryggvil@idega.com">tryggvil</a>
 * @version $Revision: 1.3 $
 */
public class ServletConfigWrapper implements ServletConfig {
	
	ServletContext context;
	String servletName;
	Hashtable parameters=new Hashtable();
	
	public ServletConfigWrapper(ServletContext context,String servletName){
		this.context=context;
		this.servletName=servletName;
	}
	
	public ServletConfigWrapper(ServletConfig config){
		this.context=config.getServletContext();
		this.servletName=config.getServletName();
		Enumeration params = config.getInitParameterNames();
		while (params.hasMoreElements()) {
			String param = (String) params.nextElement();
			String paramValue = config.getInitParameter(param);
			this.parameters.put(param, paramValue);
		}
	}
	
	
	/* (non-Javadoc)
	 * @see javax.servlet.ServletConfig#getInitParameter(java.lang.String)
	 */
	public String getInitParameter(String arg0) {
		return (String) this.parameters.get(arg0);
	}

	public void setInitParameter(String key,String value){
		this.parameters.put(key, value);
	}
	
	public void setInitParameterIfNotSet(String key,String value){
		if(this.parameters.get(key)==null){
			this.parameters.put(key, value);
		}
	}
	
	/* (non-Javadoc)
	 * @see javax.servlet.ServletConfig#getInitParameterNames()
	 */
	public Enumeration getInitParameterNames() {
		return this.parameters.keys();
	}

	/* (non-Javadoc)
	 * @see javax.servlet.ServletConfig#getServletContext()
	 */
	public ServletContext getServletContext() {
		return this.context;
	}

	/* (non-Javadoc)
	 * @see javax.servlet.ServletConfig#getServletName()
	 */
	public String getServletName() {
		return this.servletName;
	}
}
