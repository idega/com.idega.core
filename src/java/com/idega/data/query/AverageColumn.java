package com.idega.data.query;

/**
 * Special column to represent For SELECT * FROM ...
 * 
 * @author <a href="joe@truemesh.com">Joe Walnes </a>
 */
public class AverageColumn extends Column {

	public AverageColumn(Table table, String columnName) {
		super(table, columnName);
		setPrefix("AVG(");
		setPostfix(")");
	}

	public AverageColumn(String columnName) {
		super(columnName);
		setPrefix("AVG(");
		setPostfix(")");
	}
}