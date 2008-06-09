package com.idega.builder.business;

import java.text.Collator;
import java.util.Comparator;
import java.util.Locale;

import com.idega.builder.bean.AdvancedProperty;

public class AdvancedPropertyComparator implements Comparator<AdvancedProperty> {
	
	private boolean sortById = false;
	private Locale locale = null;
	
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
		
		Collator collator = Collator.getInstance(locale);
		return collator.compare(value1, value2);
	}

}
