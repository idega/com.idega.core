//idega 2000 - Tryggvi Larusson
/*

 *Copyright 2000 idega.is All Rights Reserved.

 */
package com.idega.data;

import java.sql.Connection;
import java.sql.ResultSet;
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
			//else if (maxlength<=4000){
			/*
			 * else{ theReturn = "VARCHAR("+maxlength+")"; }
			 */
			else {
				theReturn = "LONGVARCHAR";
			}
		} else if (javaClassName.equals("java.lang.Boolean")) {
			theReturn = "CHAR(1)";
		} else if (javaClassName.equals("java.lang.Float")) {
			theReturn = "REAL";
		} else if (javaClassName.equals("java.lang.Double")) {
			theReturn = "REAL";
		} else if (javaClassName.equals("java.sql.Timestamp")) {
			theReturn = "DATE";
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

	protected void createForeignKey(GenericEntity entity, String baseTableName,
			String columnName, String refrencingTableName,
			String referencingColumnName) throws Exception {
		String SQLCommand = "ALTER TABLE " + baseTableName + " ADD CONSTRAINT "
				+ columnName + refrencingTableName + referencingColumnName
				+ " FOREIGN KEY " + columnName + " REFERENCES "
				+ refrencingTableName + "(" + referencingColumnName + ")";
		executeUpdate(entity, SQLCommand);
	}

	protected String getCreatePrimaryKeyStatementBeginning(String tableName) {
		return "alter table " + tableName + " add constraint " + tableName
				+ "_PK UNIQUE (";
	}

	public void createTrigger(GenericEntity entity) throws Exception {
		//super.executeQuery(entity,"CREATE TRIGGER ins_after BEFORE INSERT ON
		// "+entity.getTableName()+" CALL \""+this.getClass().getName()+"\"");
	}

	public String getIDColumnType() {
		return "INTEGER IDENTITY";
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

	protected void executeBeforeInsert(GenericEntity entity) throws Exception {
		if (entity.isNull(entity.getIDColumnName())) {
			entity.setID(createUniqueID(entity));
		}
	}

	protected void insertBlob(GenericEntity entity) throws Exception {
		//Use the standard implementation
		super.insertBlob(entity);
	}

	protected String getCreateUniqueIDQuery(GenericEntity entity) {
		return "insert into " + getSequenceTableName(entity) + "("
				+ entity.getIDColumnName() + ") values(null)";
	}

	private static String getOracleSequenceName(GenericEntity entity) {
		String entityName = entity.getTableName();
		return entityName + "_seq";
		/*
		 * if (entityName.endsWith("_")){
		 * 
		 * return entityName+"seq"; }
		 * 
		 * else{
		 * 
		 * return entityName+"_seq"; }
		 */
	}

	public String getSequenceTableName(GenericEntity entity) {
		//return "seq_"+entity.getTableName();
		return entity.getTableName();
	}

	/**
	 * 
	 * *Creates a unique ID for the ID column
	 *  
	 */
	public int createUniqueID(GenericEntity entity) throws Exception {
		int returnInt = -1;
		Connection conn = null;
		Statement stmt = null;
		ResultSet RS = null;
		try {
			conn = entity.getConnection();
			stmt = conn.createStatement();
			stmt.executeUpdate(getCreateUniqueIDQuery(entity));
			stmt.close();
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
			if (conn != null) {
				entity.freeConnection(conn);
			}
		}
		return returnInt;
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
	
}