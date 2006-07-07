package com.idega.core.search.business;

import java.text.Collator;
import java.util.Comparator;
import java.util.Date;
import java.util.Locale;
import java.util.Map;

/**
 * A comparator that can sort by any property in SearchResult and its attribute
 * map properties (String,Number and Date) Last modified: $Date: 2006/04/09
 * 12:01:55 $ by $Author: eiki $
 * 
 * @author <a href="mailto:eiki@idega.com">Eirikur S. Hrafnsson</a>
 * @version $Revision: 1.1.2.1 $
 */
public class SearchResultComparator implements Comparator {

	public static final int SORT_BY_RESULT_TYPE = 1;

	public static final int SORT_BY_RESULT_NAME = 2;

	public static final int SORT_BY_RESULT_EXTRA_INFO = 3;

	public static final int SORT_BY_RESULT_ABSTRACT = 4;

	public static final int SORT_BY_RESULT_URI = 5;

	public static final int SORT_BY_RESULT_ATTRIBUTE_MAP_KEY = 6;

	// These we use to make the sorting faster by eliminating the need for all
	// thos instance of...
	public static final int MAP_KEY_SORT_BY_TYPE_UNKNOWN = -1;

	public static final int MAP_KEY_SORT_BY_TYPE_STRING = 1;

	public static final int MAP_KEY_SORT_BY_TYPE_DATE = 2;

	public static final int MAP_KEY_SORT_BY_TYPE_NUMBER = 3;

	protected int sortBy;

	protected int mapKeySortByType = MAP_KEY_SORT_BY_TYPE_UNKNOWN;

	protected int multiplier = 1;

	protected String mapKey;

	protected Collator collator;

	protected Locale locale;

	public SearchResultComparator() {
		super();
		collator = Collator.getInstance();
		sortBy = SearchResultComparator.SORT_BY_RESULT_NAME;
	}

	public SearchResultComparator(Locale locale, int sortBy, boolean descending) {
		this.locale = locale;
		collator = Collator.getInstance(locale);
		this.sortBy = sortBy;
		if (descending) {
			this.multiplier = -1;
		}
	}

	/**
	 * Sets the comparator to get the value to order by from
	 * SearchResult.getSearchResultAttributes() Map.
	 * 
	 * @param locale
	 * @param resultAttributeKeyToSortBy
	 * @param descending
	 */
	public SearchResultComparator(Locale locale,
			String resultAttributeKeyToSortBy, boolean descending) {
		this(locale, SearchResultComparator.SORT_BY_RESULT_ATTRIBUTE_MAP_KEY,
				descending);
		this.mapKey = resultAttributeKeyToSortBy;
		if (descending) {
			this.multiplier = -1;
		}
	}

	public int compare(Object searchResult1, Object searchResult2) {

		SearchResult o1 = (SearchResult) searchResult1;
		SearchResult o2 = (SearchResult) searchResult2;

		switch (this.sortBy) {
		case SearchResultComparator.SORT_BY_RESULT_NAME:
			return this.multiplier
					* compare(o1.getSearchResultName(), o2
							.getSearchResultName());
		case SearchResultComparator.SORT_BY_RESULT_EXTRA_INFO:
			return this.multiplier
					* compare(o1.getSearchResultExtraInformation(), o2
							.getSearchResultExtraInformation());
		case SearchResultComparator.SORT_BY_RESULT_ABSTRACT:
			return this.multiplier
					* compare(o1.getSearchResultAbstract(), o2
							.getSearchResultAbstract());
		case SearchResultComparator.SORT_BY_RESULT_URI:
			return this.multiplier
					* compare(o1.getSearchResultURI(), o2.getSearchResultURI());
		case SearchResultComparator.SORT_BY_RESULT_TYPE:
			return this.multiplier
					* compare(o1.getSearchResultType(), o2
							.getSearchResultType());
		case SearchResultComparator.SORT_BY_RESULT_ATTRIBUTE_MAP_KEY:
			return this.multiplier * mapKeyCompare(o1, o2, mapKey);
		default:
			return 0;
		}

	}

	/**
	 * Gets the value from the maps with the key supplied and compares them,
	 * support string,numbers and Date objects
	 * 
	 * @param o1
	 * @param o2
	 * @param key
	 * @return
	 */
	public int mapKeyCompare(SearchResult o1, SearchResult o2, String key) {
		Map attribMap1 = o1.getSearchResultAttributes();
		Map attribMap2 = o2.getSearchResultAttributes();

		if (attribMap1 != null && attribMap2 != null) {
			Object value1 = attribMap1.get(key);
			Object value2 = attribMap2.get(key);

			if (this.mapKeySortByType == SearchResultComparator.MAP_KEY_SORT_BY_TYPE_UNKNOWN) {
				if (value1 instanceof String) {
					this.mapKeySortByType = SearchResultComparator.MAP_KEY_SORT_BY_TYPE_STRING;
				} else if (value1 instanceof Number) {
					this.mapKeySortByType = SearchResultComparator.MAP_KEY_SORT_BY_TYPE_NUMBER;
				} else if (value1 instanceof Date) {
					this.mapKeySortByType = SearchResultComparator.MAP_KEY_SORT_BY_TYPE_DATE;
				}
			}
	
			switch (this.mapKeySortByType) {
				case SearchResultComparator.MAP_KEY_SORT_BY_TYPE_STRING:
					return compare((String) value1, (String) value2);
				case SearchResultComparator.MAP_KEY_SORT_BY_TYPE_NUMBER:
					return compare((Number) value1, (Number) value2);
				case SearchResultComparator.MAP_KEY_SORT_BY_TYPE_DATE:
					return compare((Date) value1, (Date) value2);
				default:
					return 0;
			}
	

		}

		return 0;
	}

	/**
	 * All numbers are compared as longs
	 * 
	 * @param o1
	 * @param o2
	 * @return
	 */
	public int compare(Number o1, Number o2) {
		return compare(o1.longValue(), o2.longValue());
	}

	/**
	 * All numbers are compared as longs
	 * 
	 * @param o1
	 * @param o2
	 * @return
	 */
	public int compare(long o1, long o2) {
		if (o1 < o2) {
			return -1;
		} else if (o1 > o2) {
			return 1;
		} else {
			return 0;
		}
	}

	/**
	 * The Dates are converted to their long counterparts and compared
	 * 
	 * @param o1
	 * @param o2
	 * @return
	 */
	public int compare(Date o1, Date o2) {
		long s1 = o1.getTime();
		long s2 = o2.getTime();

		return compare(s1, s2);
	}

	public int compare(String o1, String o2) {
		return this.collator.compare(o1, o2);
	}

}
