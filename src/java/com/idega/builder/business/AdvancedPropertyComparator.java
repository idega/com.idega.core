package com.idega.builder.business;

import java.util.Comparator;

import com.idega.builder.bean.AdvancedProperty;

public class AdvancedPropertyComparator implements Comparator<AdvancedProperty> {
	
	private boolean sortById = false;
	
	public AdvancedPropertyComparator() {}
	
	public AdvancedPropertyComparator(boolean sortById) {
		this();
		this.sortById = sortById;
	}

	public int compare(AdvancedProperty prop1, AdvancedProperty prop2) {
		int result = 0;
		
		String value1 = prop1.getValue();
		String value2 = prop2.getValue();
		
		if (sortById) {
			value1 = prop1.getId();
			value2 = prop2.getId();
		}
		
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

}
