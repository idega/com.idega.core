package com.idega.util.messages;

import java.util.List;
import java.util.Locale;

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
	public void setResourceList(List<MessageResource> resources);
	public List<MessageResource> getResourceList();
	
	/**
	 * Gets localised message for specified locale
	 * @return object that was found in resource and/or set to it or valueIfNotFound object in case no messages were found 
	 * 		   or autoinserted
	 */
	public Object getLocalisedMessage(Object key, Object valueIfNotFound, String bundleIdentifier);
	
	/**
	 * Gets localised message for specified locale
	 * @return object that was found in resource and/or set to it or valueIfNotFound object in case no messages were found 
	 * 		   or autoinserted
	 */
	public Object getLocalisedMessage(Object key, Object valueIfNotFound, String bundleIdentifier, Locale locale);
	public Object setLocalisedMessage(Object key, Object value, String bundleIdentifier);
	public Object setLocalisedMessage(Object key, Object value, String bundleIdentifier, Locale locale);
	public MessageResource getResourceByIdentifier(String identifier);
}
