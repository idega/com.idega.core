package com.idega.util.messages;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.naming.OperationNotSupportedException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import com.idega.core.cache.IWCacheManager2;
import com.idega.idegaweb.IWBundle;
import com.idega.idegaweb.IWMainApplication;
import com.idega.util.ListUtil;
import com.idega.util.StringUtil;
import com.idega.util.expression.ELUtil;

/**
 *
 *
 * @author <a href="anton@idega.com">Anton Makarov</a>
 * @version Revision: 1.0
 *
 * Last modified: Oct 16, 2008 by Author: Anton
 *
 */
@Service
@Scope(BeanDefinition.SCOPE_SINGLETON)
public class MessageResourceFactoryImpl implements MessageResourceFactory {

	@Autowired
	private List<MessageResource> uninitializedMessageResources;

	private static final Logger LOGGER = Logger.getLogger(MessageResourceFactoryImpl.class.getName());

	private static final String CACHED_RESOURCES = "cached_resources";

	private List<MessageResource> getInitializedResourceList(Locale locale, String bundleIdentifier) {
		if (bundleIdentifier == null) {
			bundleIdentifier = MessageResource.NO_BUNDLE;
		}

		Map<String, Map<Locale, Map<String, MessageResource>>> cachedResources = getCache();

		Map<Locale, Map<String, MessageResource>> bundleResources = cachedResources.get(bundleIdentifier);
		if (bundleResources == null) {
			bundleResources = new ConcurrentHashMap<Locale, Map<String, MessageResource>>();
			cachedResources.put(bundleIdentifier, bundleResources);
		}

		if (bundleResources.containsKey(locale)) {
			Map<String, MessageResource> resourcesMap = bundleResources.get(locale);
			List<MessageResource> resources = new ArrayList<MessageResource>(resourcesMap.values());
			sortResourcesByImportance(resources);
			return resources;
		}

		//	There is no bundle with specified bundleIdentifier and locale in cache
		List<MessageResource> uninitializedResources = getUninitializedMessageResources();
		if (ListUtil.isEmpty(uninitializedResources)) {
			return uninitializedResources;
		}

		Map<String, MessageResource> resourcesMap = new ConcurrentHashMap<String, MessageResource>();
		List<MessageResource> failedInitializationResources = new ArrayList<MessageResource>();
		for (Iterator<MessageResource> resourcesIter = uninitializedResources.iterator(); resourcesIter.hasNext();) {
			MessageResource resource = resourcesIter.next();
			try {
				long lastModified = -1;
				IWBundle bundle = getIWMainApplication().getBundle(bundleIdentifier);
				if (bundle != null) {
					lastModified = bundle.getResourceTime("resources/" + locale + ".locale/Localized.strings");
				}

				resource.initialize(bundleIdentifier, locale, lastModified);
				resourcesMap.put(resource.getIdentifier(), resource);
			} catch (IOException e) {
				LOGGER.log(Level.SEVERE, "Error initializing bundle: " + bundleIdentifier, e);
				failedInitializationResources.add(resource);
			} catch (OperationNotSupportedException e) {
				failedInitializationResources.add(resource);
			}
		}

		for (Iterator<MessageResource> resourcesIter = failedInitializationResources.iterator(); resourcesIter.hasNext();) {
			uninitializedResources.remove(resourcesIter.next());
		}

		bundleResources.put(locale, resourcesMap);
		List<MessageResource> resources = new ArrayList<MessageResource>(resourcesMap.values());
		sortResourcesByImportance(resources);
		return resources;
	}

	@Override
	public void addInitializedMessageResource(MessageResource resource, String bundleIdentifier, Locale locale) {
		List<MessageResource> cachedResources = getInitializedResourceList(locale, bundleIdentifier);
		if (!cachedResources.contains(resource)) {
			cachedResources.add(resource);
		}
	}

	/**
	 * Gets localized message for the current locale
	 * @return String that was found in resource and/or set to it or valueIfNotFound String in case no messages were found
	 * 		   or auto inserted
	 */
	@Override
	public String getLocalizedMessage(String key, String valueIfNotFound, String bundleIdentifier, Locale locale) {
		List<MessageResource> resources = getInitializedResourceList(locale, bundleIdentifier);

		for (Iterator<MessageResource> resourcesIter = resources.iterator(); resourcesIter.hasNext();) {
			MessageResource resource = resourcesIter.next();
			if (!resource.getLevel().equals(MessageResourceImportanceLevel.OFF)) {
				String message = resource.getMessage(key, true);
				if (!StringUtil.isEmpty(message)) {
					return message;
				}
			}
		}

		if (valueIfNotFound == null) {
			return null;
		}

		//	Auto inserting message in case none of resources has it
		List<MessageResource> copies = new ArrayList<>(resources);
		for (Iterator<MessageResource> copiesOfResourcesIter = copies.iterator(); copiesOfResourcesIter.hasNext();) {
			MessageResource resource = copiesOfResourcesIter.next();
			if (resource.isAutoInsert()) {
				resource.setMessage(key, valueIfNotFound);
			}
		}

		return valueIfNotFound;
	}

	@Override
	public String setLocalizedMessage(String key, String value, String bundleIdentifier, Locale locale) {
		List<MessageResource> resources = getInitializedResourceList(locale, bundleIdentifier);
		for (Iterator<MessageResource> resourcesIter = resources.iterator(); resourcesIter.hasNext();) {
			MessageResource resource = resourcesIter.next();
			resource.setMessage(key, value);
		}
		return value;
	}

	@Override
	public void setLocalizedMessages(Map<String, String> values, String bundleIdentifier, Locale locale) {
		List<MessageResource> resources = getInitializedResourceList(locale, bundleIdentifier);
		for (Iterator<MessageResource> resourcesIter = resources.iterator(); resourcesIter.hasNext();) {
			MessageResource resource = resourcesIter.next();
			resource.setMessages(values);
		}
	}

	@Override
	public Map<String, String> setLocalizedMessageToAutoInsertRes(String key, String value, String bundleIdentifier, Locale locale) {
		List<MessageResource> resources = getInitializedResourceList(locale, bundleIdentifier);
		Map<String, String> setMessages = new ConcurrentHashMap<String, String>(resources.size());

		for (Iterator<MessageResource> resourcesIter = resources.iterator(); resourcesIter.hasNext();) {
			MessageResource resource = resourcesIter.next();
			if (resource.isAutoInsert()) {
				String setValue = resource.setMessage(key, value);
				resource.store();

				if (setValue != null) {
					setMessages.put(resource.getIdentifier(), setValue);
				}
			}
		}
		return setMessages;
	}

	@Override
	public void removeLocalizedMessageFromAutoInsertRes(String key, String bundleIdentifier, Locale locale) {
		List<MessageResource> resources = getInitializedResourceList(locale, bundleIdentifier);
		for (Iterator<MessageResource> resourcesIter = resources.iterator(); resourcesIter.hasNext();) {
			MessageResource resource = resourcesIter.next();
			if (resource.isAutoInsert()) {
				resource.removeMessage(key);
			}
		}
	}

	private void sortResourcesByImportance(List<MessageResource> resources) {
		ResourceComparatorByLevel resourceComparatorByLevel = new ResourceComparatorByLevel();
		Collections.sort(resources, resourceComparatorByLevel);
	}

	@Override
	public MessageResource getResource(String identifier, String bundleIdentifier, Locale locale) {
		List<MessageResource> resources = getInitializedResourceList(locale, bundleIdentifier);
		for (Iterator<MessageResource> resourcesIter = resources.iterator(); resourcesIter.hasNext();) {
			MessageResource resource = resourcesIter.next();
			if (identifier.equals(resource.getIdentifier())) {
				return resource;
			}
		}
		return null;
	}

	@Override
	public List<MessageResource> getResourceListByStorageIdentifier(String storageIdentifier) {
		if (StringUtil.isEmpty(storageIdentifier)) {
			LOGGER.warning("Storage identifier is not defined!");
			return Collections.emptyList();
		}

		Map<String, Map<Locale, Map<String, MessageResource>>> cachedResources = getCache();

		List<MessageResource> storageResources = new ArrayList<MessageResource>();
		for (Iterator<String> keysIter = cachedResources.keySet().iterator(); keysIter.hasNext();) {
			String bundleIdentifier = keysIter.next();
			Map<Locale, Map<String, MessageResource>> bundleResources = cachedResources.get(bundleIdentifier);
			if (bundleResources == null) {
				bundleResources = new ConcurrentHashMap<Locale, Map<String, MessageResource>>();
				cachedResources.put(bundleIdentifier, bundleResources);
			}

			for (Iterator<Locale> localesIter = bundleResources.keySet().iterator(); localesIter.hasNext();) {
				Locale locale = localesIter.next();
				Map<String, MessageResource> resources = bundleResources.get(locale);
				if (resources == null) {
					resources = new ConcurrentHashMap<String, MessageResource>();
					bundleResources.put(locale, resources);
				}

				for (Iterator<MessageResource> resourcesIter = resources.values().iterator(); resourcesIter.hasNext();) {
					MessageResource resource = resourcesIter.next();
					if (storageIdentifier.equals(resource.getIdentifier())) {
						storageResources.add(resource);
					}
				}
			}
		}

		return storageResources;
	}

	@Override
	public List<MessageResource> getResourceListByBundleAndLocale(String bundleIdentifier, Locale locale) {
		return getInitializedResourceList(locale, bundleIdentifier);
	}

	private IWMainApplication getIWMainApplication() {
		return IWMainApplication.getDefaultIWMainApplication();
	}

	private List<MessageResource> getUninitializedMessageResources() {
		ELUtil.getInstance().autowire(this);
		return uninitializedMessageResources;
	}

	@Override
	public List<MessageResource> getAvailableUninitializedMessageResources() {
		List<MessageResource> allUninitializedResources = getUninitializedMessageResources();

		List<MessageResource> resourcesToRemove = new ArrayList<MessageResource>();
		for (Iterator<MessageResource> resourcesIter = allUninitializedResources.iterator(); resourcesIter.hasNext();) {
			MessageResource resource = resourcesIter.next();
			List<MessageResource> availableResourcesOfType = getResourceListByStorageIdentifier(resource.getIdentifier());

			if (availableResourcesOfType.isEmpty()) {
				resourcesToRemove.add(resource);
			}
		}
		for (Iterator<MessageResource> resourcesIter = resourcesToRemove.iterator(); resourcesIter.hasNext();) {
			MessageResource resource = resourcesIter.next();
			allUninitializedResources.remove(resource);
		}

		return allUninitializedResources;
	}

	protected class ResourceComparatorByLevel implements Comparator<MessageResource> {
		private static final int LESS = -1;
		private static final int EQUAL = 0;
		private static final int GREATER = 1;

		@Override
		public int compare(MessageResource msg1, MessageResource msg2) {
			if (msg1.getLevel().intValue() > msg2.getLevel().intValue()) {
				return LESS;
			} else if (msg1.getLevel().intValue() < msg2.getLevel().intValue()) {
				return GREATER;
			} else {
				return EQUAL;
			}
		}
	}

	private Integer maxCacheSize = null;
	private Integer getMaxCacheSize() {
		if (maxCacheSize != null) {
			return maxCacheSize;
		}

		maxCacheSize = Locale.getAvailableLocales().length;
		return maxCacheSize;
	}

	private Map<String, Map<Locale, Map<String, MessageResource>>> getCache() {
		long time = Long.MAX_VALUE;
		return IWCacheManager2.getInstance(getIWMainApplication()).getCache(CACHED_RESOURCES, getMaxCacheSize(), false, true, time, time);
	}

}