/*
 * $Id: FireEventFilter.java,v 1.1.2.1 2007/02/02 01:13:22 thomas Exp $
 * Created on Jan 11, 2007
 *
 * Copyright (C) 2007 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.core.event.impl;

import java.util.Iterator;
import java.util.Map;
import com.idega.core.event.MethodCallEvent;
import com.idega.util.datastructures.map.TimeLimitedMap;


/**
 * 
 *  Last modified: $Date: 2007/02/02 01:13:22 $ by $Author: thomas $
 * 
 * @author <a href="mailto:thomas@idega.com">thomas</a>
 * @version $Revision: 1.1.2.1 $
 */
public class FireEventFilter {
	
	public static final long EVENT_STORE_TIME = 30;
	
	private Map forbiddenEvents = null;
	
	public FireEventFilter() {
		forbiddenEvents = TimeLimitedMap.getInstanceWithTimeLimitInMinutes(EVENT_STORE_TIME);
	}
	
	public void add(MethodCallEvent methodCallEvent) {
		forbiddenEvents.put(methodCallEvent.getIdentifier(), methodCallEvent);
	}

	public boolean isAccepted(MethodCallEvent methodCallEvent) {
		String sameEvent = findEqualEvent(methodCallEvent);
		if (sameEvent == null) {
			return true;
		}
		forbiddenEvents.remove(sameEvent);
		return false;
	}
		
	private String findEqualEvent(MethodCallEvent methodCallEvent) {
		Iterator iterator = forbiddenEvents.values().iterator();
		while (iterator.hasNext()) {
			MethodCallEvent forbiddenEvent = (MethodCallEvent) iterator.next();
			if (hasSameContent(forbiddenEvent, methodCallEvent)) {
				return forbiddenEvent.getIdentifier();
			}
		}
		return null;
	}
	
	private boolean hasSameContent(MethodCallEvent forbiddenEvent, MethodCallEvent event) {
		String forbiddenSubject = forbiddenEvent.getSubject();
		String subject = event.getSubject();
		if (! hasSameValue(forbiddenSubject, subject)) {
			return false;
		}
		Iterator forbiddenKeys = forbiddenEvent.getKeys().iterator();
		while (forbiddenKeys.hasNext()) {
			String key = (String) forbiddenKeys.next();
			String forbiddenValue = forbiddenEvent.get(key);
			String value = event.get(key);
			if (! hasSameValue(forbiddenValue,value)) {
				return false;
			}
		}
		return true;
	}
	
	private boolean hasSameValue(String value1, String value2) {
		if (value1 == null && value2 == null) {
			return true;
		}
		if (value1 != null) {
			return value1.equals(value2);
		}
		// value1 is null but not value2 therefore false
		return false; 
	}
}
