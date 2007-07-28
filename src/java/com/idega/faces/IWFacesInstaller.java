/*
 * $Id: IWFacesInstaller.java,v 1.3 2007/07/28 13:17:07 civilis Exp $
 *
 * Created on 19.5.2004 by Tryggvi Larusson
 *
 * Copyright (C) 2004 Idega. All Rights Reserved.
 *
 * This software is the proprietary information of Idega.
 * Use is subject to license terms.
 *
 */
package com.idega.faces;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.faces.FactoryFinder;
import javax.faces.application.Application;
import javax.faces.application.ApplicationFactory;
import javax.faces.application.ViewHandler;
import javax.faces.lifecycle.Lifecycle;
import javax.faces.lifecycle.LifecycleFactory;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import com.idega.idegaweb.IWMainApplication;

/**
 * This Listener starts up the JavaServerFaces extensions for idegaWeb.
 * 
 * 
 * @author <a href="mailto:tryggvi@idega.is">Tryggvi Larusson</a>
 */
public class IWFacesInstaller implements ServletContextListener {
	/* (non-Javadoc)
	 * @see javax.servlet.ServletContextListener#contextInitialized(javax.servlet.ServletContextEvent)
	 */
	public void contextInitialized(ServletContextEvent arg0) {
		
		Logger.getLogger(this.getClass().getName()).log(Level.INFO, "Calling IWInstaller context initialized");
		installViewHandler(arg0.getServletContext());
		installFacesPhaseListener();
	}
	/* (non-Javadoc)
	 * @see javax.servlet.ServletContextListener#contextDestroyed(javax.servlet.ServletContextEvent)
	 */
	public void contextDestroyed(ServletContextEvent arg0) {
	}
	
	protected Lifecycle getDefaultLifecycle(){
		return getLifecycleFactory().getLifecycle(LifecycleFactory.DEFAULT_LIFECYCLE);
	}
	
	protected LifecycleFactory getLifecycleFactory(){
		return (LifecycleFactory)FactoryFinder.getFactory(FactoryFinder.LIFECYCLE_FACTORY);
	}
	
	protected void installFacesPhaseListener(){
		//This installs the phasesListener that invokes the main(iwc) method;
		getDefaultLifecycle().addPhaseListener(new IWPhaseListener());
	}

	
	public static void installViewHandler(ServletContext context) {
		
		//This installs the special idegaWeb viewHandlers that use the ViewNode system.
		
		ApplicationFactory factory = (ApplicationFactory) FactoryFinder.getFactory(FactoryFinder.APPLICATION_FACTORY);
		Application app = factory.getApplication();
		ViewHandler origViewHandler = app.getViewHandler();
		ViewHandler iwViewHandler=origViewHandler;
		
		IWMainApplication iwma = IWMainApplication.getIWMainApplication(context);
		
		iwViewHandler = new IWViewHandlerImpl(origViewHandler,iwma);
		app.setViewHandler(iwViewHandler);		
	}		
}
