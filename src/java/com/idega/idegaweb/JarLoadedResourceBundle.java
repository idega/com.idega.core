package com.idega.idegaweb;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
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

	private Level usagePriorityLevel = MessageResourceImportanceLevel.MIDDLE_ORDER;
	private boolean autoInsert = false;

	public static final String RESOURCE_IDENTIFIER = "jar_loaded_resource";

	private IWResourceBundle resource;

	public IWResourceBundle getResource() {
		return resource;
	}

	protected void initProperities() {
		setIdentifier(RESOURCE_IDENTIFIER);
		setLevel(usagePriorityLevel);
		setAutoInsert(autoInsert);
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

	@Override
	public String getBundleIdentifier() {
		if (resource != null)
			return resource.getBundleIdentifier();
		return null;
	}

	@Override
	public String getIdentifier() {
		if (resource != null) {
			return resource.getIdentifier();
		} else {
			return RESOURCE_IDENTIFIER;
		}
	}

	@Override
	public Level getLevel() {
		return resource.getLevel();
	}

	@Override
	public boolean isAutoInsert() {
		return resource.isAutoInsert();
	}

	@Override
	public void removeMessage(String key) {}

	@Override
	public void setAutoInsert(boolean value) {}

	@Override
	public void setBundleIdentifier(String identifier) {
		resource.setBundleIdentifier(identifier);
	}

	@Override
	public void setIdentifier(String identifier) {
		resource.setIdentifier(identifier);
	}

	@Override
	public void setLevel(Level priorityLevel) {
		resource.setLevel(priorityLevel);
	}

	@Override
	public String setMessage(String key, String value) {
		return null;
	}

	@Override
	public void setMessages(Map<String, String> values) {}

	@Override
	public Set<String> getAllLocalizedKeys() {
		return resource.getAllLocalizedKeys();
	}

	@Override
	public String getMessage(String key) {
		return resource.getMessage(key);
	}

	@Override
	public void store() {}

	private void writeObject(ObjectOutputStream out) throws IOException {
	}

	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
	}

	@Override
	public String toString() {
		return "Jar loaded resource: " + getBundleIdentifier();
	}
}