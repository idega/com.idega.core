/*
 * $Id: ServletConfigWrapper.java,v 1.1 2006/06/02 10:19:13 tryggvil Exp $
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
 *  Last modified: $Date: 2006/06/02 10:19:13 $ by $Author: tryggvil $
 * 
 * @author <a href="mailto:tryggvil@idega.com">tryggvil</a>
 * @version $Revision: 1.1 $
 */
public class ServletConfigWrapper implements ServletConfig {
	
	ServletContext context;
	String servletName;
	Hashtable parameters;
	
	public ServletConfigWrapper(ServletConfig config){
		this.context=config.getServletContext();
		this.servletName=config.getServletName();
		parameters = new Hashtable();
		Enumeration params = config.getInitParameterNames();
		while (params.hasMoreElements()) {
			String param = (String) params.nextElement();
			String paramValue = config.getInitParameter(param);
			parameters.put(param, paramValue);
		}
	}
	
	
	/* (non-Javadoc)
	 * @see javax.servlet.ServletConfig#getInitParameter(java.lang.String)
	 */
	public String getInitParameter(String arg0) {
		return (String) parameters.get(arg0);
	}

	public void setInitParameter(String key,String value){
		parameters.put(key, value);
	}
	
	public void setInitParameterIfNotSet(String key,String value){
		if(parameters.get(key)==null){
			parameters.put(key, value);
		}
	}
	
	/* (non-Javadoc)
	 * @see javax.servlet.ServletConfig#getInitParameterNames()
	 */
	public Enumeration getInitParameterNames() {
		return parameters.keys();
	}

	/* (non-Javadoc)
	 * @see javax.servlet.ServletConfig#getServletContext()
	 */
	public ServletContext getServletContext() {
		// TODO Auto-generated method stub
		return context;
	}

	/* (non-Javadoc)
	 * @see javax.servlet.ServletConfig#getServletName()
	 */
	public String getServletName() {
		// TODO Auto-generated method stub
		return servletName;
	}
}
