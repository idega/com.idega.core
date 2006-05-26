package com.idega.util.dbschema;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import com.idega.data.EntityAttribute;


/**
 * 
 * @author <a href="mailto:tryggvi@idega.is">Tryggvi Larusson </a>
 * 
 * @version 1.0
 *  
 */
public class HSQLSchemaAdapter extends SQLSchemaAdapter { //implements
	// org.hsqldb.Trigger{
	public String getSQLType(String javaClassName, int maxlength) {
		if (javaClassName.equals("java.lang.Integer")) {
			if (maxlength == EntityAttribute.UNLIMITED_LENGTH) {
				return "BIGINT";
			}
			if(maxlength>10){
				return "BIGINT";
			}
			return "INTEGER";
		}
		if (javaClassName.equals("java.lang.String")) {
			if (maxlength == EntityAttribute.UNLIMITED_LENGTH) {
				return "LONGVARCHAR";
			}
			if (maxlength <= 0) {
				return "VARCHAR(255)";
			}
			if (maxlength<=4000){
				return "VARCHAR("+maxlength+")";
			}
			return "LONGVARCHAR";
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
			return "TIMESTAMP";
		} 
		if (javaClassName.equals("java.sql.Date")
				|| javaClassName.equals("java.util.Date")) {
			return "DATE";
		} 
		if (javaClassName.equals("java.sql.Blob")) {
			return "LONGVARBINARY";
		} 
		if (javaClassName.equals("java.sql.Time")) {
			return "TIME";
		} 
		if (javaClassName.equals("com.idega.util.Gender")) {
			return "VARCHAR(1)";
		} 
		if (javaClassName.equals("com.idega.data.BlobWrapper")) {
			return "LONGVARBINARY";
		} 
		return "";
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

	public void createTrigger( Schema schema) throws Exception {
		//super.executeQuery(entity,"CREATE TRIGGER ins_after BEFORE INSERT ON
		// "+entity.getTableName()+" CALL \""+this.getClass().getName()+"\"");
	}

	/* (non-Javadoc)
	 * @see com.idega.data.store.DatastoreInterface#getIDColumnType(com.idega.data.IDOEntityDefinition)
	 */
	public String getIDColumnType(Schema entity) {
		if (entity.hasAutoIncrementColumn()) {
			return "INTEGER IDENTITY";
		} else {
			return "INTEGER";
		}
	}
	
	public void createSequence(Schema schema) throws Exception {
	}

	/* (non-Javadoc)
	 * @see com.idega.data.store.DatastoreInterface#deleteEntityRecord(java.lang.String, com.idega.data.IDOEntityDefinition)
	 */
	public void removeSchema(Schema schema) throws Exception {
		super.removeSchema(schema);
		deleteTrigger(schema);
		deleteSequence(schema);
	}

	protected void deleteTrigger(Schema schema) throws Exception {
	}

	protected void deleteSequence(Schema schema) throws Exception {
	}

	

	/*protected String getCreateUniqueIDQuery(GenericEntity entity) {
		return "insert into " + getSequenceTableName(entity) + "("
				+ entity.getIDColumnName() + ") values(null)";
	}*/


	public String getSequenceTableName(Schema schema) {
		//return "seq_"+entity.getTableName();
		return schema.getSQLName();
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
	public String getAddColumnCommand(SchemaColumn column, Schema schema) {
		String SQLString = "alter table "+schema.getSQLName()+" add column "+getColumnSQLDefinition(column,schema);
		return SQLString;
	}
	
	public boolean supportsUniqueConstraintInColumnDefinition(){
		//TODO: Implement support for UNIQUE for HSQLDB
		return false;
	}

}