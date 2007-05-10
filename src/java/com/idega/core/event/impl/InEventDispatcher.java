/*
 * $Id: InEventDispatcher.java,v 1.2 2007/05/10 22:35:04 thomas Exp $
 * Created on Jan 11, 2007
 *
 * Copyright (C) 2007 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.core.event.impl;

import com.idega.core.event.MethodCallEvent;
import com.idega.core.event.MethodCallEventDispatcher;
import com.idega.core.event.MethodCallEventHandler;


/**
 * 
 *  Last modified: $Date: 2007/05/10 22:35:04 $ by $Author: thomas $
 * 
 * @author <a href="mailto:thomas@idega.com">thomas</a>
 * @version $Revision: 1.2 $
 */
public class InEventDispatcher implements MethodCallEventDispatcher {
	
	private OutEventDispatcher outEventDispatcher = null;
	private MethodCallEventDispatcher inEventDispatcher = null;

	public InEventDispatcher(OutEventDispatcher outEventDispatcher) {
		this.outEventDispatcher = outEventDispatcher;
		inEventDispatcher = new MethodCallEventDispatcherImpl();
		
	}
	
	/* (non-Javadoc)
	 * @see com.idega.core.event.MethodCallEventDispatcher#addListener(com.idega.core.event.MethodCallEventHandler)
	 */
	public void addListener(MethodCallEventHandler methodCallEventHandler) {
		inEventDispatcher.addListener(methodCallEventHandler);
	}

	/* (non-Javadoc)
	 * @see com.idega.core.event.MethodCallEventDispatcher#fireEvent(com.idega.core.event.MethodCallEvent)
	 */
	public void fireEvent(MethodCallEvent methodCallEvent) {
		outEventDispatcher.catchEventOnce(methodCallEvent);
		inEventDispatcher.fireEvent(methodCallEvent);
	}

	/* (non-Javadoc)
	 * @see com.idega.core.event.MethodCallEventDispatcher#getID()
	 */
	public String getIdentifier() {
		return inEventDispatcher.getIdentifier();
	}

	/* (non-Javadoc)
	 * @see com.idega.core.event.MethodCallEventDispatcher#isActive()
	 */
	public boolean isActive() {
		return inEventDispatcher.isActive();
	}

	/* (non-Javadoc)
	 * @see com.idega.core.event.MethodCallEventDispatcher#removeListener(com.idega.core.event.MethodCallEventHandler)
	 */
	public void removeListener(MethodCallEventHandler methodCallEventHandler) {
		inEventDispatcher.removeListener(methodCallEventHandler);
	}
}
