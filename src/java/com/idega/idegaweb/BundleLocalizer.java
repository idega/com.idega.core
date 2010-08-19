package com.idega.idegaweb;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import com.idega.core.business.DefaultSpringBean;
import com.idega.util.StringUtil;

@Service("localizedStrings")
@Scope(BeanDefinition.SCOPE_SINGLETON)
public class BundleLocalizer extends DefaultSpringBean implements Map<String, BundleLocalizationMap> {

	private Map<String, BundleLocalizationMap> bundles;

	public BundleLocalizer() {
		super();

		bundles = new HashMap<String, BundleLocalizationMap>();
	}

	public void addBundle(String bundleIdentifier, IWBundle bundle) {
		if (StringUtil.isEmpty(bundleIdentifier) || bundle == null) {
			return;
		}

		if (bundles.get(bundleIdentifier) != null) {
			return;
		}

		BundleLocalizationMap localizationMap = new BundleLocalizationMap(bundle);
		bundles.put(bundleIdentifier, localizationMap);
	}

	public int size() {
		return bundles.size();
	}

	public boolean isEmpty() {
		return bundles.isEmpty();
	}

	public boolean containsKey(Object key) {
		return bundles.containsKey(key);
	}

	public boolean containsValue(Object value) {
		return bundles.containsValue(value);
	}

	public BundleLocalizationMap get(Object key) {
		return bundles.get(key);
	}

	public BundleLocalizationMap put(String key, BundleLocalizationMap value) {
		return null;
	}

	public BundleLocalizationMap remove(Object key) {
		return null;
	}

	public void putAll(Map<? extends String, ? extends BundleLocalizationMap> m) {
	}

	public void clear() {
	}

	public Set<String> keySet() {
		return bundles.keySet();
	}

	public Collection<BundleLocalizationMap> values() {
		return bundles.values();
	}

	public Set<java.util.Map.Entry<String, BundleLocalizationMap>> entrySet() {
		return bundles.entrySet();
	}
}