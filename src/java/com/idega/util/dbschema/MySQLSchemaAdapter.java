package com.idega.util.dbschema;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import com.idega.data.EntityAttribute;


/**
 * 
 * 
 *  Last modified: $Date: 2006/05/26 16:51:49 $ by $Author: thomas $
 * 
 * @author <a href="mailto:aron@idega.com">aron</a>
 * @version $Revision: 1.5 $
 */

public class MySQLSchemaAdapter extends SQLSchemaAdapter {
	MySQLSchemaAdapter() {
		this.useTransactionsInSchemaCreation = false;
	}

	protected String getCreateUniqueIDQuery(Schema schema) throws Exception{
		return "insert into " + getSequenceTableName(schema) + "("+ schema.getPrimaryKey().getColumn().getSQLName()+ ") values(null)";
	}

	/**
	 * 
	 * *Creates a unique ID for the ID column
	 *  
	 */
	public int createUniqueID(Schema schema) throws Exception {
		int returnInt = -1;
		Connection conn = null;
		Statement stmt = null;
		ResultSet RS = null;
		try {
			conn = getConnection();
			stmt = conn.createStatement();
			stmt.executeUpdate(getCreateUniqueIDQuery(schema));
			stmt.close();
			stmt = conn.createStatement();
			RS = stmt.executeQuery("select last_insert_id()");
			RS.next();
			returnInt = RS.getInt(1);
		}
		finally {
			if (RS != null) {
				RS.close();
			}
			if (stmt != null) {
				stmt.close();
			}
			if (conn != null) {
				freeConnection(conn);
			}
		}
		return returnInt;

	}

	public String getSequenceTableName(Schema schema) {
		return "seq_" + schema.getSQLName();
	}

	public String getSQLType(String javaClassName, int maxlength) {
		if (javaClassName.equals("java.lang.Integer")) {
			return "INTEGER";
		}
		if (javaClassName.equals("java.lang.String")) {
			if (maxlength == EntityAttribute.UNLIMITED_LENGTH) {
				return "LONGTEXT";
			}
			if (maxlength < 0) {
				return "VARCHAR(255)";
			}
			if (maxlength <= 255) {
				return "VARCHAR(" + maxlength + ")";
			}
			return "LONGTEXT";
		}
		if (javaClassName.equals("java.lang.Boolean")) {
			return "CHAR(1)";
		}
		if (javaClassName.equals("java.lang.Float")) {
			return "DOUBLE";
		}
		if (javaClassName.equals("java.lang.Double")) {
			return "DOUBLE";
		}
		if (javaClassName.equals("java.sql.Timestamp")) {
			return "DATETIME";
		}
		if (javaClassName.equals("java.sql.Date")
				|| javaClassName.equals("java.util.Date")) {
			return "DATE";
		}
		if (javaClassName.equals("java.sql.Blob")) {
			return "LONGBLOB";
		}
		if (javaClassName.equals("java.sql.Time")) {
			return "TIME";
		}
		if (javaClassName.equals("com.idega.util.Gender")) {
			return "VARCHAR(1)";
		}
		if (javaClassName.equals("com.idega.data.BlobWrapper")) {
			return "LONGBLOB";
		}
		return "";
	}

	/*
	 * Not Tested public boolean updateTriggers(GenericEntity entity, boolean
	 * createIfNot) throws Exception { Connection conn = null; Statement Stmt =
	 * null; ResultSet rs = null; ResultSet rs2 = null; boolean returner =
	 * false; try { conn = entity.getConnection(); Stmt =
	 * conn.createStatement(); boolean tableExists = false; String seqSQL =
	 * "select * from "+getSequenceTableName(entity); try { rs =
	 * Stmt.executeQuery(seqSQL); tableExists = true; } catch (Exception e) {
	 * log("Error finding sequence table"); } if (rs != null && rs.next()) {
	 * returner = true; } else if (createIfNot) { String maxSQL = "select max
	 * ("+entity.getIDColumnName()+" as MAX from "+entity.getEntityName(); if
	 * (!tableExists) { createSequenceTable(entity); } int valueToSet = 1; rs2 =
	 * Stmt.executeQuery(maxSQL); if (rs2 != null && rs2.next()) { valueToSet =
	 * Integer.parseInt(rs2.getString("MAX")); Stmt.executeUpdate("update
	 * "+getSequenceTableName(entity)+" set "+entity.getIDColumnName()+" =
	 * "+valueToSet); }
	 * 
	 * returner = true; } } finally { if (Stmt != null) { Stmt.close(); } if (rs !=
	 * null) { rs.close(); } if (rs2 != null) { rs2.close(); } if (conn != null) {
	 * entity.freeConnection(conn); } } return returner; }
	 */

	public void createSequenceTable(Schema schema)throws Exception {
		Connection conn = null;
		Statement Stmt = null;
		try {
			conn = getConnection();
			Stmt = conn.createStatement();
			String s = "CREATE table "
					+ getSequenceTableName(schema)
					+ "("
					+ schema.getPrimaryKey().getColumn().getSQLName()
					+ " integer PRIMARY KEY auto_increment)";
			System.out.println(s);
			Stmt.executeUpdate(s);
		} finally {
			if (Stmt != null) {
				Stmt.close();
			}
			if (conn != null) {
				freeConnection(conn);
			}
		}
	}

	public void createTrigger(Schema schema) throws Exception {
		createSequenceTable(schema);
	}

	public void createForeignKeys(Schema schema) throws Exception {
	}

	protected String getCreationStatement(Schema schema) {
		String returnString = "create table " + schema.getSQLName() + "(";
		//String[] names = entity.getColumnNames();
		SchemaColumn[] columns = schema.getColumns();
		//for (int i = 0; i < names.length; i++){
		for (int i = 0; i < columns.length; i++) {
			returnString = returnString
					+ columns[i].getSQLName()
					+ " "
					+ getSQLType(columns[i].getDataTypeClass(), columns[i]
							.getMaxLength());
			if (columns[i].isPartOfPrimaryKey()) {
				returnString = returnString + " PRIMARY KEY auto_increment";
			}
			if (i != columns.length - 1) {
				returnString = returnString + ",";
			}
		}
		returnString = returnString + ")";
		System.out.println(returnString);
		return returnString;
	}

	public String getIDColumnType(Schema schema) {
		String s = "INTEGER";
		if(schema.hasAutoIncrementColumn()) {
			s+=" AUTO_INCREMENT";
		}
		return s;
	}

	public void setNumberGeneratorValue(Schema schema, int value) {
		//throw new RuntimeException("setSequenceValue() not implemented for
		// "+this.getClass().getName());
		String statement = "insert into " + this.getSequenceTableName(schema)
				+ " values(" + value + ")";
		try {
			this.executeUpdate(statement);
		} catch (Exception e) {
			//e.printStackTrace();
			System.err.println("MySQLDatastoreInterface.setNumberGeneratorValue() Exception: "+ e.getMessage());
		}
	}
	
	/**
	 * <p>
	 * This method returns the max length of a column to be part of a (composite) primary key.<br>
	 * This method by default returns -1 which is no limit, but this is overridden here with the value 255 for longer values for MySQL.
	 * </p>
	 * @return
	 */
	public int getMaxColumnPrimaryKeyLength(SchemaColumn column){
		if(column.getMaxLength()>255){
			return 255;
		}
		else{
			return -1;
		}
	}
	
}

