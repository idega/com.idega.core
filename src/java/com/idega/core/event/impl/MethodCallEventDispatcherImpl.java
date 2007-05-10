/*
 * $Id: MethodCallEventDispatcherImpl.java,v 1.2 2007/05/10 22:35:04 thomas Exp $
 * Created on Jan 9, 2007
 *
 * Copyright (C) 2007 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.core.event.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import com.idega.core.event.MethodCallEvent;
import com.idega.core.event.MethodCallEventDispatcher;
import com.idega.core.event.MethodCallEventHandler;
import com.idega.core.idgenerator.business.UUIDGenerator;


/**
 * 
 *  Last modified: $Date: 2007/05/10 22:35:04 $ by $Author: thomas $
 * 
 * @author <a href="mailto:thomas@idega.com">thomas</a>
 * @version $Revision: 1.2 $
 */
public class MethodCallEventDispatcherImpl implements  MethodCallEventDispatcher{
	
	private String id = null;
	
	private List eventHandler = null;
	
	private boolean isActive = false;
	
	public  boolean isActive() {
		return isActive;
	}

	public MethodCallEventDispatcherImpl() {
		// create a unique id for this application during runtime!
		// it does not matter if it changes when a new startup is done
		id = UUIDGenerator.getInstance().generateUUID();
	}

	public String getIdentifier() {
		return id;
	}
	
	public synchronized void addListener(MethodCallEventHandler methodCallEventHandler) {
		if (eventHandler == null) {
			eventHandler = new ArrayList();
		}
		eventHandler.add(methodCallEventHandler);
		isActive = true;
	}
	
	public synchronized void removeListener(MethodCallEventHandler methodCallEventHandler) {
		if (eventHandler != null) {
			// wrong call, was never added
			return;
		}
		if (! eventHandler.remove(methodCallEventHandler)) {
			// wrong call, is already removed or was never added
			return;
		}
		// still active?
		if (eventHandler.isEmpty()){
			isActive = false;
		}
	}
	
	public void fireEvent(MethodCallEvent methodCallEvent) {
		if (isActive()) {
			Iterator iterator = eventHandler.iterator();
			while (iterator.hasNext()) {
				MethodCallEventHandler methodCallEventListener = (MethodCallEventHandler) iterator.next();
				methodCallEventListener.handleEvent(methodCallEvent);
			}
		}
	}
	
	
	
	
	
	

}
