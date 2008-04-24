/**
 * $Id: ICDomainLookup.java,v 1.2 2008/04/24 23:36:51 laddi Exp $
 * Created in 2007 by tryggvil
 *
 * Copyright (C) 2000-2007 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.core.builder.business;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.idega.core.builder.data.CachedDomain;
import com.idega.core.builder.data.ICDomain;
import com.idega.core.builder.data.ICDomainHome;
import com.idega.data.IDOLookup;

/**
 * <p>
 * Lookup Registry or cache of ICDomains that are available to the idegaWeb Application.
 * </p>
 *  Last modified: $Date: 2008/04/24 23:36:51 $ by $Author: laddi $
 * 
 * @author <a href="mailto:tryggvil@idega.com">tryggvil</a>
 * @version $Revision: 1.2 $
 */
public class ICDomainLookup {

	private static final String DEFAULT_DOMAIN_KEY = "default";
	private static ICDomainLookup instance;
	
	public static ICDomainLookup getInstance(){
		if(instance==null){
			instance = new ICDomainLookup();
		}
		return instance;
	}

	private Map<String,ICDomain> domainMap = new HashMap<String,ICDomain>();
	private Map<String,CachedDomain>cachedDomainMap = new HashMap<String,CachedDomain>();

	/**
	 * @see com.idega.idegaweb.IWApplicationContext#getDomain()
	 */	
	public ICDomain getDefaultDomain(){
		return getDomainByServerName(null);
	}
	
	public ICDomain getDomainByRequest(HttpServletRequest request){
		String serverName = request.getServerName();
		return getDomainByServerName(serverName);
	}
	
	public ICDomain getDomainByServerName(String serverName) {
		String cacheKey = serverName;
		if(serverName==null){
			cacheKey=DEFAULT_DOMAIN_KEY;
		}
		
		ICDomain domain = cachedDomainMap.get(cacheKey);
		if(domain==null){
			ICDomain realDomain = getPersistentDomainByServerName(serverName);
			
			if(realDomain.isDefaultDomain()){
				//This if/else clause is so that we will always get the same CachedDomain instance for the default domain:
				if(cachedDomainMap.containsKey(DEFAULT_DOMAIN_KEY)){
					domain = cachedDomainMap.get(DEFAULT_DOMAIN_KEY);
				}
				else{
					CachedDomain cachedDomain = new CachedDomain(realDomain);
					cachedDomainMap.put(DEFAULT_DOMAIN_KEY, cachedDomain);
					domain = cachedDomain;
				}
				
			}
			else{
				CachedDomain cachedDomain = new CachedDomain(realDomain);
				cachedDomainMap.put(cacheKey, cachedDomain);
				domain = cachedDomain;
			}
		}
		return domain;
	}

	public ICDomain getPersistentDomainByServerName(String serverName) {
		String mapKey = serverName;
		if(serverName==null){
			mapKey=DEFAULT_DOMAIN_KEY;
		}
		
		ICDomain domain = this.domainMap.get(mapKey);
		if(domain==null){
			ICDomainHome domainHome;
			try {
				domainHome = (ICDomainHome)IDOLookup.getHome(ICDomain.class);
				domain = domainHome.findDomainByServernameOrDefault(serverName);

				this.domainMap.put(mapKey,domain);
				if(domain.isDefaultDomain()){
					this.domainMap.put(DEFAULT_DOMAIN_KEY, domain);
				}
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
		return domain;	
	}	
}