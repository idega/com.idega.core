package com.idega.business.chooser.helper;

import java.util.List;

import com.idega.builder.bean.AdvancedProperty;
import com.idega.cal.bean.CalendarPropertiesBean;
import com.idega.core.builder.business.ICBuilderConstants;
import com.idega.user.bean.PropertiesBean;
import com.idega.util.CoreConstants;

public class CalendarsChooserHelper extends GroupsChooserHelper {

	public CalendarPropertiesBean getExtractedPropertiesFromString(String value) {
		PropertiesBean propBean = super.getExtractedPropertiesFromString(value);
		if (propBean == null) {
			return null;
		}
		
		CalendarPropertiesBean calendarProperties = new CalendarPropertiesBean(propBean);
		
		if (value == null) {
			return calendarProperties;
		}
		String[] values = value.split(ICBuilderConstants.BUILDER_MODULE_PROPERTY_VALUES_SEPARATOR);
		if (values == null) {
			return calendarProperties;
		}
		
		List<String> ledgers = getValuesFromString(values[values.length - 1], CoreConstants.COMMA);
		List<String> events = getValuesFromString(values[values.length - 2], CoreConstants.COMMA);
		
		if (isValueListValid(events)) {
			calendarProperties.setEvents(events);
		}
		if (isValueListValid(ledgers)) {
			calendarProperties.setLedgers(ledgers);
		}
		
		return calendarProperties;
	}
	
	public String[] getPropertyValue(List<AdvancedProperty> properties) {
		String[] basicValues = super.getPropertyValue(properties);
		if (basicValues == null) {
			return null;
		}
		
		if (basicValues.length > 1) {
			return basicValues;
		}
		
		StringBuffer value = new StringBuffer(basicValues[0]).append(ICBuilderConstants.BUILDER_MODULE_PROPERTY_VALUES_SEPARATOR);
		
		String events = findPropertyValue(properties, ICBuilderConstants.CALENDAR_EVENTS_ADVANCED_PROPERTY_KEY);
		if (events == null) {
			events = CoreConstants.MINUS;
		}
		String ledgers = findPropertyValue(properties, ICBuilderConstants.CALENDAR_LEDGERS_ADVANCED_PROPERTY_KEY);
		if (ledgers == null) {
			ledgers = CoreConstants.MINUS;
		}
		
		value.append(events).append(ICBuilderConstants.BUILDER_MODULE_PROPERTY_VALUES_SEPARATOR).append(ledgers);
		
		return new String[] {value.toString()};
	}
	
	private boolean isValueListValid(List<String> values) {
		if (values == null) {
			return false;
		}
		
		if (values.size() == 1) {
			if (values.get(0).equals(CoreConstants.MINUS)) {
				return false;
			}
		}
		
		return true;
	}
}
