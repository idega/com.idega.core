package com.idega.data.query.range;

/**
 * Class IntegerRange represents a range of Integer values.
 * 
 * @author <a href="mailto:derek@derekmahar.ca">Derek Mahar</a>
 */
public class IntegerRange {

	// Ending value of the range.
	private Integer end;

	// Starting value of the range.
	private Integer start;

	/**
	 * Initializes an IntegerRange with a given starting integer value and ending
	 * integer value that bound the range.
	 * 
	 * @param start
	 *          the starting integer value of the range.
	 * 
	 * @param end
	 *          the ending integer value of the range.
	 */
	public IntegerRange(int start, int end) {
		this.start = new Integer(start);
		this.end = new Integer(end);
	}

	/**
	 * Initializes an IntegerRange with a given starting Integer value and ending
	 * Integer value that bound the range.
	 * 
	 * @param start
	 *          the starting Integer value of the range.
	 * 
	 * @param end
	 *          the ending Integer value of the range.
	 */
	public IntegerRange(Integer start, Integer end) {
		this.start = start;
		this.end = end;
	}

	/**
	 * Returns the ending Integer value of the range.
	 * 
	 * @return the ending Integer value of the range.
	 */
	public Integer getEnd() {
		return end;
	}

	/**
	 * Returns the ending Integer value of the range.
	 * 
	 * @return the ending Integer value of the range.
	 */
	public Integer getStart() {
		return start;
	}
}
