/*
 * $Id: PropertyCache.java,v 1.5 2006/04/09 12:13:19 laddi Exp $
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
import com.idega.util.ListUtil;


/**
 * This class holds a cache of lists of Property object keyed by an id (ICObjectInstanceId).
 *
 *  Last modified: $Date: 2006/04/09 12:13:19 $ by $Author: laddi $
 *
 * @author <a href="mailto:tryggvil@idega.com">tryggvil</a>
 * @version $Revision: 1.5 $
 */
public class PropertyCache implements Singleton {

	private Map<String, List<Property>> propertyListCache;
	private static String appKey =  "IW_PROPERTYCACHE";

	private PropertyCache(){}

	public static PropertyCache getInstance(){
		IWMainApplication iwma = IWMainApplication.getDefaultIWMainApplication();
		PropertyCache cache = (PropertyCache) iwma.getAttribute(appKey);
		if (cache == null) {
			cache = new PropertyCache();
			iwma.setAttribute(appKey,cache);
		}
		return cache;
	}

	private Map<String, List<Property>> getPropertyListCache(){
		if (this.propertyListCache == null) {
			this.propertyListCache = new HashMap<String, List<Property>>();
		}
		return this.propertyListCache;
	}

	public List<Property> getPropertyList(String key){
		List<Property> l = getPropertyListCache().get(key);
		if(l==null){
			l = new ArrayList<Property>();
			getPropertyListCache().put(key,l);
		}
		return l;
	}

	public void setAllCachedPropertiesOnInstance(String key, Object instance) {
		List<Property> props = getPropertyList(key);
		if (ListUtil.isEmpty(props)) {
			return;
		}

		for (Iterator<Property> iter = props.iterator(); iter.hasNext();) {
			Property prop = iter.next();
			if (prop == null) {
				continue;
			}

			prop.setPropertyOnInstance(instance);
		}
	}

	public void addProperty(String key, Property property) {
		getPropertyList(key).add(property);
	}

	public void clearPropertiesForKey(String key) {
		getPropertyListCache().remove(key);
	}

}
