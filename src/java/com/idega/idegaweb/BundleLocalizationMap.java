/*
 * Created on Dec 15, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.idega.idegaweb;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.Set;
import javax.faces.context.FacesContext;


class BundleLocalizationMap implements Map {

	//private ResourceBundle _bundle;
	private IWBundle bundle;
	private List _values;

	public BundleLocalizationMap(IWBundle bundle) {
		this.bundle = bundle;
	}

	//Optimized methods

	public Object get(Object key) {
		try{
			return getResourceBundle().getObject(key.toString());
		}
		catch(MissingResourceException msre){
			msre.printStackTrace();
			return "";
		}
	}

	private IWBundle getBundle() {
		return this.bundle;
	}
	
	private ResourceBundle getResourceBundle() {
		Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
		return getBundle().getResourceBundle(locale);
	}
	
	public boolean isEmpty() {
		return !getResourceBundle().getKeys().hasMoreElements();
	}

	public boolean containsKey(Object key) {
		return getResourceBundle().getObject(key.toString()) != null;
	}


	//Unoptimized methods

	public Collection values() {
		if (_values == null) {
			_values = new ArrayList();
			for (Enumeration enumer = getResourceBundle().getKeys(); enumer.hasMoreElements();) {
				String v = getResourceBundle().getString((String)enumer.nextElement());
				_values.add(v);
			}
		}
		return _values;
	}

	public int size() {
		return values().size();
	}

	public boolean containsValue(Object value) {
		return values().contains(value);
	}

	public Set entrySet() {
		Set set = new HashSet();
		for (Enumeration enumer = getResourceBundle().getKeys(); enumer.hasMoreElements();) {
			final String k = (String) enumer.nextElement();
			set.add(new Map.Entry() {
				public Object getKey() {
					return k;
				}

				public Object getValue() {
					return getResourceBundle().getObject(k);
				}

				public Object setValue(Object value) {
					throw new UnsupportedOperationException(this.getClass().getName() + " UnsupportedOperationException");
				}
			});
		}
		return set;
	}

	public Set keySet() {
		Set set = new HashSet();
		for (Enumeration enumer = getResourceBundle().getKeys(); enumer.hasMoreElements();) {
			set.add(enumer.nextElement());
		}
		return set;
	}


	//Unsupported methods

	public Object remove(Object key) {
		throw new UnsupportedOperationException(this.getClass().getName() + " UnsupportedOperationException");
	}

	public void putAll(Map t) {
		throw new UnsupportedOperationException(this.getClass().getName() + " UnsupportedOperationException");
	}

	public Object put(Object key, Object value) {
		throw new UnsupportedOperationException(this.getClass().getName() + " UnsupportedOperationException");
	}

	public void clear() {
		throw new UnsupportedOperationException(this.getClass().getName() + " UnsupportedOperationException");
	}
}