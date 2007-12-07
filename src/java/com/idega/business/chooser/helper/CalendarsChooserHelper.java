package com.idega.business.chooser.helper;

import com.idega.cal.bean.CalendarPropertiesBean;
import com.idega.user.bean.PropertiesBean;

public class CalendarsChooserHelper extends GroupsChooserHelper {

	public CalendarPropertiesBean getExtractedPropertiesFromString(String value) {
		PropertiesBean propBean = super.getExtractedPropertiesFromString(value);
		if (propBean == null) {
			return null;
		}
		
		CalendarPropertiesBean calendarProperties = new CalendarPropertiesBean(propBean);
		
		//	TODO
		
		return calendarProperties;
	}
}
