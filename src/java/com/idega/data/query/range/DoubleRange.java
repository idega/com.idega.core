package com.idega.data.query.range;

/**
 * Class DoubleRange represents a range of Double values.
 * 
 * @author <a href="mailto:derek@derekmahar.ca">Derek Mahar</a>
 */
public class DoubleRange {

	// Ending value of the range.
	private Double end;

	// Starting value of the range.
	private Double start;

	/**
	 * Initializes a DoubleRange with a given starting double value and ending
	 * double value that bound the range.
	 * 
	 * @param start
	 *          the starting double value of the range.
	 * 
	 * @param end
	 *          the ending double value of the range.
	 */
	public DoubleRange(double start, double end) {
		this.start = new Double(start);
		this.end = new Double(end);
	}

	/**
	 * Initializes a DoubleRange with a given starting Double value and ending
	 * Double value that bound the range.
	 * 
	 * @param start
	 *          the starting Double value of the range.
	 * 
	 * @param end
	 *          the ending Double value of the range.
	 */
	public DoubleRange(Double start, Double end) {
		this.start = start;
		this.end = end;
	}

	/**
	 * Returns the ending Double value of the range.
	 * 
	 * @return the ending Double value of the range.
	 */
	public Double getEnd() {
		return end;
	}

	/**
	 * Returns the starting Double value of the range.
	 * 
	 * @return the starting Double value of the range.
	 */
	public Double getStart() {
		return start;
	}
}
