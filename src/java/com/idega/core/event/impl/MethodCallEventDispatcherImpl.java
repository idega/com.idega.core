/*
 * $Id: MethodCallEventDispatcherImpl.java,v 1.1.2.3 2007/05/29 08:13:36 laddi Exp $ Created on Jan 9, 2007
 * 
 * Copyright (C) 2007 Idega Software hf. All Rights Reserved.
 * 
 * This software is the proprietary information of Idega hf. Use is subject to license terms.
 */
package com.idega.core.event.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.idega.core.event.MethodCallEvent;
import com.idega.core.event.MethodCallEventDispatcher;
import com.idega.core.event.MethodCallEventHandler;

/**
 * 
 * Last modified: $Date: 2007/05/29 08:13:36 $ by $Author: laddi $
 * 
 * @author <a href="mailto:thomas@idega.com">thomas</a>
 * @version $Revision: 1.1.2.3 $
 */
public class MethodCallEventDispatcherImpl implements MethodCallEventDispatcher {

	// identifer used e.g. for sender id (used by filters)
	private String identifier = null;

	private List eventHandler = null;

	private boolean isActive = false;

	public boolean isActive() {
		return isActive;
	}

	public MethodCallEventDispatcherImpl(String identifier) {
		this.identifier = identifier;
	}

	public String getIdentifier() {
		return identifier;
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
		if (!eventHandler.remove(methodCallEventHandler)) {
			// wrong call, is already removed or was never added
			return;
		}
		// still active?
		if (eventHandler.isEmpty()) {
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
