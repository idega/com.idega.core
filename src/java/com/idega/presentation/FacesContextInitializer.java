package com.idega.presentation;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.ServletContext;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import com.idega.business.SpringBeanName;

@SpringBeanName("customIdegaFacesContextInitializer")
public interface FacesContextInitializer {

	public FacesContext getInitializedFacesContext(ServletContext context, ServletRequest request, ServletResponse response);
	
	public ExternalContext getInitializedExternalContext(ServletContext context, ServletRequest request, ServletResponse response);
	
}
