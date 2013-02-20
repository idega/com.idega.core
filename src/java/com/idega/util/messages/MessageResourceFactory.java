package com.idega.util.messages;

import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 *
 *
 * @author <a href="anton@idega.com">Anton Makarov</a>
 * @version Revision: 1.0
 *
 * Last modified: Oct 16, 2008 by Author: Anton
 *
 */

public interface MessageResourceFactory {

	/**
	 * Gets localized message for specified locale
	 * @return object that was found in resource and/or set to it or valueIfNotFound object in case no messages were found
	 * 		   or autoinserted
	 */
	public Object getLocalizedMessage(Object key, Object valueIfNotFound, String bundleIdentifier, Locale locale);

	public Object setLocalizedMessage(Object key, Object value, String bundleIdentifier, Locale locale);

	public void setLocalizedMessages(Map<Object, Object> values, String bundleIdentifier, Locale locale);

	public Map<String, Object> setLocalizedMessageToAutoInsertRes(Object key, Object value, String bundleIdentifier, Locale locale);

	public void removeLocalizedMessageFromAutoInsertRes(Object key, String bundleIdentifier, Locale locale);

	public MessageResource getResource(String storageIdentifier, String bundleIdentifier, Locale locale);

	public List<MessageResource> getResourceListByStorageIdentifier(String storageIdentifier);

	public List<MessageResource> getResourceListByBundleAndLocale(String bundleIdentifier, Locale locale);

	public void addInitializedMessageResource(MessageResource resource, String bundleIdentifier, Locale locale);

	public List<MessageResource> getAvailableUninitializedMessageResources();
}
