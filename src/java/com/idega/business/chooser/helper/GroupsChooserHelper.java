package com.idega.business.chooser.helper;

import java.util.ArrayList;
import java.util.List;

import com.idega.core.builder.business.ICBuilderConstants;
import com.idega.user.bean.PropertiesBean;
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
		if (values.length == 5)	{	//	server, user, login, ids, connection
			bean = new PropertiesBean();
			
			bean.setServer(values[0]);
			bean.setLogin(values[1]);
			bean.setPassword(CoreUtil.getDecodedValue(values[2]));
			bean.setUniqueIds(getValuesFromString(values[3], ","));
			bean.setRemoteMode(getBooleanValue(values[4]));
		}
		
		return bean;
	}
	
	private boolean getBooleanValue(String value) {
		if (value == null) {
			return false;
		}
		return value.equals(ICBuilderConstants.GROUPS_CHOOSER_REMOTE_CONNECTION);
	}
	
	private List<String> getValuesFromString(String value, String separator) {
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
