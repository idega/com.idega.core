/*
 * $Id: IWActionURIManager.java,v 1.1 2005/02/03 11:25:48 eiki Exp $
 * Created on 31.1.2005
 *
 * Copyright (C) 2005 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.core.uri;

import java.util.Map;


/**
 * 
 *  Last modified: $Date: 2005/02/03 11:25:48 $ by $Author: eiki $
 * 
 * @author <a href="mailto:eiki@idega.com">Eirikur S. Hrafnsson</a>
 * @version $Revision: 1.1 $
 */
public class IWActionURIManager {
	
	public static String IDEGAWEB_ACTION_PATH_PREFIX = "/idegaweb/action/";
	private Map handlerMap;
	
	//static variables:
	private static IWActionURIManager instance;
	private static DefaultIWActionURIHandler defaultHandler;
	
	private IWActionURIManager(){}
	
	public static IWActionURIManager getInstance(){
		if(instance==null){
			instance=new IWActionURIManager();
		}
		return instance;
	} 

	
	protected Map getHandlerMap() {
		return handlerMap;
	}
	protected void setHandlerMap(Map handlerMap) {
		this.handlerMap = handlerMap;
	}
	/**
	 * @param scheme is the part of the URI (/idega/
	 * @param handler
	 */
	public void registerHandler(String scheme,IWActionURIHandler handler){
		getHandlerMap().put(scheme,handler);
	}
	
	public IWActionURIHandler getIWActionURIHandler(String sUri){
		IWActionURI uri;
		
			uri = new IWActionURI(sUri);
			String scheme = uri.getIdentifierPart();
			
			IWActionURIHandler handler = (IWActionURIHandler) getHandlerMap().get(scheme);
			if(handler!=null){
				return handler;
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
