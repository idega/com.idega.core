/*
 * $Id: MethodIdentifierCache.java,v 1.1.2.1 2007/01/12 19:33:00 idegaweb Exp $
 * Created on Sep 29, 2005
 *
 * Copyright (C) 2005 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 * 
 * 
 */
package com.idega.util.reflect;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * 
* A cache for updated method identifier.
 * <p/>
 * Be careful: 
 * Every method identifer is stored in the cache even if the updated method identifier
 * is equal to the specified method identifier.
 * This is done to avoid checking the same identifier again. If there is an entry the
 * identifier was already checked.
 * 
 * <p/>
 * Note: This cache might therefore become very large. 
 * Do not keep this cache for a long time or do not change this class into an ever living singleton.
 * 
 * <p/>
 *  Last modified: $Date: 2007/01/12 19:33:00 $ by $Author: idegaweb $
 * 
 * @author <a href="mailto:thomas@idega.com">thomas</a>
 * @version $Revision: 1.1.2.1 $
 */
public class MethodIdentifierCache {
	
	private Map oldNewMethodIdentifier = null;
	
	private MethodFinder methodFinder = null;
	
	private Map getCache() {
		if (this.oldNewMethodIdentifier == null) {
			this.oldNewMethodIdentifier = new HashMap();
		}
		return this.oldNewMethodIdentifier;
	}
	
	private MethodFinder getMethodFinder() {
		if (this.methodFinder == null) {
			this.methodFinder = MethodFinder.getInstance();
		}
		return this.methodFinder;
	}
	
	public String getUpdatedMethodIdentifier(String methodIdentifier) {
		if (getCache().containsKey(methodIdentifier)) {
			return (String) getCache().get(methodIdentifier);
		}
		String updatedMethodIdentifier = getMethodFinder().getUpdatedMethodIdentifier(methodIdentifier);
		getCache().put(methodIdentifier, updatedMethodIdentifier);
		return updatedMethodIdentifier;
	}
	
	/** A smart way to avoid checking method identifiers later */
	public String getMethodIdentifierWithoutDeclaringClass(Method method) {
		String methodIdentifier = getMethodFinder().getMethodIdentifierWithoutDeclaringClass(method);
		getCache().put(methodIdentifier,methodIdentifier);
		return methodIdentifier;
	}
}
