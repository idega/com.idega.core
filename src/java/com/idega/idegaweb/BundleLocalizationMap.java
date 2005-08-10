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

/**
 * <p>
 * This class is a Map representation of an IWResourceBundle that can be used as value bindings in JSF to the idegaWeb
 * Bundle and localization system.<br/>
 * The notation is #{localizedStrings['BUNDLE_IDENTIFIER']['LOCALIZATION_KEY']}, example:
 * #{localizedStrings['com.idega.manager']['store']}
 * </p>
 * Last modified: $Date: 2005/08/10 18:35:27 $ by $Author: tryggvil $<br/>
 * 
 * @author <a href="mailto:tryggvil@idega.com">Tryggvi Larusson</a>
 * @version $Revision: 1.5 $
 */
public class BundleLocalizationMap implements Map {

	//private ResourceBundle _bundle;
	private IWBundle bundle;
	private List _values;

	public BundleLocalizationMap(IWBundle bundle) {
		this.bundle = bundle;
	}

	//Optimized methods

	/**
	 * Gets a value for a localized key in the idegaWeb bundle for this Map and the current (JSF) Locale.
	 */
	public Object get(Object key) {
		try{
			return getResourceBundle().getObject(key.toString());
		}
		catch(MissingResourceException msre){
			//System.err.println(msre.getMessage());
			//return null;
			return handleKeyNotFound((String)key);
			
		}
	}
	/**
	 * <p>
	 * Block that handles if the key is not found in the resourcebundle:
	 * </p>
	 * @param key
	 * @return
	 */
	protected String handleKeyNotFound(String key){
		IWResourceBundle iwrb  = getIWResourceBundle();
		//Set the default application locale to be English
		Locale defaultLocale = Locale.ENGLISH;
		if( !iwrb.getLocale().equals(defaultLocale)){
			//this block is not gone into of this resourcebundle is the default (english) bundle
			iwrb = iwrb.getIWBundleParent().getResourceBundle(defaultLocale);
		}
		//set the default value as the key and auto create it for the english resourcebundle:
		return iwrb.getLocalizedString(key,key);
	}
	
	protected IWBundle getBundle() {
		return this.bundle;
	}
	
	private ResourceBundle getResourceBundle() {
		Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
		return getBundle().getResourceBundle(locale);
	}
	
	
	protected IWResourceBundle getIWResourceBundle(){
		return (IWResourceBundle)getResourceBundle();
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
		//throw new UnsupportedOperationException(this.getClass().getName() + " UnsupportedOperationException");
		Object oldValue = get(key);
		((IWResourceBundle) getResourceBundle()).setLocalizedString((String)key,(String)value);
		
		return oldValue;
	}

	public void clear() {
		throw new UnsupportedOperationException(this.getClass().getName() + " UnsupportedOperationException");
	}
}