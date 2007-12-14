package com.idega.business.chooser.helper;

import java.util.ArrayList;
import java.util.List;

import com.idega.builder.bean.AdvancedProperty;
import com.idega.core.builder.business.ICBuilderConstants;
import com.idega.user.bean.PropertiesBean;
import com.idega.util.CoreConstants;
import com.idega.util.CoreUtil;

public class GroupsChooserHelper {
	
	public PropertiesBean getExtractedPropertiesFromString(String value) {
		if (value == null) {
			return null;
		}
		
		String[] values = value.split(ICBuilderConstants.BUILDER_MODULE_PROPERTY_VALUES_SEPARATOR);
		if (values == null) {
			return null;
		}
		
		PropertiesBean bean = null;
		if (values.length >= 5)	{	//	server, user, login, ids, connection
			bean = new PropertiesBean();
			
			bean.setServer(values[0]);
			bean.setLogin(values[1]);
			bean.setPassword(CoreUtil.getDecodedValue(values[2]));
			bean.setUniqueIds(getValuesFromString(values[3], CoreConstants.COMMA));
			bean.setRemoteMode(getBooleanValue(values[4]));
		}
		
		return bean;
	}
	
	public String[] getPropertyValue(List<AdvancedProperty> properties) {
		String server = null;
		String login = null;
		String password = null;
		String uniqueIds = null;
		
		String connection = findPropertyValue(properties, "connection");
		if (connection == null) {
			return null;
		}
		
		if (connection.equals(ICBuilderConstants.GROUPS_CHOOSER_REMOTE_CONNECTION)) {
			//	Settings for remote connection
			server = findPropertyValue(properties, "server");
			login = findPropertyValue(properties, "login");
			password = CoreUtil.getEncodedValue(findPropertyValue(properties, "password"));
		}
		else {
			//	Settings for local connection
			server = connection;
			login = connection;
			password = connection;
		}
		uniqueIds = findPropertyValue(properties, "uniqueids");
		
		if (server == null || login == null || password == null || uniqueIds == null) {
			return null;
		}
		StringBuffer value = new StringBuffer(server).append(ICBuilderConstants.BUILDER_MODULE_PROPERTY_VALUES_SEPARATOR);
		value.append(login).append(ICBuilderConstants.BUILDER_MODULE_PROPERTY_VALUES_SEPARATOR).append(password);
		value.append(ICBuilderConstants.BUILDER_MODULE_PROPERTY_VALUES_SEPARATOR).append(uniqueIds);
		value.append(ICBuilderConstants.BUILDER_MODULE_PROPERTY_VALUES_SEPARATOR).append(connection);
		return new String[] {value.toString()};
	}
	
	/**
	 * Finds value in list
	 * @param properties
	 * @param id
	 * @return
	 */
	protected String findPropertyValue(List<AdvancedProperty> properties, String id) {
		String value = null;
		boolean found = false;
		AdvancedProperty property = null;
		for (int i = 0; (i < properties.size() && !found); i++) {
			property = properties.get(i);
			if (id.equals(property.getId())) {
				value = property.getValue();
				found = true;
			}
		}
		return value;
	}
	
	private boolean getBooleanValue(String value) {
		if (value == null) {
			return false;
		}
		return value.equals(ICBuilderConstants.GROUPS_CHOOSER_REMOTE_CONNECTION);
	}
	
	protected List<String> getValuesFromString(String value, String separator) {
		if (value == null) {
			return null;
		}

		String[] values = value.split(separator);
		if (values == null) {
			return null;
		}

		List<String> extractedValues = new ArrayList<String>();
		for (int i = 0; i < values.length; i++) {
			extractedValues.add(values[i]);
		}
		return extractedValues;
	}

}
