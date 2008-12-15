package com.idega.idegaweb;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;

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
 * Last modified: $Date: 2008/12/15 14:07:34 $ by $Author: anton $
 *
 */
@Service
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class JarLoadedResourceBundle extends IWResourceBundle implements MessageResource {
//	private String bundleIdentifier;
//	private Level usagePriorityLevel = MessageResourceImportanceLevel.MIDDLE_ORDER;
//	private boolean autoInsert = false;
	
	public static final String RESOURCE_IDENTIFIER = "jar_loaded_resource";
	
//	private String identifier = RESOURCE_IDENTIFIER;
	
	
	public JarLoadedResourceBundle(IWBundle parent, File file, Locale locale) throws IOException {
		super(parent, file, locale);
	}

	public JarLoadedResourceBundle() {
		super();
	}

	
	public JarLoadedResourceBundle(IWBundle parent, InputStream stream, Locale locale) throws IOException {
		super(parent,stream,locale);
	}

	public JarLoadedResourceBundle(IWResourceBundle parent, File file, Locale locale) throws IOException {
		super(parent, file, locale);
	}

	public JarLoadedResourceBundle(IWResourceBundle parent, InputStream inputStream, Locale locale) throws IOException {
		super(parent, inputStream, locale);
	}
	
	@Override
	protected void initProperities() {
		setIdentifier(RESOURCE_IDENTIFIER);
		setLevel(MessageResourceImportanceLevel.MIDDLE_ORDER);
		setAutoInsert(false);
	}
	
	@Override
	public void initialize(String bundleIdentifier, Locale locale) throws OperationNotSupportedException {
		throw new OperationNotSupportedException("Not implemented");
	}

//	@Override
//	public String getBundleIdentifier() {
//		return bundleIdentifier;
//	}
//
//	@Override
//	public void setBundleIdentifier(String bundleIdentifier) {
//		this.bundleIdentifier = bundleIdentifier;
//	}
//
//	@Override
//	public Level getLevel() {
//		return usagePriorityLevel;
//	}
//
//	@Override
//	public void setLevel(Level usagePriorityLevel) {
//		this.usagePriorityLevel = usagePriorityLevel;
//	}
//
//	@Override
//	public boolean isAutoInsert() {
//		return autoInsert;
//	}
//
//	@Override
//	public void setAutoInsert(boolean autoInsert) {
//		this.autoInsert = autoInsert;
//	}
//
//	@Override
//	public String getIdentifier() {
//		return identifier;
//	}
//
//	@Override
//	public void setIdentifier(String identifier) {
//		this.identifier = identifier;
//	}
}
