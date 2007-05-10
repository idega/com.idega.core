/*
 * $Id: EventClient.java,v 1.2 2007/05/10 22:35:04 thomas Exp $
 * Created on Jan 11, 2007
 *
 * Copyright (C) 2007 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.core.event.impl;

import java.util.HashMap;
import java.util.Map;
import com.idega.core.event.MethodCallEvent;
import com.idega.core.event.MethodCallEventDispatcher;
import com.idega.core.event.MethodCallEventGenerator;
import com.idega.core.event.MethodCallEventHandler;
import com.idega.core.event.client.MethodWrapper;


/**
 * 
 *  Last modified: $Date: 2007/05/10 22:35:04 $ by $Author: thomas $
 * 
 * @author <a href="mailto:thomas@idega.com">thomas</a>
 * @version $Revision: 1.2 $
 */
public abstract class EventClient implements MethodCallEventGenerator, MethodCallEventHandler {
	
	private static final String METHOD_NAME = "methodName";
	
	protected MethodCallEventDispatcher dispatcher = null;
	
	private String subject = null;
	
	private Map methodWrappers = null;
	
	protected void initialize(Class parentClass) {
		this.subject = parentClass.getName();
		// set firing events
		InOutEventDispatcher inOutEventDispatcher = InOutEventDispatcher.getInstance();
		dispatcher = inOutEventDispatcher.getOutEventDispatcher();
		// set listening to events
		MethodCallEventDispatcher inDispatcher = inOutEventDispatcher.getInEventDispatcher();
		inDispatcher.addListener(this);
	}
	
	public void addMethod(MethodWrapper methodWrapper) {
		String identifier = methodWrapper.getIdentifier();
		getMethodWrappers().put(identifier, methodWrapper);
	}
	
	public MethodWrapper getMethod(String methodName) {
		return (MethodWrapper) getMethodWrappers().get(methodName);
	}
	
	private Map getMethodWrappers() {
		if (methodWrappers == null) {
			methodWrappers = new HashMap();
		}
		return methodWrappers;
	}
	
	protected boolean isNothingToDo()	{
		return ! (dispatcher.isActive());
	}
	
	protected boolean isMethod(MethodCallEvent methodCallEvent, String methodName) {
		return methodName.equals(methodCallEvent.get(METHOD_NAME));
	}

	protected void fireEvent(String methodName) {
		fireEvent(methodName, null, null);
	}

	protected void fireEvent(String methodName, String key1, String value1) {
		fireEvent(methodName, key1, value1, null, null);
	}
	
	protected void fireEvent(String methodName, String key1, String value1, String key2, String value2) {
		// use application id as sender 
		String sender = dispatcher.getIdentifier();
		MethodCallEvent methodCallEvent = new MethodCallEventImpl(sender, subject);
		methodCallEvent.put(METHOD_NAME, methodName);
		if (key1 != null) {
			methodCallEvent.put(key1, value1);
		}
		if (key2 != null) {
			methodCallEvent.put(key2, value2);
		}
		dispatcher.fireEvent(methodCallEvent);	
	}

	protected boolean isEventCompatible(MethodCallEvent methodCallEvent) {
		String eventSubject = methodCallEvent.getSubject();
		return subject.equals(eventSubject);
	}

	
	/* (non-Javadoc)
	 * @see com.idega.core.event.MethodCallEventHandler#handleEvent(com.idega.core.event.MethodCallEvent)
	 */
	public void handleEvent(MethodCallEvent methodCallEvent) {
		// typically method body looks like:
		
		//if (isEventCompatible(methodCallEvent)) {
		//	do something
		//}
	}
}
