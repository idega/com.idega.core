package com.idega.data.query;

/**
 * Special column to represent For SELECT * FROM ...
 * 
 * @author <a href="joe@truemesh.com">Joe Walnes </a>
 */
public class SumColumn extends Column {

	public SumColumn(Table table, String columnName) {
		super(table, columnName);
		setPrefix("SUM(");
		setPostfix(")");
	}

	public SumColumn(String columnName) {
		super(columnName);
		setPrefix("SUM(");
		setPostfix(")");
	}
}