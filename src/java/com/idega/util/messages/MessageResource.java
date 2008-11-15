package com.idega.util.messages;

import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;

/**
 *
 * 
 * @author <a href="anton@idega.com">Anton Makarov</a>
 * @version Revision: 1.0 
 *
 * Last modified: Oct 16, 2008 by Author: Anton 
 *
 */

public interface MessageResource {
	public static final String NO_BUNDLE = "no bundle";
	
	public void setLevel(Level priorityLevel);
	
	public Level getLevel();
	
	/**
	 * @return object that was set or null if there was a failure setting object
	 */
	public Object setMessage(Object key, Object value, String bundleIdentifier);
	
	/**
	 * @return object that was set or null if there was a failure setting object
	 */
	
	public Object setMessage(Object key, Object value, String bundleIdentifier, Locale locale);
	
	public void setMessages(Map<Object, Object> values, String bundleIdentifier, Locale locale);
	
	public Set<Object> getAllLocalisedKeys(String bundleIdentifier, Locale locale);
	
	/**
	 * @param key - message key
	 * @param bundleIdentifier - bundle in which messages should be located
	 * @return object that was found in resource, null - if there are no values with specified key
	 */
	public Object getMessage(Object key, String bundleIdentifier);
	
	/**
	 * @param key - message key
	 * @param bundleIdentifier - bundle in which messages should be located
	 * @return object that was found in resource, null - if there are no values with specified key
	 */	
	public Object getMessage(Object key, String bundleIdentifier, Locale locale);
	
	public void removeMessage(Object key, String bundleIdentifier, Locale locale);
	
	public boolean isAutoInsert();
	
	public void setAutoInsert(boolean value);
	
	public String getIdentifier();
}
