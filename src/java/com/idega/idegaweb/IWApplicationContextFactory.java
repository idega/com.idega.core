/**
 * $Id: IWApplicationContextFactory.java,v 1.1 2007/04/09 22:17:59 tryggvil Exp $
 * Created in 2007 by tryggvil
 *
 * Copyright (C) 2000-2007 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.idegaweb;

import javax.servlet.http.HttpServletRequest;

/**
 * <p>
 * This class is used to keep track of IWApplicationContext instances, attach, fetch and remove them
 * from a currently running request Thread.
 * </p>
 *  Last modified: $Date: 2007/04/09 22:17:59 $ by $Author: tryggvil $
 * 
 * @author <a href="mailto:tryggvil@idega.com">tryggvil</a>
 * @version $Revision: 1.1 $
 */
public class IWApplicationContextFactory {

	
	private static ThreadLocal<IWApplicationContext> local = new ThreadLocal<IWApplicationContext>(){
		protected IWApplicationContext initialValue(){
			return null;
		}
	};
	/**
	 * <p>
	 * Detects and sets the IWApplicationContext instance and associates it to the current Thread.<br/>
	 * This should live throughout the request processing (until removeCurrentIWApplicationContext() is called).<br/>
	 * These methods should called by the first ServletFilter in the Chain (which currently iw IWUrlRedirector).
	 * </p>
	 * @param request
	 */
	public static void setCurrentIWApplicationContext(HttpServletRequest request){
		IWApplicationContext iwc = getIWApplicationContextForRequest(request);
		local.set(iwc);
	}
	/**
	 * <p>
	 * Removed the set IWApplicationContext instance from the current Thread, that was prevously 
	 * allocated by setCurrentIWApplicationContext() method.<br/>
	 * These methods should called by the first ServletFilter in the Chain (which currently iw IWUrlRedirector).
	 * </p>
	 * @param request
	 */
	public static void removeCurrentIWApplicationContext(HttpServletRequest request){
		local.remove();
	}
	/**
	 * <p>
	 * Gets the set IWApplicationContext instance from the current Thread, that was prevously 
	 * allocated by setCurrentIWApplicationContext() method.<br/>
	 * </p>
	 * @param request
	 */
	public static IWApplicationContext getCurrentIWApplicationContext(){
		return local.get();
	}
	
	
	protected static IWApplicationContext getIWApplicationContextForRequest(HttpServletRequest request) {
		return IWMainApplication.getIWApplicationContext(request);
		
	}
}
