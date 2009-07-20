package com.idega.facelets.ui.util;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import javax.faces.context.FacesContext;

import com.idega.servlet.filter.IWBundleResourceFilter;

/**
 * simple hack for resolving iw resources, as el doesn't support parameters in method vbs.
 * 
 * @author <a href="civilis@idega.com">Vytautas Čivilis</a>
 * @version $Revision: 1.1 $
 *
 * Last modified: $Date: 2008/02/14 15:48:48 $ by $Author: civilis $
 *
 */
public class IWUIResource {
	
	IWUIResourceMap resMap;

	public Map<String, String> getRes() {
		
		if(resMap == null)
			resMap = new IWUIResourceMap();
		
		return resMap;
	}
	
	public class IWUIResourceMap implements Map<String, String> {

		public void clear() {
		}

		public boolean containsKey(Object obj) {
			return false;
		}

		public boolean containsValue(Object obj) {
			return false;
		}

		public Set<java.util.Map.Entry<String, String>> entrySet() {
			return null;
		}

		public String get(Object obj) {

			String resourceURI = String.valueOf(obj);
			IWBundleResourceFilter.checkCopyOfResourceToWebapp(FacesContext.getCurrentInstance(), resourceURI);
			
			return resourceURI;
		}

		public boolean isEmpty() {
			return false;
		}

		public Set<String> keySet() {
			return null;
		}

		public String put(String key, String value) {
			return null;
		}

		public void putAll(Map<? extends String, ? extends String> t) {
			
		}

		public String remove(Object obj) {
			return null;
		}

		public int size() {
			return 1;
		}

		public Collection<String> values() {
			return null;
		}
	}
}