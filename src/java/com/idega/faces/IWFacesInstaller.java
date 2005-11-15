/*
 * $Id: IWFacesInstaller.java,v 1.2 2005/11/15 23:57:58 tryggvil Exp $
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
		System.out.println("IWInstaller.contextInitialized");
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
		/*try {
			//iwViewHandler = (ViewHandler)Class.forName("com.idega.faces.IWViewHandlerImpl").newInstance();
			Constructor[] css = Class.forName("com.idega.faces.componentbased.IWViewHandlerImpl").getConstructors();
			Constructor cs = css[1];
			Object[] args = {origViewHandler};
			iwViewHandler = (ViewHandler)cs.newInstance(args);
			
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
		
		IWMainApplication iwma = IWMainApplication.getIWMainApplication(context);
		
		iwViewHandler = new IWViewHandlerImpl(origViewHandler,iwma);
		app.setViewHandler(iwViewHandler);		
	}		
}
