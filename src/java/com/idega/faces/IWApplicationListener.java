/**
 * Smile, the open-source JSF implementation.
 * Copyright (C) 2003  The smile team (http://smile.sourceforge.net)
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package com.idega.faces;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Iterator;
import java.util.List;

import javax.faces.FactoryFinder;
import javax.faces.application.Application;
import javax.faces.application.ApplicationFactory;
import javax.faces.application.ViewHandler;
import javax.faces.component.UIComponent;
import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;
import javax.faces.event.PhaseEvent;
import javax.faces.event.PhaseId;
import javax.faces.event.PhaseListener;
import javax.faces.lifecycle.Lifecycle;
import javax.faces.lifecycle.LifecycleFactory;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import com.idega.event.IWEventProcessor;
import com.idega.idegaweb.IWMainApplicationStarter;
import com.idega.presentation.IWContext;
import com.idega.presentation.PresentationObject;


/**
 * Listener class that is used to initialize the smile jsf environment at application startup
 * 
 * @author <a href="mailto:tryggvi@idega.is">Tryggvi Larusson</a>
 */
public class IWApplicationListener implements ServletContextListener {

	private IWMainApplicationStarter starter;
	
	/**
	 * initialize jsf environment
	 */
	public void contextInitialized(ServletContextEvent event) {
		System.out.println("IWApplicationListener.contextInitialized");
		getDefaultLifecycle().addPhaseListener(new IWPhaseListener());
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
	}
	
	public static void installViewHandler(ServletContext context) {
		
		ApplicationFactory factory = (ApplicationFactory) FactoryFinder.getFactory(FactoryFinder.APPLICATION_FACTORY);
		Application app = factory.getApplication();
		ViewHandler origViewHandler = app.getViewHandler();
		ViewHandler iwViewHandler=origViewHandler;
		try {
			//iwViewHandler = (ViewHandler)Class.forName("com.idega.faces.IWViewHandlerImpl").newInstance();
			Constructor[] css = Class.forName("com.idega.faces.smile.IWViewHandlerImpl").getConstructors();
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
	
	
	public static class IWPhaseListener implements PhaseListener{

		/* (non-Javadoc)
		 * @see javax.faces.event.PhaseListener#afterPhase(javax.faces.event.PhaseEvent)
		 */
		public void afterPhase(PhaseEvent arg0) {
			callMain(arg0.getFacesContext(),arg0.getFacesContext().getViewRoot());
		}
		
		protected void callMain(FacesContext context,UIViewRoot root){
			System.out.println("IWPhaseListener.callMain");
			IWContext iwc = IWContext.getIWContext(context);
			//recurseMain(iwc,root);
			call_Main(iwc,root);
		}
		
		/*
		protected void recurseMain(IWContext iwc,UIComponent comp){
			if(comp!=null){
				if(comp instanceof PresentationObject){
					PresentationObject po = (PresentationObject)comp;
					try {
						System.out.println("IWPhaseListener.recurseMain for "+po);
						po.facesMain(iwc);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				List children = comp.getChildren();
				for (Iterator iter = children.iterator(); iter.hasNext();) {
					UIComponent child = (UIComponent) iter.next();
					recurseMain(iwc,child);
				}
			}
		}*/

		/**
		 * This method goes down the tree to call the _main(iwc) methods of PresentationObjects
		 * @param iwc
		 * @param comp
		 */
		protected void call_Main(IWContext iwc,UIComponent comp){
			if(comp!=null){
				if(comp instanceof PresentationObject){
					PresentationObject po = (PresentationObject)comp;
					try {
						po._main(iwc);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					findNextInstanceOfNotPresentationObject(iwc,po);
				}
				else{
					List children = comp.getChildren();
					for (Iterator iter = children.iterator(); iter.hasNext();) {
						UIComponent child = (UIComponent) iter.next();
						call_Main(iwc,child);
					}
				}
			}
		}
		
		/**
		 * This method goes down the component tree to find a child that is not an instance
		 * of PresentationObject and calls call_Main for those components.<br>
		 * This is to handle if we have the case PresentationObject->UIComponent->PresentationObject 
		 * in the tree hierarchy and make sure _main(iwc) is called for all PresentationObjects.
		 * @param iwc
		 * @param comp
		 */
		protected void findNextInstanceOfNotPresentationObject(IWContext iwc, UIComponent comp) {
			if(comp!=null){
				if(comp instanceof PresentationObject){
					List children = comp.getChildren();
					for (Iterator iter = children.iterator(); iter.hasNext();) {
						UIComponent child = (UIComponent) iter.next();
						findNextInstanceOfNotPresentationObject(iwc,child);
					}
				}
				else{
					List children = comp.getChildren();
					for (Iterator iter = children.iterator(); iter.hasNext();) {
						UIComponent child = (UIComponent) iter.next();
						call_Main(iwc,child);
					}
				}
			}
			
		}

		/* (non-Javadoc)
		 * @see javax.faces.event.PhaseListener#beforePhase(javax.faces.event.PhaseEvent)
		 */
		public void beforePhase(PhaseEvent arg0) {
			System.out.println("IWPhaseListener.processAllEvents:");
			IWEventProcessor.getInstance().processAllEvents(IWContext.getIWContext(arg0.getFacesContext()));
		}

		/* (non-Javadoc)
		 * @see javax.faces.event.PhaseListener#getPhaseId()
		 */
		public PhaseId getPhaseId() {
			return PhaseId.RESTORE_VIEW;
		}
	}

}
