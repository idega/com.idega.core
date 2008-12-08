package com.idega.util.messages;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.naming.OperationNotSupportedException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import com.idega.core.cache.IWCacheManager2;
import com.idega.idegaweb.IWMainApplication;
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
public class MessageResourceFactoryImpl implements MessageResourceFactory, ApplicationListener {
	@Autowired private List<MessageResource> uninitializedMessageResources;
	
	private static final String CASHED_RESOURCES = "cashed_resources";
	
	@SuppressWarnings("unchecked")
	private List<MessageResource> getInitializedResourceList(Locale locale, String bundleIdentifier) {
		if(bundleIdentifier == null)
			bundleIdentifier = MessageResource.NO_BUNDLE;
		Map<String, Map<Locale, List<MessageResource>>> cashResources = getCache();
		
		Map<Locale, List<MessageResource>> bundleResources;
		if(cashResources.containsKey(bundleIdentifier)) {
			bundleResources = cashResources.get(bundleIdentifier);
		} else {
			bundleResources = new HashMap<Locale, List<MessageResource>>();
			cashResources.put(bundleIdentifier, bundleResources);
		}
		
		if(bundleResources.containsKey(locale)) {
			return bundleResources.get(locale);
		} else {
			List<MessageResource> resources = getUninitializedMessageResources();
			List<MessageResource> failedInitializationResources = new ArrayList<MessageResource>(resources.size());
			for(MessageResource resource : resources) {
				try {
					resource.initialize(bundleIdentifier, locale);
				} catch (IOException e) {
					e.printStackTrace();
					return null;
				} catch (OperationNotSupportedException e) {
					failedInitializationResources.add(resource);
				}
			}
			
			for(MessageResource resource : failedInitializationResources) {
				resources.remove(resource);
			}
			
			bundleResources.put(locale, resources);
			return resources;
		}
	}
	
	public void addInitializedMessageResource(MessageResource resource, String bundleIdentifier, Locale locale) {
		Map<String, Map<Locale, List<MessageResource>>> cashResources = getCache();
		cashResources.get(bundleIdentifier).get(locale).add(resource);
		sortResourcesByImportance();
	}
	
	/**
	 * Gets localised message for current locale
	 * @return object that was found in resource and/or set to it or valueIfNotFound object in case no messages were found 
	 * 		   or autoinserted
	 */
	public Object getLocalisedMessage(Object key, Object valueIfNotFound, String bundleIdentifier, Locale locale) {
		List<MessageResource> resources = getInitializedResourceList(locale, bundleIdentifier);

		for(MessageResource resource : resources) {
			if(!resource.getLevel().equals(MessageResourceImportanceLevel.OFF)) {
				Object message = resource.getMessage(key);
				if(message != null) {
					return message;
				}
			}
		}

		if(valueIfNotFound == null) {
			return null;
		}
		
		//Autoinserting message in case none of resources has it
		for(MessageResource resource : resources) {
			if(resource.isAutoInsert()) {
				resource.setMessage(key, valueIfNotFound);
			}
		}
		return valueIfNotFound;
	}

	public Object setLocalisedMessage(Object key, Object value, String bundleIdentifier, Locale locale) {
		List<MessageResource> resources = getInitializedResourceList(locale, bundleIdentifier);
		for(MessageResource resource : resources) {
			resource.setMessage(key, value);
		}
		return value;
	}
	
	public void setLocalisedMessages(Map<Object, Object> values, String bundleIdentifier, Locale locale) {
		List<MessageResource> resources = getInitializedResourceList(locale, bundleIdentifier);
		for(MessageResource resource : resources) {
			resource.setMessages(values);
		}
	}
	
	public Map<String, Object> setLocalisedMessageToAutoInsertRes(Object key, Object value, String bundleIdentifier, Locale locale) {
		List<MessageResource> resources = getInitializedResourceList(locale, bundleIdentifier);
		Map<String, Object> setMessages = new HashMap<String, Object>(resources.size());
		
		for(MessageResource resource : resources) {
			if(resource.isAutoInsert()) {
				Object setValue = resource.setMessage(key, value);
				
				if(setValue != null) {
					setMessages.put(resource.getIdentifier(), setValue);
				}
			}
		}
		return setMessages;
	}
	
	public void removeLocalisedMessageFromAutoInsertRes(Object key, String bundleIdentifier, Locale locale) {
		List<MessageResource> resources = getInitializedResourceList(locale, bundleIdentifier);
		for(MessageResource resource : resources) {
			if(resource.isAutoInsert()) {
				resource.removeMessage(key);
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	private void sortResourcesByImportance() {
		ResourceComparatorByLevel resourceComparatorByLevel = new ResourceComparatorByLevel();
		
		Map<String, Map<Locale, List<MessageResource>>> cashResources = getCache();
		
		for(String bundleIdentifier : cashResources.keySet()) {
			Map<Locale, List<MessageResource>> bundleResources = cashResources.get(bundleIdentifier);
			for(Locale locale : bundleResources.keySet()) {
				List<MessageResource> resources = bundleResources.get(locale);
				Collections.sort(resources, resourceComparatorByLevel);
			}			
		}
	}	

	public MessageResource getResource(String identifier, String bundleIdentifier, Locale locale) {
		for(MessageResource resource : getInitializedResourceList(locale, bundleIdentifier)) {
			if(resource.getIdentifier().equals(identifier)) {
				return resource;
			}
		}
		return null;
	}
	
	@SuppressWarnings("unchecked")
	public List<MessageResource> getResourceListByStorageIdentifier(String storageIdentifier) {
		Map<String, Map<Locale, List<MessageResource>>> cashResources = getCache();
		
		List<MessageResource> storageResources = new ArrayList<MessageResource>();
		for(String bundleIdentifier : cashResources.keySet()) {
			
			for(Locale locale : cashResources.get(bundleIdentifier).keySet()) {
				
				for(MessageResource resource : cashResources.get(bundleIdentifier).get(locale)) {
					if(resource.getIdentifier().equals(storageIdentifier)) {
						storageResources.add(resource);
					}
				}
				
			}
			
		}
		return storageResources;
	}
	
	public List<MessageResource> getResourceListByBundleAndLocale(String bundleIdentifier, Locale locale) {
		return getInitializedResourceList(locale, bundleIdentifier);
	}

	public void onApplicationEvent(ApplicationEvent applicationEvent) {
		if(applicationEvent instanceof ResourceLevelChangeEvent) {
			sortResourcesByImportance();
		}
	}
	
	private IWMainApplication getIWMainApplication() {
		IWMainApplication iwma = IWMainApplication.getDefaultIWMainApplication();
		return iwma;
	}

	public List<MessageResource> getUninitializedMessageResources() {
		ELUtil.getInstance().autowire(this);
		return uninitializedMessageResources;
	}

	public void setUninitializedMessageResources(
			List<MessageResource> uninitializedMessageResources) {
		this.uninitializedMessageResources = uninitializedMessageResources;
	}
	
	protected class ResourceComparatorByLevel implements Comparator<MessageResource> {
		private static final int LESS = -1;
		private static final int EQUAL = 0;
		private static final int GREATER = 1;
		
		public int compare(MessageResource arg0, MessageResource arg1) {
			
			if(arg0.getLevel().intValue() > arg1.getLevel().intValue()) {
				return LESS;
			} else if(arg0.getLevel().intValue() < arg1.getLevel().intValue()) {
				return GREATER;
			} else return EQUAL;
		}
	}
	
	@SuppressWarnings("unchecked")
	private Map<String, Map<Locale, List<MessageResource>>> getCache() {
		return IWCacheManager2.getInstance(getIWMainApplication()).getCache(CASHED_RESOURCES, 1000, true, true, 50, 10);							//.getCache(CASHED_RESOURCES);
	}
}
