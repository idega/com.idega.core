
package com.idega.util;

import java.text.Collator;
import java.util.Comparator;
import java.util.Locale;

/**
 * A very simple class that compares two strings alphabetically by their local. Useful for TreeMaps for example
 * just construct a TreeMap map = new TreeMap(new StringAlphabeticalComparator(locale)) and it keeps the maps keys sorted.
 * @author Eiki
 *
 */
public class StringAlphabeticalComparator implements Comparator {

	private Locale _locale;
	
	public StringAlphabeticalComparator(Locale locale) {
		this._locale = locale;
	}

	/**
	 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
	 */
	public int compare(Object o1, Object o2) {
		Collator collator = Collator.getInstance(this._locale);		
		return collator.compare((String)o1,(String)o2);
	}

}
