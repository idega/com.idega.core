package com.idega.data.query;

/**
 * Special column to represent For SELECT * FROM ...
 * 
 * @author <a href="joe@truemesh.com">Joe Walnes </a>
 */
public class CountColumn extends Column {

	public CountColumn(Table table, String columnName) {
		super(table, columnName);
		setPrefix("COUNT(");
		setPostfix(")");
	}

	public CountColumn(String columnName) {
		super(columnName);
		setPrefix("COUNT(");
		setPostfix(")");
	}
}