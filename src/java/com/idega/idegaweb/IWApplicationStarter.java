/*
 * $Id: IWApplicationStarter.java,v 1.3 2005/06/03 15:18:29 thomas Exp $
 *
 * Created by Tryggvi Larusson in 2004
 *
 * Copyright (C) 2004 Idega. All Rights Reserved.
 *
 * This software is the proprietary information of Idega.
 * Use is subject to license terms.
 *
 */
package com.idega.idegaweb;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import javax.faces.FactoryFinder;
import javax.faces.application.Application;
import javax.faces.application.ApplicationFactory;
import javax.faces.application.ViewHandler;
import javax.faces.lifecycle.Lifecycle;
import javax.faces.lifecycle.LifecycleFactory;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import com.idega.repository.data.RefactorClassRegistry;


/**
 * This Listener starts up the idegaWeb environment and starts the IWMainApplication.
 * 
 * @author <a href="mailto:tryggvi@idega.is">Tryggvi Larusson</a>
 */
public class IWApplicationStarter implements ServletContextListener {

	private IWMainApplicationStarter starter;
	
	/**
	 * initialize jsf environment
	 */
	public void contextInitialized(ServletContextEvent event) {
		System.out.println("IWApplicationStarter.contextInitialized");
		//getDefaultLifecycle().addPhaseListener(new IWPhaseListener());
		ServletContext sContext = event.getServletContext();
		//installViewHandler(sContext);
		starter = new IWMainApplicationStarter(sContext);
		
	}
	
	protected Lifecycle getDefaultLifecycle(){
		return getLifecycleFactory().getLifecycle(LifecycleFactory.DEFAULT_LIFECYCLE);
	}
	
	protected LifecycleFactory getLifecycleFactory(){
		return (LifecycleFactory)FactoryFinder.getFactory(FactoryFinder.LIFECYCLE_FACTORY);
	}

	/**
	 * @see javax.servlet.ServletContextListener#contextDestroyed(javax.servlet.ServletContextEvent)
	 */
	public void contextDestroyed(ServletContextEvent arg0) {
		starter.shutdown();
		starter=null;
	}
	
	public static void installViewHandler(ServletContext context) {
		
		ApplicationFactory factory = (ApplicationFactory) FactoryFinder.getFactory(FactoryFinder.APPLICATION_FACTORY);
		Application app = factory.getApplication();
		ViewHandler origViewHandler = app.getViewHandler();
		ViewHandler iwViewHandler=origViewHandler;
		try {
			//iwViewHandler = (ViewHandler)Class.forName("com.idega.faces.IWViewHandlerImpl").newInstance();
			Constructor[] css = RefactorClassRegistry.forName("com.idega.faces.smile.IWViewHandlerImpl").getConstructors();
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
		}
		app.setViewHandler(iwViewHandler);		
	}

}
