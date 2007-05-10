/*
 * $Id: MethodCallEventImpl.java,v 1.2 2007/05/10 22:35:04 thomas Exp $
 * Created on Jan 9, 2007
 *
 * Copyright (C) 2007 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.core.event.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import com.idega.core.event.MethodCallEvent;
import com.idega.core.idgenerator.business.UUIDGenerator;


/**
 * 
 *  Last modified: $Date: 2007/05/10 22:35:04 $ by $Author: thomas $
 * 
 * @author <a href="mailto:thomas@idega.com">thomas</a>
 * @version $Revision: 1.2 $
 */
public class MethodCallEventImpl implements MethodCallEvent {
	
	private String sender = null;
	private String subject = null;
	private String identifier = null;
	
	private Map content = null;
	
	public MethodCallEventImpl(String sender, String subject) {
		this.sender = sender;
		this.subject = subject;
	}

	/* (non-Javadoc)
	 * @see com.idega.core.event.MethodCallEvent#get(java.lang.String)
	 */
	public String get(String name) {
		return (String) content.get(name);
	}

	/* (non-Javadoc)
	 * @see com.idega.core.event.MethodCallEvent#getIdentifier()
	 */
	public String getIdentifier() {
		if (identifier == null) {
			identifier = UUIDGenerator.getInstance().generateUUID();
		}
		return identifier;
	}

	/* (non-Javadoc)
	 * @see com.idega.core.event.MethodCallEvent#getKeys()
	 */
	public Set getKeys() {
		return content.keySet();
	}

	/* (non-Javadoc)
	 * @see com.idega.core.event.MethodCallEvent#getSender()
	 */
	public String getSender() {
		return sender;
	}

	/* (non-Javadoc)
	 * @see com.idega.core.event.MethodCallEvent#getSubject()
	 */
	public String getSubject() {
		return subject;
	}

	/* (non-Javadoc)
	 * @see com.idega.core.event.MethodCallEvent#put(java.lang.String, java.lang.String)
	 */
	public void put(String name, String value) {
		if (content == null) {
			content = new HashMap();
		}
		content.put(name, value);
	}
	

	
}
