package com.idega.idegaweb;

import java.io.IOException;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;

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
 * Last modified: $Date: 2009/03/11 10:06:04 $ by $Author: civilis $
 *
 */
@Service
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class JarLoadedResourceBundle implements MessageResource {
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
	
	public void initialize(String bundleIdentifier, Locale locale) throws OperationNotSupportedException {
		if(/*DefaultIWBundle.isProductionEnvironment() && */!bundleIdentifier.equals(MessageResource.NO_BUNDLE)) {
			DefaultIWBundle bundle = (DefaultIWBundle) IWMainApplication.getDefaultIWMainApplication().getBundle(bundleIdentifier);
			try {
				resource = bundle.initializeResourceBundle(locale);
				initProperities();
			} catch (IOException e) {
				throw new OperationNotSupportedException("Initialization of this resource is not supported in test environment");
			}
		} else {
			throw new OperationNotSupportedException("Initialization of this resource is not supported in test environment");
		}
	}

	public String getBundleIdentifier() {
		String identifier = resource.getBundleIdentifier();
		return identifier;
	}

	public String getIdentifier() {
		if(resource != null) {
			String identifier = resource.getIdentifier();
			return identifier;
		} else {
			return RESOURCE_IDENTIFIER;
		}
	}

	public Level getLevel() {
		return resource.getLevel();
	}

	public boolean isAutoInsert() {
		return resource.isAutoInsert();
	}

	public void removeMessage(Object key) {}

	public void setAutoInsert(boolean value) {}

	public void setBundleIdentifier(String identifier) {
		resource.setBundleIdentifier(identifier);
	}

	public void setIdentifier(String identifier) {
		resource.setIdentifier(identifier);
	}

	public void setLevel(Level priorityLevel) {
		resource.setLevel(priorityLevel);	
	}

	public Object setMessage(Object key, Object value) {
		return null;
	}

	public void setMessages(Map<Object, Object> values) {}

	public Set<Object> getAllLocalisedKeys() {
		return resource.getAllLocalisedKeys();
	}

	public Object getMessage(Object key) {
		return resource.getMessage(key);
	}
}
