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

	static final String NO_BUNDLE = "no bundle";

	void setLevel(Level priorityLevel);

	Level getLevel();

	/**
	 * @return String that was set or null if there was a failure setting String
	 */
	String setMessage(String key, String value);

	void setMessages(Map<String, String> values);

	Set<String> getAllLocalizedKeys();

	/**
	 * @param key - message key
	 * @return String that was found in resource, null - if there are no values with specified key
	 */
	String getMessage(String key);

	void removeMessage(String key);

	boolean isAutoInsert();

	void setAutoInsert(boolean value);

	String getIdentifier();

	void setIdentifier(String identifier);

	void initialize(String bundleIdentifier, Locale locale) throws IOException, OperationNotSupportedException;

	String getBundleIdentifier();

	void setBundleIdentifier(String identifier);

	boolean isModificationAllowed();

	void store();
}
