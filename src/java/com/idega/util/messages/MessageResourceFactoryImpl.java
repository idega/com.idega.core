package com.idega.util.messages;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.naming.OperationNotSupportedException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import com.idega.core.cache.IWCacheManager2;
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
		if (bundleIdentifier == null)
			bundleIdentifier = MessageResource.NO_BUNDLE;

		Map<String, Map<Locale, List<MessageResource>>> cachedResources = getCache();

		Map<Locale, List<MessageResource>> bundleResources = cachedResources.get(bundleIdentifier);
		if (bundleResources == null) {
			bundleResources = new HashMap<Locale, List<MessageResource>>();
			cachedResources.put(bundleIdentifier, bundleResources);
		}

		if (bundleResources.containsKey(locale)) {
			List<MessageResource> resources = bundleResources.get(locale);
			sortResourcesByImportance(resources);
			return resources;
		}

		//	There is no bundle with specified bundleIdentifier and locale
		List<MessageResource> resources = getUninitializedMessageResources();
		if (ListUtil.isEmpty(resources))
			return resources;

		List<MessageResource> failedInitializationResources = new ArrayList<MessageResource>();
		List<MessageResource> localCopyOfResources = new ArrayList<MessageResource>(resources);
		for (MessageResource resource: localCopyOfResources) {
			try {
				resource.initialize(bundleIdentifier, locale);
			} catch (IOException e) {
				LOGGER.log(Level.SEVERE, "Error initializing bundle: " + bundleIdentifier, e);
				failedInitializationResources.add(resource);
			} catch (OperationNotSupportedException e) {
				failedInitializationResources.add(resource);
			}
		}

		for (MessageResource resource: failedInitializationResources)
			localCopyOfResources.remove(resource);

		bundleResources.put(locale, localCopyOfResources);
		sortResourcesByImportance(localCopyOfResources);
		return localCopyOfResources;
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

		for (MessageResource resource: resources) {
			if (!resource.getLevel().equals(MessageResourceImportanceLevel.OFF)) {
				String message = resource.getMessage(key);
				if (message != null && message.length() > 0) {
					return message;
				}
			}
		}

		if (valueIfNotFound == null)
			return null;

		//Auto inserting message in case none of resources has it
		for (MessageResource resource: resources) {
			if (resource.isAutoInsert()) {
				resource.setMessage(key, valueIfNotFound);
			}
		}

		return valueIfNotFound;
	}

	@Override
	public String setLocalizedMessage(String key, String value, String bundleIdentifier, Locale locale) {
		List<MessageResource> resources = getInitializedResourceList(locale, bundleIdentifier);
		for (MessageResource resource: resources) {
			resource.setMessage(key, value);
		}
		return value;
	}

	@Override
	public void setLocalizedMessages(Map<String, String> values, String bundleIdentifier, Locale locale) {
		List<MessageResource> resources = getInitializedResourceList(locale, bundleIdentifier);
		for (MessageResource resource: resources) {
			resource.setMessages(values);
		}
	}

	@Override
	public Map<String, String> setLocalizedMessageToAutoInsertRes(String key, String value, String bundleIdentifier, Locale locale) {
		List<MessageResource> resources = getInitializedResourceList(locale, bundleIdentifier);
		Map<String, String> setMessages = new HashMap<String, String>(resources.size());

		for (MessageResource resource: resources) {
			if (resource.isAutoInsert()) {
				String setValue = resource.setMessage(key, value);
				resource.store();

				if (setValue != null)
					setMessages.put(resource.getIdentifier(), setValue);
			}
		}
		return setMessages;
	}

	@Override
	public void removeLocalizedMessageFromAutoInsertRes(String key, String bundleIdentifier, Locale locale) {
		List<MessageResource> resources = getInitializedResourceList(locale, bundleIdentifier);
		for (MessageResource resource: resources) {
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
		for (MessageResource resource: getInitializedResourceList(locale, bundleIdentifier)) {
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

		Map<String, Map<Locale, List<MessageResource>>> cachedResources = getCache();

		List<MessageResource> storageResources = new ArrayList<MessageResource>();
		for (String bundleIdentifier: cachedResources.keySet()) {
			Map<Locale, List<MessageResource>> bundleResources = cachedResources.get(bundleIdentifier);
			if (bundleResources == null) {
				bundleResources = new HashMap<Locale, List<MessageResource>>();
				cachedResources.put(bundleIdentifier, bundleResources);
			}

			for (Locale locale: bundleResources.keySet()) {
				List<MessageResource> localizedMessages = bundleResources.get(locale);
				if (localizedMessages == null) {
					localizedMessages = new ArrayList<MessageResource>();
					bundleResources.put(locale, localizedMessages);
				}

				for (MessageResource resource: localizedMessages) {
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
		IWMainApplication iwma = IWMainApplication.getDefaultIWMainApplication();
		return iwma;
	}

	private List<MessageResource> getUninitializedMessageResources() {
		ELUtil.getInstance().autowire(this);
		return uninitializedMessageResources;
	}

	@Override
	public List<MessageResource> getAvailableUninitializedMessageResources() {
		List<MessageResource> allUninitializedResources = getUninitializedMessageResources();

		List<MessageResource> resourcesToRemove = new ArrayList<MessageResource>();
		for (MessageResource resource : allUninitializedResources) {
			List<MessageResource> availableResourcesOfType = getResourceListByStorageIdentifier(resource.getIdentifier());

			if (availableResourcesOfType.isEmpty()) {
				resourcesToRemove.add(resource);
			}
		}
		for (MessageResource resource: resourcesToRemove) {
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
			} else return EQUAL;
		}
	}

	private Map<String, Map<Locale, List<MessageResource>>> getCache() {
		return IWCacheManager2.getInstance(getIWMainApplication()).getCache(CACHED_RESOURCES, 1000, true, false, 50, 10);
	}
}