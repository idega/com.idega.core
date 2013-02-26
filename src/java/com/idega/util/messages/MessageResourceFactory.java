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
	 * @return String that was found in resource and/or set to it or valueIfNotFound String in case no messages were found
	 * 		   or autoinserted
	 */
	public String getLocalizedMessage(String key, String valueIfNotFound, String bundleIdentifier, Locale locale);

	public String setLocalizedMessage(String key, String value, String bundleIdentifier, Locale locale);

	public void setLocalizedMessages(Map<String, String> values, String bundleIdentifier, Locale locale);

	public Map<String, String> setLocalizedMessageToAutoInsertRes(String key, String value, String bundleIdentifier, Locale locale);

	public void removeLocalizedMessageFromAutoInsertRes(String key, String bundleIdentifier, Locale locale);

	public MessageResource getResource(String storageIdentifier, String bundleIdentifier, Locale locale);

	public List<MessageResource> getResourceListByStorageIdentifier(String storageIdentifier);

	public List<MessageResource> getResourceListByBundleAndLocale(String bundleIdentifier, Locale locale);

	public void addInitializedMessageResource(MessageResource resource, String bundleIdentifier, Locale locale);

	public List<MessageResource> getAvailableUninitializedMessageResources();
}
