/*
 * $Id: FacesUtil.java,v 1.2 2005/11/04 14:07:27 tryggvil Exp $
 * Created on 30.12.2004
 *
 * Copyright (C) 2004 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.util;

import javax.faces.context.FacesContext;


/**
 *  Utility class for various JavaServer Faces functions.
 * 
 *  Last modified: $Date: 2005/11/04 14:07:27 $ by $Author: tryggvil $
 * 
 * @author <a href="mailto:tryggvil@idega.com">tryggvil</a>
 * @version $Revision: 1.2 $
 */
public class FacesUtil {
	
	private static String SLASH="/";
	public static final String REQUEST_START="request_start_time";


	/**
	 * Fetches the request uri from the faces context. This method includes the context path of the servlet container.
	 * @param ctx
	 * @return
	 */
	public static String getRequestUri(FacesContext ctx) {
		return getRequestUri(ctx,true);
	}
	
	/**
	 * Fetches the request uri from the faces context.
	 * @param ctx
	 * @param includeContextPath if true then the contextPath will be included in the URI
	 * @return
	 */
	public static String getRequestUri(FacesContext ctx,boolean includeContextPath) {
		//HttpServletRequest request = (HttpServletRequest)ctx.getExternalContext().getRequest();
		String EMPTY_STRING="";
		//String contextPath = request.getContextPath();
		//String fullRequestUri = request.getRequestURI();
		String contextPath = ctx.getExternalContext().getRequestContextPath();
		String requestUri = ctx.getExternalContext().getRequestServletPath()+ctx.getExternalContext().getRequestPathInfo();
		
		if(contextPath.equals(SLASH) || contextPath.equals(EMPTY_STRING)){
			return requestUri;
		}
		else{
			//return fullRequestUri;
			if(includeContextPath){
				return contextPath+requestUri;
			}
			else{
				return requestUri;
			}
		}
	}
	
	/**
	 * <p>
	 * Registers the request start time in system time milliseconds and stores as a request variable.
	 * </p>
	 * @param context
	 * @return
	 */
	public static Long registerRequestBegin(FacesContext context){
		Long time = (Long) context.getExternalContext().getRequestMap().get(REQUEST_START);
		if(time==null){
			time = new Long(System.currentTimeMillis());
			context.getExternalContext().getRequestMap().put(REQUEST_START,time);
		}
		return time;
	}
	/**
	 * <p>
	 * Gets difference between the start time and request begin time and removes the request start time variable
	 * </p>
	 * @param context
	 * @return
	 */
	public static long registerRequestEnd(FacesContext context){
		Long time = (Long) context.getExternalContext().getRequestMap().get(REQUEST_START);
		if(time!=null){
			long endTime = System.currentTimeMillis();
			context.getExternalContext().getRequestMap().remove(REQUEST_START);
			return endTime-time.longValue();
		}
		return -1;
	}
}
