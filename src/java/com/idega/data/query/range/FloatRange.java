package com.idega.data.query.range;

/**
 * Class FloatRange represents a range of Float values.
 * 
 * @author <a href="mailto:derek@derekmahar.ca">Derek Mahar</a>
 */
public class FloatRange {

	// Ending value of the range.
	private Float end;

	// Starting value of the range.
	private Float start;

	/**
	 * Initializes a FloatRange with a given starting Float value and ending Float
	 * value that bound the range.
	 * 
	 * @param start
	 *          the starting Float value of the range.
	 * 
	 * @param end
	 *          the ending Float value of the range.
	 */
	public FloatRange(float start, float end) {
		this.start = new Float(start);
		this.end = new Float(end);
	}

	/**
	 * Initializes a FloatRange with a given starting Float value and ending Float
	 * value that bound the range.
	 * 
	 * @param start
	 *          the starting Float value of the range.
	 * 
	 * @param end
	 *          the ending Float value of the range.
	 */
	public FloatRange(Float start, Float end) {
		this.start = start;
		this.end = end;
	}

	/**
	 * Returns the ending Float value of the range.
	 * 
	 * @return the ending Float value of the range.
	 */
	public Float getEnd() {
		return end;
	}

	/**
	 * Returns the ending Float value of the range.
	 * 
	 * @return the ending Float value of the range.
	 */
	public Float getStart() {
		return start;
	}
}
