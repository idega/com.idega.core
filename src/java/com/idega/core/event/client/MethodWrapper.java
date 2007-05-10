/*
 * $Id: MethodWrapper.java,v 1.2 2007/05/10 22:35:04 thomas Exp $
 * Created on Jan 12, 2007
 *
 * Copyright (C) 2007 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.core.event.client;


/**
 * 
 *  Last modified: $Date: 2007/05/10 22:35:04 $ by $Author: thomas $
 * 
 * @author <a href="mailto:thomas@idega.com">thomas</a>
 * @version $Revision: 1.2 $
 */
public abstract class MethodWrapper {
	
	public static final String OBJECT1 = "object1";
	
	public static final String OBJECT2 = "object2";
	
	
	public String getIdentifier() {
		return "method";
	}
	
	public void perform(Object object1) {
		// do nothing
	}
	
	public void perform(Object object1, Object object2) {
		// do nothing
	}
}
