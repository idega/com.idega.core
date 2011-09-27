
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
public class StringAlphabeticalComparator implements Comparator<String> {

	private boolean ascending = false;
	private Collator collator;
	
	public StringAlphabeticalComparator(Locale locale) {
		this.collator = Collator.getInstance(locale);
	}
	
	public StringAlphabeticalComparator(Locale locale, boolean ascending) {
		this(locale);
		this.ascending = ascending;
	}

	/**
	 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
	 */
	public int compare(String o1, String o2) {
		int discriminator = ascending ? -1: 1;
		return discriminator * collator.compare(o1, o2);
	}

}