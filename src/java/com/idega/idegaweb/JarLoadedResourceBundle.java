package com.idega.idegaweb;

import java.io.IOException;
import java.io.Serializable;
import java.util.Collections;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.naming.OperationNotSupportedException;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import com.idega.util.messages.MessageResource;
import com.idega.util.messages.MessageResourceImportanceLevel;

/**
 *
 *
 * @author <a href="anton@idega.com">Anton Makarov</a>
 * @version Revision: 1.0
 *
 * Last modified: $Date: 2009/03/11 15:48:59 $ by $Author: valdas $
 *
 */
@Service
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class JarLoadedResourceBundle implements MessageResource, Serializable {

	private static final long serialVersionUID = 2817447347536356853L;

	public static final String RESOURCE_IDENTIFIER = "jar_loaded_resource";

	private IWResourceBundle resource;

	public IWResourceBundle getResource() {
		return resource;
	}

	protected void initProperities() {
		setIdentifier(RESOURCE_IDENTIFIER);
		setLevel(MessageResourceImportanceLevel.MIDDLE_ORDER);
		setAutoInsert(Boolean.FALSE);
	}

	@Override
	public void initialize(String bundleIdentifier, Locale locale) throws OperationNotSupportedException {
		if (!bundleIdentifier.equals(MessageResource.NO_BUNDLE)) {
			IWBundle bundle = IWMainApplication.getDefaultIWMainApplication().getBundle(bundleIdentifier);
			try {
				if (bundle instanceof DefaultIWBundle) {
					resource = ((DefaultIWBundle) bundle).initializeResourceBundle(locale);
				} else {
					Logger.getLogger(JarLoadedResourceBundle.class.getName()).warning("Bundle " + bundle.getClass() + " doesn't support this method!");
				}
				initProperities();
			} catch (IOException e) {
				throw new OperationNotSupportedException("Initialization of this resource is not supported in test environment");
			}
		} else {
			throw new OperationNotSupportedException("Initialization of this resource is not supported in test environment");
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.idega.util.messages.MessageResource#getBundleIdentifier()
	 */
	@Override
	public String getBundleIdentifier() {
		if (getResource() != null) {
			return getResource().getBundleIdentifier();
		}

		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see com.idega.util.messages.MessageResource#getIdentifier()
	 */
	@Override
	public String getIdentifier() {
		if (getResource() != null) {
			return getResource().getIdentifier();
		}
		
		return RESOURCE_IDENTIFIER;
	}

	/*
	 * (non-Javadoc)
	 * @see com.idega.util.messages.MessageResource#getLevel()
	 */
	@Override
	public Level getLevel() {
		if (getResource() != null) {
			return getResource().getLevel();
		}

		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see com.idega.util.messages.MessageResource#isAutoInsert()
	 */
	@Override
	public boolean isAutoInsert() {
		if (getResource() != null) {
			return getResource().isAutoInsert();
		} 

		throw new NullPointerException("No resource is loaded!");
	}

	/*
	 * (non-Javadoc)
	 * @see com.idega.util.messages.MessageResource#removeMessage(java.lang.String)
	 */
	@Override
	public void removeMessage(String key) {
		throw new UnsupportedOperationException("Modification in not allowed for .jar resource");
	}

	/*
	 * (non-Javadoc)
	 * @see com.idega.util.messages.MessageResource#setAutoInsert(boolean)
	 */
	@Override
	public void setAutoInsert(boolean value) {
//		throw new UnsupportedOperationException("Modification in not allowed for .jar resource");
	}

	/*
	 * (non-Javadoc)
	 * @see com.idega.util.messages.MessageResource#setBundleIdentifier(java.lang.String)
	 */
	@Override
	public void setBundleIdentifier(String identifier) {
		if (getResource() != null) {
			getResource().setBundleIdentifier(identifier);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.idega.util.messages.MessageResource#setIdentifier(java.lang.String)
	 */
	@Override
	public void setIdentifier(String identifier) {
		if (getResource() != null) {
			getResource().setIdentifier(identifier);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.idega.util.messages.MessageResource#setLevel(java.util.logging.Level)
	 */
	@Override
	public void setLevel(Level priorityLevel) {
		if (getResource() != null) {
			getResource().setLevel(priorityLevel);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.idega.util.messages.MessageResource#setMessage(java.lang.String, java.lang.String)
	 */
	@Override
	public String setMessage(String key, String value) {
		throw new UnsupportedOperationException("Modification in not allowed for .jar resource");
	}

	/*
	 * (non-Javadoc)
	 * @see com.idega.util.messages.MessageResource#setMessages(java.util.Map)
	 */
	@Override
	public void setMessages(Map<String, String> values) {
		throw new UnsupportedOperationException("Modification in not allowed for .jar resource");
	}

	/*
	 * (non-Javadoc)
	 * @see com.idega.util.messages.MessageResource#getAllLocalizedKeys()
	 */
	@Override
	public Set<String> getAllLocalizedKeys() {
		if (getResource() != null) {
			return getResource().getAllLocalizedKeys();
		}

		return Collections.emptySet();
	}

	/*
	 * (non-Javadoc)
	 * @see com.idega.util.messages.MessageResource#getMessage(java.lang.String)
	 */
	@Override
	public String getMessage(String key) {
		if (getResource() != null) {
			return getResource().getMessage(key);
		}

		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see com.idega.util.messages.MessageResource#isModificationAllowed()
	 */
	@Override
	public boolean isModificationAllowed() {
		return Boolean.FALSE;
	}

	/*
	 * (non-Javadoc)
	 * @see com.idega.util.messages.MessageResource#store()
	 */
	@Override
	public void store() {
		throw new UnsupportedOperationException("Modification in not allowed for .jar resource");
	}

	@Override
	public String toString() {
		return "Jar loaded resource: " + getBundleIdentifier();
	}
}