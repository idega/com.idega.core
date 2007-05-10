/*
 * $Id: MethodCallEvent.java,v 1.2 2007/05/10 22:35:04 thomas Exp $
 * Created on Jan 9, 2007
 *
 * Copyright (C) 2007 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.core.event;

import java.util.Set;


/**
 * 
 *  Last modified: $Date: 2007/05/10 22:35:04 $ by $Author: thomas $
 * 
 * @author <a href="mailto:thomas@idega.com">thomas</a>
 * @version $Revision: 1.2 $
 */
public interface MethodCallEvent {
	
	void put(String name, String value);
	
	String get(String name);
	
	String getIdentifier();
	
	String getSender();
	
	String getSubject();
	
	Set getKeys();
	
}
