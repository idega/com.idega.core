package com.idega.util.messages;

import java.io.IOException;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;

import javax.naming.OperationNotSupportedException;

/**
 *
 * 
 * @author <a href="anton@idega.com">Anton Makarov</a>
 * @version Revision: 1.1 
 *
 * Last modified: Nov 16, 2008 by Author: Anton 
 *
 */

public interface MessageResource {
	public static final String NO_BUNDLE = "no bundle";
	
	public void setLevel(Level priorityLevel);
	
	public Level getLevel();
	
	/**
	 * @return object that was set or null if there was a failure setting object
	 */
	public Object setMessage(Object key, Object value);
	
	public void setMessages(Map<Object, Object> values);
	
	public Set<Object> getAllLocalisedKeys();
	

	/**
	 * @param key - message key
	 * @return object that was found in resource, null - if there are no values with specified key
	 */	
	public Object getMessage(Object key);
	
//	/**
//	 * @param key - message key
//	 * @param bundleIdentifier - bundle in which messages should be located
//	 * @param locale - message locale
//	 * @param defaultValue - value that can be automatically set to to the resource according t its implementation
//	 * @return object that was found in resource, valueIfNotFound - if there are no values with specified key
//	 */
//	public Object getMessage(Object key, String defaultValue, String bundleIdentifier, Locale locale);
	
	public void removeMessage(Object key);
	
	public boolean isAutoInsert();
	
	public void setAutoInsert(boolean value);
	
	public String getIdentifier();
	
	public void initialize(String bundleIdentifier, Locale locale) throws IOException, OperationNotSupportedException;
}
