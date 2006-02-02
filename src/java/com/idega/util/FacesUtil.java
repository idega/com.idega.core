/*
 * $Id: FacesUtil.java,v 1.6 2006/02/02 13:13:15 tryggvil Exp $
 * Created on 30.12.2004
 *
 * Copyright (C) 2004 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.util;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.el.ValueBinding;


/**
 * <p>
 *  Utility class for various JavaServer Faces functions.
 * </p>
 *  Last modified: $Date: 2006/02/02 13:13:15 $ by $Author: tryggvil $
 * 
 * @author <a href="mailto:tryggvil@idega.com">tryggvil</a>
 * @version $Revision: 1.6 $
 */
public class FacesUtil {
	
	private static String SLASH="/";
	public static final String REQUEST_START="request_start_time";
	public static final String SEPARATOR="-";
	

	public static final String EXPRESSION_BEGIN="#{";
	public static final String EXPRESSION_END="}";


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
		String requestPathInfo = ctx.getExternalContext().getRequestPathInfo();
		String requestUri = ctx.getExternalContext().getRequestServletPath();
		if(requestPathInfo==null){
			requestPathInfo=SLASH;
		}
		requestUri+=requestPathInfo;
		
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
	
	/**
	 * <p>
	 * Method to put an attibute in a Map that lives throughout the FacesContext instance.<br/>
	 * This actually uses the RequestMap but suffixes an id for the current FacesContext because
	 * there can be several FacesContext instances in the lifetime of one (HttpServlet)Request, 
	 * this happens for example in the case of Navigation-Rule, because that is implemented
	 * as server-side dispatches to a new page with the same Request object.
	 * </p>
	 * @param context
	 * @param key
	 * @param value
	 */
	public static void putAttribute(FacesContext context,String key,Object value){
		String newKey = getNewFacesContextKey(context,key);
		context.getExternalContext().getRequestMap().put(newKey,value);
	}
	/**
	 * <p>
	 * Method to get an attibute in a Map that lives throughout the FacesContext instance.<br/>
	 * This actually uses the RequestMap but suffixes an id for the current FacesContext because
	 * there can be several FacesContext instances in the lifetime of one (HttpServlet)Request, 
	 * this happens for example in the case of Navigation-Rule, because that is implemented
	 * as server-side dispatches to a new page with the same Request object.
	 * </p>
	 * @param context
	 * @param oldKey
	 * @return
	 */
	public static Object getAttribute(FacesContext context,String key){
		String newKey = getNewFacesContextKey(context,key);
		return context.getExternalContext().getRequestMap().get(newKey);
	}
	
	protected static String getNewFacesContextKey(FacesContext context,String oldKey){
		String facesContextId = context.toString();
		String newKey = oldKey;
		newKey += SEPARATOR;
		newKey+=facesContextId;
		return newKey;
	}
	
	
	/**
	 * <p>
	 * Returns the "nearest" parent of UIComponent component of type ofType if it exists.
	 * If none is found it returns null.
	 * </p>
	 * @param component
	 * @param ofType
	 * @return
	 */
	public static UIComponent getFirstParentOfType(UIComponent component,Class ofType){
		if(component!=null){
			UIComponent parent = component.getParent();
			while(parent!=null){
				if(parent.getClass().isAssignableFrom(ofType)){
					return parent;
				}
				parent=parent.getParent();
			}
		}
		return null;
	}

    /**
     * <p>
     * This method finds a bean instance from a given beanId.<br/>
     * Goes first to request scope, then to session scope and finally to application scope
     * and returns the first found.
     * </p>
     * @param beanId
     * @return
     */
    public static Object getBeanInstance(String beanId) {
	    	FacesContext context = FacesContext.getCurrentInstance();
	    	Object bean = context.getExternalContext().getRequestMap().get(beanId);
		String expr= getExpression(beanId);
		ValueBinding vb = context.getApplication().createValueBinding(expr);
		bean = vb.getValue(context);
	    	return bean;
    }
    
    /**
     * <p>
     * Creates the expression syntax, i.e. wraps the beanReference String around with #{ and }
     * </p>
     * @param beanReference
     * @return
     */
    public static String getExpression(String beanReference){
    		return EXPRESSION_BEGIN+beanReference+EXPRESSION_END;
    }
    
	/**
	 * <p>
	 * A method to check if the passed String is a valuebinding expression,
	 * i.e. a string in the format '#{MyBeanId.myProperty}'
	 * </p>
	 * @param any String
	 * @return
	 */
    public static boolean isValueBinding(String value)
    {
        if (value == null) return false;
        
        int start = value.indexOf(EXPRESSION_BEGIN);
        if (start < 0) return false;
        
        int end = value.lastIndexOf('}');
        return (end >=0 && start < end);
    }
    
	
}