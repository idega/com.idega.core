package com.idega.core.localisation.business;

import java.util.Comparator;
import java.util.Locale;

public class LocalesComparator implements Comparator<Locale> {
	
	private boolean sortByDisplayCountry = false;

	public void setSortByDisplayCountry(boolean sortByDisplayCountry) {
		this.sortByDisplayCountry = sortByDisplayCountry;
	}

	/**
	 * By default sorts by display language values
	 */
	public int compare(Locale l1, Locale l2) {
		int result = 0;
		
		String value1 = l1.getDisplayLanguage();
		String value2 = l2.getDisplayLanguage();
		
		if (sortByDisplayCountry) {
			value1 = l1.getDisplayCountry();
			value2 = l2.getDisplayCountry();
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
