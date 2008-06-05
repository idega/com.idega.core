package com.idega.data.query.range;

import java.util.Date;

/**
 * Class DateRange represents a range of Date values.
 * 
 * @author <a href="mailto:derek@derekmahar.ca">Derek Mahar</a>
 */
public class DateRange {

	// Ending value of the range.
	private Date end;

	// Starting value of the range.
	private Date start;

	/**
	 * Initializes a DateRange with a given starting Date and ending Date that
	 * bound the range.
	 * 
	 * @param start
	 *          the starting Date of the range.
	 * 
	 * @param end
	 *          the ending Date of the range.
	 */
	public DateRange(Date start, Date end) {
		this.start = start;
		this.end = end;
	}

	/**
	 * Returns the ending Date of the range.
	 * 
	 * @return the ending Date of the range.
	 */
	public Date getEnd() {
		return end;
	}

	/**
	 * Returns the starting Date of the range.
	 * 
	 * @return the starting Date of the range.
	 */
	public Date getStart() {
		return start;
	}
}
