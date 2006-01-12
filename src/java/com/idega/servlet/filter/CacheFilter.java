/*
 * $Id: CacheFilter.java,v 1.3 2006/01/12 17:04:29 tryggvil Exp $
 * Created on 7.1.2005
 *
 * Copyright (C) 2005 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.servlet.filter;

import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheException;
import net.sf.ehcache.CacheManager;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import com.idega.core.accesscontrol.business.LoginBusinessBean;
import com.idega.core.cache.filter.SimplePageCachingFilter;
import com.idega.idegaweb.IWMainApplication;

/**
 * <p>
 * Filter that can be enabled to generically cache of output from all .jsps or Servlets.<br>
 * The rule is that this filter when enabled caches output of all GET requests when the user
 * is not authenticated into the idegaWeb user system.
 * </p>
 * Last modified: $Date: 2006/01/12 17:04:29 $ by $Author: tryggvil $
 * 
 * @author <a href="mailto:tryggvi@idega.com">Tryggvi Larusson</a>
 * @version $Revision: 1.3 $
 */
public class CacheFilter extends SimplePageCachingFilter {
	
	private static final Log log = LogFactory.getLog(CacheFilter.class.getName());

	public static final int DEFAULT_CACHE_SIZE=1000;
	public static final long DEFAULT_CACHE_TIME_SECONDS=60*20;
	public static final long DEFAULT_CACHE_TIME_IDLE_SECONDS=60*20;
	public static final String DEFAULT_CACHE_NAME="CacheFilter";
	
	public static boolean defaultEnabled = false;
	private static final String METHOD_GET="GET";
	public static final String PROPERTY_CACHE_FILTER_ENABLED = "CACHE_FILTER_ENABLED";
	public static final String PROPERTY_CACHE_FILTER_SIZE = "CACHE_FILTER_SIZE";
	public static final String PROPERTY_CACHE_FILTER_TTL = "CACHE_FILTER_TTL";
	public static final String PROPERTY_CACHE_FILTER_IDLE_TTL = "CACHE_FILTER_IDLE_TTL";
	private static boolean INITALIZED=false;
	/*
	 * (non-Javadoc)
	 * 
	 * @see net.sf.ehcache.constructs.web.filter.CachingFilter#doFilter(javax.servlet.http.HttpServletRequest,
	 *      javax.servlet.http.HttpServletResponse, javax.servlet.FilterChain)
	 */
	public void doFilter(ServletRequest sRequest, ServletResponse sResponse, FilterChain chain) throws ServletException {
		HttpServletRequest request = (HttpServletRequest)sRequest;
		HttpServletResponse response = (HttpServletResponse)sResponse;
		
		if(cacheRequest(request,response)){
			super.doFilter(sRequest, sResponse, chain);
		}
		else{
			try {
				chain.doFilter(sRequest,sResponse);
			}
			catch (IOException e) {
				throw new ServletException(e);
			}
		}
	}

	
    /**
     * Initialises blockingCache to use
     *
     * @throws CacheException The most likely cause is that a cache has not been
     *                        configured in ehcache's configuration file ehcache.xml for the filter name
     */
    public void doInit() throws CacheException {
    		//overriding from superclass

		int cacheSize = DEFAULT_CACHE_SIZE;
		long cacheTTLSeconds = DEFAULT_CACHE_TIME_SECONDS;
		long cacheTTLIdleSeconds = DEFAULT_CACHE_TIME_IDLE_SECONDS;
		
		FilterConfig config = this.getFilterConfig();
		ServletContext context = config.getServletContext();
		IWMainApplication iwma = IWMainApplication.getIWMainApplication(context);
		
		try{
			String propEnabled = iwma.getSettings().getProperty(PROPERTY_CACHE_FILTER_ENABLED);
			defaultEnabled = Boolean.valueOf(propEnabled).booleanValue();
			if(defaultEnabled){
				log.info("CacheFilter is enabled");
				System.out.println("CacheFilter is enabled");
			}
			else{
				log.info("CacheFilter is disabled");
				System.out.println("CacheFilter is disabled");
			}
		}
		catch(Exception e){}
		try{
			String propCacheSize = iwma.getSettings().getProperty(PROPERTY_CACHE_FILTER_SIZE);
			cacheSize = Integer.parseInt(propCacheSize);
		}
		catch(Exception e){}
		try{
			String propTTL = iwma.getSettings().getProperty(PROPERTY_CACHE_FILTER_TTL);
			cacheTTLSeconds = Long.parseLong(propTTL);
		}
		catch(Exception e){}
		try{
			String propIdleTTL = iwma.getSettings().getProperty(PROPERTY_CACHE_FILTER_IDLE_TTL);
			cacheTTLIdleSeconds = Long.parseLong(propIdleTTL);
		}
		catch(Exception e){}

		final String cacheName = getCacheName();

    		CacheManager cm = CacheManager.create();
    		cm.removeCache(cacheName);
    		Cache cache = new Cache(cacheName, cacheSize, true, false, cacheTTLSeconds, cacheTTLIdleSeconds);
    		cm.addCache(cache);
    		
    		
    		super.doInit();
    		INITALIZED=true;
    }
    
    protected void checkInitialization(){
    		if(!INITALIZED){
    			try {
    					synchronized(this){
    						if(!INITALIZED){
    							doInit();
    						}
    					}
				}
				catch (CacheException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
    		}
    }
    
    /**
     * <p>
     * Tells the CacheFilter to reload its settings.
     * </p>
     */
    public static void reload(){
    		INITALIZED=false;
    }
    
	/**
	 * <p>
	 * TODO tryggvil describe method cacheRequest
	 * </p>
	 * @param request
	 * @param response
	 * @return
	 */
	protected boolean cacheRequest(HttpServletRequest request, HttpServletResponse response) {
		checkInitialization();
		if(defaultEnabled){
			String method = request.getMethod();
			if(method.equals(METHOD_GET)){
				LoginBusinessBean loginBusiness = LoginBusinessBean.getLoginBusinessBean(request);
				if(loginBusiness.isLoggedOn(request)){
					//Never cache for a logged on user:
					return false;
				}
				return true;
			}
		}
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.sf.ehcache.constructs.web.filter.CachingFilter#getCacheName()
	 */
	protected String getCacheName() {
		return DEFAULT_CACHE_NAME;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.sf.ehcache.constructs.web.filter.CachingFilter#calculateKey(javax.servlet.http.HttpServletRequest)
	 */
	protected String calculateKey(HttpServletRequest request) {
		return super.calculateKey(request);
	}
}
