//idega 2000 - Tryggvi Larusson
/*

 *Copyright 2000 idega.is All Rights Reserved.

 */
package com.idega.data;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * 
 * @author <a href="mailto:tryggvi@idega.is">Tryggvi Larusson </a>
 * 
 * @version 1.0
 *  
 */
public class HSQLDatastoreInterface extends DatastoreInterface { //implements
	// org.hsqldb.Trigger{
	public String getSQLType(String javaClassName, int maxlength) {
		String theReturn;
		if (javaClassName.equals("java.lang.Integer")) {
			theReturn = "INTEGER";
		} else if (javaClassName.equals("java.lang.String")) {
			if (maxlength < 0) {
				theReturn = "VARCHAR(255)";
			}
			else if (maxlength<=4000){
				theReturn = "VARCHAR("+maxlength+")";
			}
			else {
				theReturn = "LONGVARCHAR";
			}
		} else if (javaClassName.equals("java.lang.Boolean")) {
			theReturn = "CHAR(1)";
		} else if (javaClassName.equals("java.lang.Float")) {
			theReturn = "DOUBLE";
		} else if (javaClassName.equals("java.lang.Double")) {
			theReturn = "DOUBLE";
		} else if (javaClassName.equals("java.sql.Timestamp")) {
			theReturn = "TIMESTAMP";
		} else if (javaClassName.equals("java.sql.Date")
				|| javaClassName.equals("java.util.Date")) {
			theReturn = "DATE";
		} else if (javaClassName.equals("java.sql.Blob")) {
			theReturn = "LONGVARBINARY";
		} else if (javaClassName.equals("java.sql.Time")) {
			theReturn = "TIME";
		} else if (javaClassName.equals("com.idega.util.Gender")) {
			theReturn = "VARCHAR(1)";
		} else if (javaClassName.equals("com.idega.data.BlobWrapper")) {
			theReturn = "LONGVARBINARY";
		} else {
			theReturn = "";
		}
		return theReturn;
	}
/*
	protected void createForeignKey(GenericEntity entity, String baseTableName,
			String columnName, String refrencingTableName,
			String referencingColumnName) throws Exception {
		String SQLCommand = "ALTER TABLE " + baseTableName + " ADD CONSTRAINT "
				+ columnName + refrencingTableName + referencingColumnName
				+ " FOREIGN KEY " + columnName + " REFERENCES "
				+ refrencingTableName + "(" + referencingColumnName + ")";
		executeUpdate(entity, SQLCommand);
	}
*/
	protected String getCreatePrimaryKeyStatementBeginning(String tableName) {
		return "alter table " + tableName + " add constraint " + tableName
				+ "_PK UNIQUE (";
	}

	public void createTrigger(GenericEntity entity) throws Exception {
		//super.executeQuery(entity,"CREATE TRIGGER ins_after BEFORE INSERT ON
		// "+entity.getTableName()+" CALL \""+this.getClass().getName()+"\"");
	}

	public String getIDColumnType(GenericEntity entity)
	{
		if (entity.getIfAutoIncrement()) {
			return "INTEGER IDENTITY";
		} else {
			return "INTEGER";
		}
	}
	
	public void createSequence(GenericEntity entity) throws Exception {
	}

	public void deleteEntityRecord(GenericEntity entity) throws Exception {
		super.deleteEntityRecord(entity);
		deleteTrigger(entity);
		deleteSequence(entity);
	}

	protected void deleteTrigger(GenericEntity entity) throws Exception {
	}

	protected void deleteSequence(GenericEntity entity) throws Exception {
	}

	protected void executeAfterInsert(GenericEntity entity) throws Exception {
		//if (entity.isNull(entity.getIDColumnName())) {
		//	entity.setID(createUniqueID(entity));
		//}
		super.executeAfterInsert(entity);
	}

	protected void insertBlob(GenericEntity entity) throws Exception {
		//Use the standard implementation
		super.insertBlob(entity);
	}

	/*protected String getCreateUniqueIDQuery(GenericEntity entity) {
		return "insert into " + getSequenceTableName(entity) + "("
				+ entity.getIDColumnName() + ") values(null)";
	}*/


	public String getSequenceTableName(GenericEntity entity) {
		//return "seq_"+entity.getTableName();
		return entity.getTableName();
	}	
	
	
	/**
	 * 
	 * *Creates a unique ID for the ID column
	 *  
	 */
	public int getCreatedUniqueId(GenericEntity entity,Connection conn) throws Exception {
		int returnInt = -1;
		//Connection conn = null;
		Statement stmt = null;
		ResultSet RS = null;
		try {
			//conn = entity.getConnection();
			//stmt = conn.createStatement();
			//stmt.executeUpdate(getCreateUniqueIDQuery(entity));
			//stmt.close();
			stmt = conn.createStatement();
			RS = stmt.executeQuery("CALL IDENTITY()");
			RS.next();
			returnInt = RS.getInt(1);
		} finally {
			if (RS != null) {
				RS.close();
			}
			if (stmt != null) {
				stmt.close();
			}
			/*if (conn != null) {
				entity.freeConnection(conn);
			}*/
		}
		return returnInt;
	}
	
	public boolean updateNumberGeneratedValueAfterInsert(){
		return true;
	}

	/* (non-Javadoc)
	 * @see com.idega.data.DatastoreInterface#updateNumberGeneratedValue(com.idega.data.GenericEntity, java.sql.Connection)
	 */
	protected void updateNumberGeneratedValue(GenericEntity entity,
			Connection conn) {
		int id;
		try {
			if(entity.getEntityDefinition().getPrimaryKeyDefinition().getPrimaryKeyClass()==Integer.class){
				id = this.getCreatedUniqueId(entity,conn);
				entity.setID(id);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}	
	
	
	//Implementing org.hsqldb.Trigger:
	public void fire(String trigName, String tabName, Object row[]) {
		System.out.println(trigName + " trigger fired on " + tabName);
		System.out.print("col 0 value <");
		System.out.print(row[0]);
		System.out.println(">");
		// you can cast row[i] given your knowledge of what the table
		// format is.
	}
	
	
	/**
	 * Returns the string "CREATE CACHED TABLE [tableName]".<br>
	 * This is overrided from the superclass.
	 * @param tableName
	 * @return
	 */
	public String getCreateTableCommand(String tableName){
		return "CREATE CACHED TABLE "+tableName;
	}
	
	
	/**
	 * Override in subclasses
	 **/
	public void onConnectionCreate(Connection newConn) {
		try {
			Statement stmt = newConn.createStatement();
			stmt.execute("SET PROPERTY \"hsqldb.first_identity\" 1");
			stmt.close();
			System.out.println("HSQLDatastoreInterface: Setting first_identity property to 1 for HSQLDB");
		/*	
		 This parameter is set for the OCI driver in a shell script usually but could be set here also
			stmt = newConn.createStatement();
			stmt.execute("ALTER SESSION SET NLS_LANG='.AL32UTF8'");
			stmt.close();
			System.out.println("OracleDatastoreInterface: Setting language environment variable for Oracle to NLS_LANG=.UTF8 for Unicode support.");
		*/
		}
		catch (SQLException sqle) {
			System.err.println("HSQLDatastoreInterface: Error when changing property: " + sqle.getMessage());
			sqle.printStackTrace();
		}
	}	
	
	
	/**
	 * Returns the command for "ALTER TABLE [tableName] ADD <strong>COLUMN</strong> [columnName] [dataType] by default.<br>
	 * This is overrided from the superclass.
	 * @param columnName
	 * @param entity
	 * @return
	 */
	public String getAddColumnCommand(String columnName, GenericEntity entity) {
		String SQLString = "alter table "+entity.getTableName()+" add column "+getColumnSQLDefinition(columnName,entity);
		return SQLString;
	}
	
	public boolean supportsUniqueConstraintInColumnDefinition(){
		//TODO: Implement support for UNIQUE for HSQLDB
		return false;
	}

}