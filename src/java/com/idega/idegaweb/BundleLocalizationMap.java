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
 * Last modified: $Date: 2009/01/05 10:27:32 $ by $Author: anton $<br/>
 *
 * @author <a href="mailto:tryggvil@idega.com">Tryggvi Larusson</a>
 * @version $Revision: 1.8 $
 */
public class BundleLocalizationMap implements Map<String, String> {

	private IWBundle bundle;
	private List<String> values;

	public BundleLocalizationMap(IWBundle bundle) {
		this.bundle = bundle;
	}

	public String get(Object key) {
		return getIWMainAppliction().getLocalizedStringMessage(String.valueOf(key), String.valueOf(key), bundle.getBundleIdentifier());
	}

	private IWMainApplication getIWMainAppliction() {
		return IWMainApplication.getDefaultIWMainApplication();
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

	public ResourceBundle getResourceBundle() {
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

	public Collection<String> values() {
		if (this.values == null) {
			this.values = new ArrayList<String>();
			for (Enumeration<String> enumer = getResourceBundle().getKeys(); enumer.hasMoreElements();) {
				String v = getResourceBundle().getString(enumer.nextElement());
				this.values.add(v);
			}
		}
		return this.values;
	}

	public int size() {
		return values().size();
	}

	public boolean containsValue(Object value) {
		return values().contains(value);
	}

	public Set<Map.Entry<String, String>> entrySet() {
		Set<Map.Entry<String, String>> set = new HashSet<Map.Entry<String, String>>();
		for (Enumeration<String> enumer = getResourceBundle().getKeys(); enumer.hasMoreElements();) {
			final String k = enumer.nextElement();
			set.add(new Map.Entry<String, String>() {
				public String getKey() {
					return k;
				}

				public String getValue() {
					return (String) getResourceBundle().getObject(k);
				}

				public String setValue(String value) {
					throw new UnsupportedOperationException(this.getClass().getName() + " UnsupportedOperationException");
				}
			});
		}
		return set;
	}

	public Set<String> keySet() {
		Set<String> set = new HashSet<String>();
		for (Enumeration<String> enumer = getResourceBundle().getKeys(); enumer.hasMoreElements();) {
			set.add(enumer.nextElement());
		}
		return set;
	}

	public String remove(Object key) {
		throw new UnsupportedOperationException(this.getClass().getName() + " UnsupportedOperationException");
	}

	public void putAll(Map<? extends String, ? extends String> t) {
		throw new UnsupportedOperationException(this.getClass().getName() + " UnsupportedOperationException");
	}

	public String put(String key, String value) {
		String oldValue = get(key);
		((IWResourceBundle) getResourceBundle()).setLocalizedString(key, value);
		return oldValue;
	}

	public void clear() {
		throw new UnsupportedOperationException(this.getClass().getName() + " UnsupportedOperationException");
	}
}