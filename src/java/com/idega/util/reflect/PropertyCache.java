/*
 * $Id: PropertyCache.java,v 1.4.2.1 2007/01/12 19:33:01 idegaweb Exp $
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
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import com.idega.idegaweb.IWMainApplication;
import com.idega.repository.data.Singleton;


/**
 * This class holds a cache of lists of Property object keyed by an id (ICObjectInstanceId).
 * 
 *  Last modified: $Date: 2007/01/12 19:33:01 $ by $Author: idegaweb $
 * 
 * @author <a href="mailto:tryggvil@idega.com">tryggvil</a>
 * @version $Revision: 1.4.2.1 $
 */
public class PropertyCache implements Singleton {
	
	//private PropertyCache instance�
	private Map propertyListCache;
	private static String appKey =  "IW_PROPERTYCACHE";
	
	private PropertyCache(){}
	
	public static synchronized PropertyCache getInstance(){
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
		if(this.propertyListCache==null){
			this.propertyListCache=new HashMap();
		}
		return this.propertyListCache;
	}
	
	public List getPropertyList(String key){
		List l = (List)getPropertyListCache().get(key);
		if(l==null){
			l = new ArrayList();
			getPropertyListCache().put(key,l);
		}
		return l;
	}
	
	public void setAllCachedPropertiesOnInstance(String key, Object instance){
		List props = getPropertyList(key);
		for (Iterator iter = props.iterator(); iter.hasNext();) {
			Property prop = (Property) iter.next();
			prop.setPropertyOnInstance(instance);
		}
	}
	
	public void addProperty(String key,Property property){
		getPropertyList(key).add(property);
	}
	
	public void clearPropertiesForKey(String key){
		getPropertyListCache().remove(key);
	}
	
	
}
