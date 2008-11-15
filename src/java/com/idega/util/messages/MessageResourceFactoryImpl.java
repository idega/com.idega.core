package com.idega.util.messages;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

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
	
	private List<MessageResource> resources;
	
	/**
	 * Gets localised message for specified locale
	 * @return object that was found in resource and/or set to it or valueIfNotFound object in case no messages were found 
	 * 		   or autoinserted
	 */
	public Object getLocalisedMessage(Object key, Object valueIfNotFound, String bundleIdentifier) {
		if(bundleIdentifier == null)
			bundleIdentifier = MessageResource.NO_BUNDLE;
		for(MessageResource resource : resources) {
			if(!resource.getLevel().equals(MessageResourceImportanceLevel.OFF)) {
				Object message = resource.getMessage(key, bundleIdentifier);
				if(message != null) {
					return message;
				}
			}
		}
		
		//Autoinserting message in case none of resources has it
		for(MessageResource resource : resources) {
			if(resource.isAutoInsert()) {
				resource.setMessage(key, valueIfNotFound, bundleIdentifier);
			}
		}
		return valueIfNotFound;
	}
	
	/**
	 * Gets localised message for specified locale
	 * @return object that was found in resource and/or set to it or valueIfNotFound object in case no messages were found 
	 * 		   or autoinserted
	 */
	public Object getLocalisedMessage(Object key, Object valueIfNotFound, String bundleIdentifier, Locale locale) {
		if(bundleIdentifier == null)
			bundleIdentifier = MessageResource.NO_BUNDLE;
		for(MessageResource resource : resources) {
			if(!resource.getLevel().equals(MessageResourceImportanceLevel.OFF)) {
				Object message = resource.getMessage(key, bundleIdentifier, locale);
				if(message != null) {
					return message;
				}
			}
		}
		//Autoinserting message in case none of resources has it
		for(MessageResource resource : resources) {
			if(resource.isAutoInsert()) {
				resource.setMessage(key, valueIfNotFound, bundleIdentifier);
			}
		}
		return valueIfNotFound;
	}

	public Object setLocalisedMessage(Object key, Object value, String bundleIdentifier) {
		for(MessageResource resource : resources) {
			resource.setMessage(key, value, bundleIdentifier);
		}
		return value;
	}
	
	public Object setLocalisedMessage(Object key, Object value, String bundleIdentifier, Locale locale) {
		for(MessageResource resource : resources) {
			resource.setMessage(key, value, bundleIdentifier, locale);
		}
		return value;
	}
	
	public Map<String, Object> setLocalisedMessageToAutoInsertRes(Object key, Object value, String bundleIdentifier, Locale locale) {
		Map<String, Object> setMessages = new HashMap<String, Object>(resources.size());
		
		for(MessageResource resource : resources) {
			if(resource.isAutoInsert()) {
				Object setValue = resource.setMessage(key, value, bundleIdentifier, locale);
				
				if(setValue != null) {
					setMessages.put(resource.getIdentifier(), setValue);
				}
			}
		}
		return setMessages;
	}
	
	public void removeLocalisedMessageFromAutoInsertRes(Object key, String bundleIdentifier, Locale locale) {
		for(MessageResource resource : resources) {
			if(resource.isAutoInsert()) {
				resource.removeMessage(key, bundleIdentifier, locale);
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	private void sortResourcesByImportance() {
		ResourceComparatorByLevel resourceComparatorByLevel = new ResourceComparatorByLevel();
		Collections.sort(resources, resourceComparatorByLevel);
	}
	
	@Autowired
	public void setResourceList(List<MessageResource> resources) {
		this.resources = resources;
		sortResourcesByImportance();
	}
	
	public List<MessageResource> getResourceList() {
		return resources;
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

	public MessageResource getResourceByIdentifier(String identifier) {
		for(MessageResource resource : resources) {
			if(resource.getIdentifier().equals(identifier)) {
				return resource;
			}
		}
		return null;
	}

	public void onApplicationEvent(ApplicationEvent applicationEvent) {
		if(applicationEvent instanceof ResourceLevelChangeEvent) {
			sortResourcesByImportance();
		}
	}
}
