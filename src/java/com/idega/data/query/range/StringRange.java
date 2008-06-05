package com.idega.data.query.range;

/**
 * Class StringRange represents a range of String values.
 * 
 * @author <a href="mailto:derek@derekmahar.ca">Derek Mahar</a>
 */
public class StringRange {

	// Ending value of the range.
	private String end;

	// Starting value of the range.
	private String start;

	/**
	 * Initializes a StringRange with a given starting String value and ending
	 * String value that bound the range.
	 * 
	 * @param start
	 *          the starting String value of the range.
	 * 
	 * @param end
	 *          the ending String value of the range.
	 */
	public StringRange(String start, String end) {
		this.start = start;
		this.end = end;
	}

	/**
	 * Returns the ending String value of the range.
	 * 
	 * @return the ending String value of the range.
	 */
	public String getEnd() {
		return end;
	}

	/**
	 * Returns the ending String value of the range.
	 * 
	 * @return the ending String value of the range.
	 */
	public String getStart() {
		return start;
	}
}
