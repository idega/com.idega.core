/*
 * $Id: IWActionURIManager.java,v 1.4 2005/02/28 13:37:06 eiki Exp $
 * Created on 31.1.2005
 *
 * Copyright (C) 2005 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.core.uri;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import com.idega.idegaweb.IWApplicationContext;
import com.idega.idegaweb.IWMainApplication;
import com.idega.repository.data.Singleton;


/**
 * 
 *  Last modified: $Date: 2005/02/28 13:37:06 $ by $Author: eiki $
 * 
 * A singleton business object to get an IWActionURIHandler for an URI or the redirect URI directly.<br>
 * Register you IWActionURIHandlers using the registerHandler methods. <br>
 * The first handler that can handle an URI is always used so if you need<br>
 * your custom handler to be used you might need use registerHandler(int,handler) <br>
 * and set a low number (0-x) to prioratize your handler before the default one.
 * 
 * @author <a href="mailto:eiki@idega.com">Eirikur S. Hrafnsson</a>
 * @version $Revision: 1.4 $
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

	public String getActionURIPrefixWithContext(String action){
		String context = IWMainApplication.getDefaultIWMainApplication().getApplicationContextURI();
		StringBuffer buf = new StringBuffer();
		if(context.endsWith("/")){
			context = context.substring(0,context.length()-1);
		}
		
		buf.append(context).append(IDEGAWEB_ACTION_PATH_PREFIX).append(action).append("/");
		return buf.toString();
	}
	
	public String getActionURIPrefixWithContext(String action, String suffix){
		String prefix = getActionURIPrefixWithContext(action);
		if(suffix.startsWith("/")){
			suffix = suffix.substring(1);
		}
		return prefix+suffix;	
	}
	
	public String getActionURIPrefixWithoutContext(String action){
		StringBuffer buf = new StringBuffer();
		buf.append(IDEGAWEB_ACTION_PATH_PREFIX).append(action).append("/");
		return buf.toString();
	}
	
	public String getActionURIPrefixWithoutContext(String action, String suffix){
		String prefix = getActionURIPrefixWithoutContext(action);
		if(suffix.startsWith("/")){
			suffix = suffix.substring(1);
		}
		return prefix+suffix;		
	}
	
	
	/**
	 * 
	 * @return the list of IWActionURIHandlers, default impl uses a LinkedList
	 */
	public List getHandlerList() {
		if(handlerList==null){
			handlerList = new LinkedList();
		}
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
				
		IWActionURIHandler handler = getIWActionURIHandler(requestURI);
		
		IWActionURI actionURI = handler.getIWActionURI(requestURI);
	
		if(actionURI==null){
			actionURI = new IWActionURI(requestURI);
		}
		
		return handler.getRedirectURI(actionURI); 
	}
	
	
	public IWActionURIHandler getIWActionURIHandler(String requestURI){
	//TODO register handlers with regular expressions	, and lookup with regexp instead of iterator	
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
