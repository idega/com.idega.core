package com.idega.data.query.range;

import java.math.BigDecimal;

/**
 * Class BigDecimalRange represents a range of BigDecimal values.
 * 
 * @author <a href="mailto:derek@derekmahar.ca">Derek Mahar</a>
 */
public class BigDecimalRange {

	// Ending value of the range.
	private BigDecimal end;

	// Starting value of the range.
	private BigDecimal start;

	/**
	 * Initializes a BigDecimalRange with a given starting BigDecimal and ending
	 * BigDecimal that bound the range.
	 * 
	 * @param start
	 *          the starting BigDecimal value of the range.
	 * 
	 * @param end
	 *          the ending BigDecimal value of the range.
	 */
	public BigDecimalRange(BigDecimal start, BigDecimal end) {
		this.start = start;
		this.end = end;
	}

	/**
	 * Returns the ending BigDecimal value of the range.
	 * 
	 * @return the ending BigDecimal value of the range.
	 */
	public BigDecimal getEnd() {
		return end;
	}

	/**
	 * Returns the starting BigDecimal value of the range.
	 * 
	 * @return the starting BigDecimal value of the range.
	 */
	public BigDecimal getStart() {
		return start;
	}
}
