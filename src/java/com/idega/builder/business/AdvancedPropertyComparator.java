package com.idega.builder.business;

import java.text.Collator;
import java.util.Comparator;
import java.util.Locale;

import com.idega.builder.bean.AdvancedProperty;
import com.idega.idegaweb.IWMainApplication;
import com.idega.presentation.IWContext;
import com.idega.util.CoreUtil;

public class AdvancedPropertyComparator implements Comparator<AdvancedProperty> {
	
	private boolean sortById = false;
	private Locale locale = null;
	private Collator collator;
	
	public AdvancedPropertyComparator() {
		Locale locale = null;
		
		IWContext iwc = CoreUtil.getIWContext();
		if (iwc == null) {
			locale = IWMainApplication.getDefaultIWMainApplication().getSettings().getDefaultLocale();
		}
		else {
			locale = iwc.getCurrentLocale();
		}
		if (locale == null) {
			locale = Locale.ENGLISH;
		}
		
		this.locale = locale;
	}
	
	public AdvancedPropertyComparator(Locale locale) {
		this.locale = locale;
	}
	
	public AdvancedPropertyComparator(boolean sortById, Locale locale) {
		this(locale);
		this.sortById = sortById;
	}

	public int compare(AdvancedProperty prop1, AdvancedProperty prop2) {
		int result = 0;
		
		String value1 = prop1.getValue();
		String value2 = prop2.getValue();
		
		if (sortById) {
			value1 = prop1.getId();
			value2 = prop2.getId();
			
			if (value1 == null && value2 == null) {
				result = 0;
			}
			else if (value2 == null) {
				result = 1;
			}
			else if (value1 == null) {
				result = -1;
			}
			else {
				result = value1.compareTo(value2);
			}
			
			return result;
		}
		
		return getCollator().compare(value1, value2);
	}
	
	private Collator getCollator() {
		if (collator == null) {
			collator = Collator.getInstance(locale);
		}
		return collator;
	}

}