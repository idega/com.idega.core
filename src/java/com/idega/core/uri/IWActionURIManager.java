/*
 * $Id: IWActionURIManager.java,v 1.2 2005/02/25 14:50:13 eiki Exp $
 * Created on 31.1.2005
 *
 * Copyright (C) 2005 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.core.uri;

import java.util.Iterator;
import java.util.List;
import com.idega.idegaweb.IWApplicationContext;
import com.idega.idegaweb.IWMainApplication;
import com.idega.repository.data.Singleton;


/**
 * 
 *  Last modified: $Date: 2005/02/25 14:50:13 $ by $Author: eiki $
 * 
 * A singleton business object to get an IWActionURIHandler for an URI or the redirect URI directly.<br>
 * Register you IWActionURIHandlers using the registerHandler methods. <br>
 * The first handler that can handle an URI is always used so if you need<br>
 * your custom handler to be used you might need use registerHandler(int,handler) <br>
 * and set a low number (0-x) to prioratize your handler before the default one.
 * 
 * @author <a href="mailto:eiki@idega.com">Eirikur S. Hrafnsson</a>
 * @version $Revision: 1.2 $
 */
public class IWActionURIManager implements Singleton {
	
	public static String IDEGAWEB_ACTION_PATH_PREFIX = "/idegaweb/action/";
	public static String IW_ACTION_URI_HANDLER_APPLICATION_STORAGE_PARAM = "IWActionURIManager";
	
	private List handlerList;
	
	//static variables:
	private DefaultIWActionURIHandler defaultHandler;
	
	private IWActionURIManager(){}
	
	public static IWActionURIManager getInstance(){
		IWApplicationContext iwac = IWMainApplication.getDefaultIWApplicationContext();
		
		IWActionURIManager manager = (IWActionURIManager)iwac.getApplicationAttribute(IW_ACTION_URI_HANDLER_APPLICATION_STORAGE_PARAM);
		if(manager==null){
			manager=new IWActionURIManager();
			iwac.setApplicationAttribute(IW_ACTION_URI_HANDLER_APPLICATION_STORAGE_PARAM,manager);
		}
		return manager;
	} 

	
	/**
	 * 
	 * @return the list of IWActionURIHandlers, default impl uses a LinkedList
	 */
	public List getHandlerList() {
		return handlerList;
	}
	
	public synchronized void setHandlerList(List handlerList) {
		this.handlerList = handlerList;
	}
	/**
	 * @param handler
	 */
	public synchronized void registerHandler(IWActionURIHandler handler){
		getHandlerList().add(handler);
	}
	
	/**
	 * @param position 0-x lower number gets higher priority
	 * @param handler
	 */
	public synchronized void registerHandler(int position, IWActionURIHandler handler){
		getHandlerList().add(position,handler);
	}
	
	public String getRedirectURI(String requestURI){
		
		requestURI = IWMainApplication.getDefaultIWMainApplication().getURIFromURL(requestURI);
		
		IWActionURIHandler handler = getIWActionURIHandler(requestURI);
		
		IWActionURI actionURI = handler.getIWActionURI(requestURI);
		
		if(actionURI==null){
			actionURI = new IWActionURI(requestURI);
		}
		
		return handler.getRedirectURI(actionURI); 
	}
	
	
	public IWActionURIHandler getIWActionURIHandler(String requestURI){
	//TODO register handlers with regular expressions	, and lookup with regexp instead of iterator	
		requestURI = IWMainApplication.getDefaultIWMainApplication().getURIFromURL(requestURI);
		IWActionURI actionURI = new IWActionURI(requestURI);
		List handlers = getHandlerList();
		Iterator iter = handlers.iterator();
		
		boolean handles = false;
		while (iter.hasNext()) {
			IWActionURIHandler handler = (IWActionURIHandler) iter.next();
			handles = handler.canHandleIWActionURI(actionURI);
			if(handles){
				return handler;
			}
		}
		
		//fallback to default handler>
		return getDefaultHandler();
	}
	
	public IWActionURIHandler getDefaultHandler(){
		if(defaultHandler==null){
			defaultHandler=new DefaultIWActionURIHandler();
		}
		return defaultHandler;
	}
	
}
