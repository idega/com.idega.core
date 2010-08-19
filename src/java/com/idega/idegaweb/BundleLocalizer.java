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

	@Override
	public int size() {
		return bundles.size();
	}

	@Override
	public boolean isEmpty() {
		return bundles.isEmpty();
	}

	@Override
	public boolean containsKey(Object key) {
		return bundles.containsKey(key);
	}

	@Override
	public boolean containsValue(Object value) {
		return bundles.containsValue(value);
	}

	@Override
	public BundleLocalizationMap get(Object key) {
		return bundles.get(key);
	}

	@Override
	public BundleLocalizationMap put(String key, BundleLocalizationMap value) {
		return null;
	}

	@Override
	public BundleLocalizationMap remove(Object key) {
		return null;
	}

	@Override
	public void putAll(Map<? extends String, ? extends BundleLocalizationMap> m) {
	}

	@Override
	public void clear() {
	}

	@Override
	public Set<String> keySet() {
		return bundles.keySet();
	}

	@Override
	public Collection<BundleLocalizationMap> values() {
		return bundles.values();
	}

	@Override
	public Set<java.util.Map.Entry<String, BundleLocalizationMap>> entrySet() {
		return bundles.entrySet();
	}
}