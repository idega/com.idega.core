package com.idega.data.query;

/**
 * Special column to represent For SELECT * FROM ...
 * 
 * @author <a href="joe@truemesh.com">Joe Walnes </a>
 */
public class MaxColumn extends Column {

	public MaxColumn(Table table, String columnName) {
		super(table, columnName);
		setPrefix("MAX(");
		setPostfix(")");
	}

	public MaxColumn(String columnName) {
		super(columnName);
		setPrefix("MAX(");
		setPostfix(")");
	}
}