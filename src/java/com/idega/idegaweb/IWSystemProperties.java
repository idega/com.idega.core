package com.idega.idegaweb;

import java.util.Locale;

/**
 * @author Laddi
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public class IWSystemProperties extends IWPropertyList {

	private final static String IW_BUNDLE_IDENTIFIER = "com.idega.idegaweb";
	private IWMainApplication _application;
	
	public IWSystemProperties(IWMainApplication application) {
		super(application.getPropertiesRealPath(), "system_properties.pxml", true);
		_application = application;
	}

	public String getLocalizedName(Locale locale,String propertyName) {
		IWProperty property = getIWProperty(propertyName);
		if ( property != null )
			return getLocalizedName(locale,property);
		return null;
	}
	
	public String getLocalizedName(Locale locale,IWProperty property) {
		return _application.getBundle(IW_BUNDLE_IDENTIFIER).getResourceBundle(locale).getLocalizedString(property.getName(), property.getName());
	}
	
	public IWPropertyList getProperties(String propertyListName) {
		IWPropertyList list = getPropertyList(propertyListName);
		if ( list == null )
			list = this.getNewPropertyList(propertyListName);
		return list;
	}
}
