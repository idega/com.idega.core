package com.idega.data.query;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.HashSet;
import java.util.Set;

import com.idega.data.query.output.Output;
import com.idega.data.query.range.BigDecimalRange;
import com.idega.data.query.range.DateRange;
import com.idega.data.query.range.DoubleRange;
import com.idega.data.query.range.FloatRange;
import com.idega.data.query.range.IntegerRange;
import com.idega.data.query.range.StringRange;

/**
 * Class BetweenCriteria is a Criteria extension that generates the SQL syntax
 * for a BETWEEN operator in an SQL Where clause.
 * 
 * @author <a href="mailto:derek@derekmahar.ca">Derek Mahar</a>
 */

public class BetweenCriteria extends Criteria {

	private Column column;
	private StringRange range;

	/**
	 * Initializes a new BetweenCriteria with a BigDecimal column and a BigDecimal
	 * range which serves as the bounds for the SQL BETWEEN operator within which
	 * the operator tests whether the column lies.
	 * 
	 * @param column
	 *          the first operand to the SQL BETWEEN operator that the operator
	 *          uses to test whether the column falls within the given range. The
	 *          SQL type of the column must be DECIMAL or NUMERIC.
	 * 
	 * @param range
	 *          the BigDecimal bounds for the SQL BETWEEN operator within which
	 *          the operator tests whether the column lies.
	 */

	public BetweenCriteria(Column column, BigDecimalRange range) {
		this.column = column;
		this.range = new StringRange(range.getStart().toString(), range.getEnd().toString());
	}

	/**
	 * Initializes a new BetweenCriteria with a Date column and a Date range which
	 * serves as the bounds for the SQL BETWEEN operator within which the operator
	 * tests whether the column lies.
	 * 
	 * @param column
	 *          the first operand to the SQL BETWEEN operator that the operator
	 *          uses to test whether the column falls within the given range. The
	 *          SQL type of the column must be TIMESTAMP.
	 * 
	 * @param range
	 *          the Date bounds for the SQL BETWEEN operator within which the
	 *          operator tests whether the column lies.
	 */

	public BetweenCriteria(Column column, DateRange range) {
		this.column = column;
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S");
		this.range = new StringRange("'" + dateFormat.format(range.getStart()) + "'", "'" + dateFormat.format(range.getEnd()) + "'");
	}

	/**
	 * Initializes a new BetweenCriteria with a Double column and a Double range
	 * which serves as the bounds for the SQL BETWEEN operator within which the
	 * operator tests whether the column lies.
	 * 
	 * @param column
	 *          the first operand to the SQL BETWEEN operator that the operator
	 *          uses to test whether the column falls within the given range. The
	 *          SQL type of the column must be DOUBLE.
	 * 
	 * @param range
	 *          the Double bounds for the SQL BETWEEN operator within which the
	 *          operator tests whether the column lies.
	 */

	public BetweenCriteria(Column column, DoubleRange range) {
		this.column = column;
		this.range = new StringRange(range.getStart().toString(), range.getEnd().toString());
	}

	/**
	 * Initializes a new BetweenCriteria with a Float column and a Float range
	 * which serves as the bounds for the SQL BETWEEN operator within which the
	 * operator tests whether the column lies.
	 * 
	 * @param column
	 *          the first operand to the SQL BETWEEN operator that the operator
	 *          uses to test whether the column falls within the given range. The
	 *          SQL type of the column must be FLOAT.
	 * 
	 * @param range
	 *          the Float bounds for the SQL BETWEEN operator within which the
	 *          operator tests whether the column lies.
	 */

	public BetweenCriteria(Column column, FloatRange range) {
		this.column = column;
		this.range = new StringRange(range.getStart().toString(), range.getEnd().toString());
	}

	/**
	 * Initializes a new BetweenCriteria with a Integer column and a Integer range
	 * which serves as the bounds for the SQL BETWEEN operator within which the
	 * operator tests whether the column lies.
	 * 
	 * @param column
	 *          the first operand to the SQL BETWEEN operator that the operator
	 *          uses to test whether the column falls within the given range. The
	 *          SQL type of the column must be INTEGER.
	 * 
	 * @param range
	 *          the Integer bounds for the SQL BETWEEN operator within which the
	 *          operator tests whether the column lies.
	 */

	public BetweenCriteria(Column column, IntegerRange range) {
		this.column = column;
		this.range = new StringRange(range.getStart().toString(), range.getEnd().toString());
	}

	/**
	 * Writes a BetweenCriteria as an SQL BETWEEN operator to the given output
	 * destination.
	 * 
	 * @param out
	 *          the output destination to which we write the SQL BETWEEN operator.
	 * 
	 * @see com.idega.data.query.Criteria#write(com.idega.data.query.output.Output)
	 */

	public void write(Output out) {
		out.print(column).print(" BETWEEN ").print(range.getStart()).print(" AND ").print(range.getEnd());
	}

	public Set getTables() {
		Set s = new HashSet();
		s.add(this.column.getTable());
		return s;
	}
}