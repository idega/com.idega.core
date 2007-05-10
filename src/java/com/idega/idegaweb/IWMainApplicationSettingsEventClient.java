/*
 * $Id: IWMainApplicationSettingsEventClient.java,v 1.2 2007/05/10 22:34:28 thomas Exp $
 * Created on Jan 11, 2007
 *
 * Copyright (C) 2007 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.idegaweb;

import com.idega.core.event.MethodCallEvent;
import com.idega.core.event.MethodCallEventGenerator;
import com.idega.core.event.MethodCallEventHandler;
import com.idega.core.event.client.MethodWrapper;
import com.idega.core.event.impl.EventClient;


/**
 * 
 *  Last modified: $Date: 2007/05/10 22:34:28 $ by $Author: thomas $
 * 
 * @author <a href="mailto:thomas@idega.com">thomas</a>
 * @version $Revision: 1.2 $
 */
public class IWMainApplicationSettingsEventClient extends EventClient implements MethodCallEventGenerator, MethodCallEventHandler {
		 
	public IWMainApplicationSettingsEventClient() {
		initialize(IWMainApplicationSettings.class);
	}
	
	/* (non-Javadoc)
	 * @see com.idega.core.event.MethodCallEventHandler#handleEvent(com.idega.core.event.MethodCallEvent)
	 */
	public void handleEvent(MethodCallEvent methodCallEvent) {
		if (isEventCompatible(methodCallEvent)) {
			setApplicationBindingInMap(methodCallEvent);
			removeApplicationBindingFromMap(methodCallEvent);
		}
	}

	// start list of pairs 
	// event firing methods and event handling methods 
	
	// pair 1
	
	protected static final String SET_APPLICATION_BINDING_IN_MAP = "setApplicationBindingInMap";
	
	public void setApplicationBindingInMap(String key, String value) {
		if (isNothingToDo()) return;
		fireEvent(SET_APPLICATION_BINDING_IN_MAP,MethodWrapper.OBJECT1, key, MethodWrapper.OBJECT2, value);
	}
	
	public void setApplicationBindingInMap(MethodCallEvent methodCallEvent) {
		if (isMethod(methodCallEvent, SET_APPLICATION_BINDING_IN_MAP)) {
			String key = methodCallEvent.get(MethodWrapper.OBJECT1);
			String value = methodCallEvent.get(MethodWrapper.OBJECT2);
			MethodWrapper methodWrapper = getMethod(SET_APPLICATION_BINDING_IN_MAP);
			methodWrapper.perform(key, value);
		}
	}
	
	// pair 2
	
	protected static final String REMOVE_APPLICATION_BINDING_FROM_MAP = "removeApplicationBindingFromMap";
	
	public void removeApplicationBindingFromMap(String key) {
		if (isNothingToDo()) return;
		fireEvent(REMOVE_APPLICATION_BINDING_FROM_MAP, MethodWrapper.OBJECT1, key);
	}
	
	public void removeApplicationBindingFromMap(MethodCallEvent methodCallEvent) {
		if (isMethod(methodCallEvent, REMOVE_APPLICATION_BINDING_FROM_MAP)) {
			String key = methodCallEvent.get(MethodWrapper.OBJECT1);
			MethodWrapper methodWrapper = getMethod(REMOVE_APPLICATION_BINDING_FROM_MAP);
			methodWrapper.perform(key);
		}
	}
	
}
