/*
 * $Id: PropertyCache.java,v 1.2 2005/02/01 18:05:14 thomas Exp $
 * Created on 27.12.2004
 *
 * Copyright (C) 2004 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.util.reflect;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.idega.idegaweb.IWMainApplication;
import com.idega.repository.data.Singleton;


/**
 * This class holds a cache of lists of Property object keyed by an id (ICObjectInstanceId).
 * 
 *  Last modified: $Date: 2005/02/01 18:05:14 $ by $Author: thomas $
 * 
 * @author <a href="mailto:tryggvil@idega.com">tryggvil</a>
 * @version $Revision: 1.2 $
 */
public class PropertyCache implements Singleton {
	
	//private PropertyCache instance¾
	private Map propertyListCache;
	private static String appKey =  "IW_PROPERTYCACHE";
	
	private PropertyCache(){}
	
	public static PropertyCache getInstance(){
		IWMainApplication iwma = IWMainApplication.getDefaultIWMainApplication();
		PropertyCache cache = (PropertyCache)iwma.getAttribute(appKey);
		if(cache==null){
			cache = new PropertyCache();
			iwma.setAttribute(appKey,cache);
		}
		return cache;
		//PropertyCache cache = iwma.get
	}
	
	private Map getPropertyListCache(){
		if(propertyListCache==null){
			propertyListCache=new HashMap();
		}
		return propertyListCache;
	}
	
	public List getPropertyList(String key){
		List l = (List)getPropertyListCache().get(key);
		if(l==null){
			l = new ArrayList();
			getPropertyListCache().put(key,l);
		}
		return l;
	}
	
	public void addProperty(String key,Property property){
		getPropertyList(key).add(property);
	}
	
	public void clearPropertiesForKey(String key){
		getPropertyListCache().remove(key);
	}
	
	
}
