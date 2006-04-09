package com.idega.util.dbschema;

import java.sql.Connection;
import java.sql.Statement;

import com.idega.util.IWTimestamp;
/**
 * 
 * 
 *  Last modified: $Date: 2006/04/09 12:13:19 $ by $Author: laddi $
 * 
 * @author <a href="mailto:aron@idega.com">aron</a>
 * @version $Revision: 1.2 $
 */
public class InformixSchemaAdapter extends SQLSchemaAdapter {
	
	InformixSchemaAdapter() {
		this.useTransactionsInSchemaCreation = false;
		IWTimestamp.CUT_MILLISECONDS_OFF_IN_TOSTRING=false;
	}
	public String getSQLType(String javaClassName, int maxlength) {
		String theReturn;
		if (javaClassName.equals("java.lang.Integer")) {
			theReturn = "INTEGER";
		}
		else if (javaClassName.equals("java.lang.String")) {
			if (maxlength < 0) {
				theReturn = "VARCHAR(255)";
			}
			else if (maxlength <= 255) {
				theReturn = "VARCHAR(" + maxlength + ")";
			}
			else if (maxlength <= 2000) {
				theReturn = "LVARCHAR";
			}
			else {
				theReturn = "TEXT";
			}
		}
		else if (javaClassName.equals("java.lang.Boolean")) {
			theReturn = "CHAR(1)";
		}
		else if (javaClassName.equals("java.lang.Float")) {
			theReturn = "FLOAT";
		}
		else if (javaClassName.equals("java.lang.Double")) {
			theReturn = "FLOAT(15)";
		}
		else if (javaClassName.equals("java.sql.Timestamp")) {
			theReturn = "DATETIME YEAR TO FRACTION";
		}
		else if (javaClassName.equals("java.sql.Date") || javaClassName.equals("java.util.Date")) {
			theReturn = "DATE";
		}
		else if (javaClassName.equals("java.sql.Blob")) {
			theReturn = "BYTE";
		}
		else if (javaClassName.equals("java.sql.Time")) {
			theReturn = "DATETIME HOUR TO FRACTION";
		}
		else if (javaClassName.equals("com.idega.util.Gender")) {
			theReturn = "VARCHAR(1)";
		}
		else if (javaClassName.equals("com.idega.data.BlobWrapper")) {
			theReturn = "BYTE";
		}
		else {
			theReturn = "";
		}
		return theReturn;
	}
	public String getIDColumnType(Schema entity) {
		return "SERIAL";
	}
	/**
	 * On Informix the generated ID column is implemented as a serial column and no Trigger not used yet
	 */
	public void createTrigger(Schema entity) throws Exception {
		createSequence(entity);
		
	}
	public void createSequence(Schema entity) throws Exception {
		String sql = "create table " + this.getInformixSequenceTableName(entity) + "(" + entity.getPrimaryKey().getColumn().getSQLName() + " serial)";
		executeUpdate(sql);
		
	}
	public void removeSchema( Schema entity) throws Exception {
		/**
		 * @todo change
		 */
		//deleteTrigger(entity);
		deleteSequence(entity);
		super.removeSchema(entity);
	}
	protected void deleteSequence(Schema entity) throws Exception {
		executeUpdate("drop table " + this.getInformixSequenceTableName(entity));
	}
	
	
	/**
	**Creates a unique ID for the ID column
	**/
	public int createUniqueID(Schema entity) throws Exception {
		int returnInt = -1;
		//String query = "insert into " + this.getInformixSequenceTableName(entity) + "(" + entity.getIDColumnName() + ") values (0)";
		String query = "insert into " + this.getInformixSequenceTableName(entity) + "(" + entity.getPrimaryKey().getColumn().getSQLName()+ ") values (0)";
		Connection conn = null;
		Statement stmt = null;
		try {
			conn = getConnection();
			stmt = conn.createStatement();
			stmt.executeUpdate(query);
			com.informix.jdbc.IfxStatement ifxStatement = (com.informix.jdbc.IfxStatement) stmt;
			returnInt = ifxStatement.getSerial();
		}
		finally {
			if (stmt != null) {
				stmt.close();
			}
			if (conn != null) {
				freeConnection(conn);
			}
		}
		return returnInt;
	}
	private String getInformixSequenceTableName(Schema entity) {
		String entityName = entity.getSQLName();
		return entityName + "_seq";
	}
	
	
	/*protected void insertBlob(IDOLegacyEntity entity)throws Exception{
	  checkBlobTable();
	  int id = insertIntoBlobTable(entity);
	  System.out.print("id from blob = "+id);
	  //this.updateRealTable(id,entity);
	}*/
	
	protected void createForeignKey(
		String baseTableName,
		String columnName,
		String refrencingTableName,
		String referencingColumnName)
		throws Exception {
		String SQLCommand =
			"ALTER TABLE "
				+ baseTableName
				+ " ADD CONSTRAINT FOREIGN KEY ("
				+ columnName
				+ ") REFERENCES "
				+ refrencingTableName
				+ "("
				+ referencingColumnName
				+ ")";
		executeUpdate( SQLCommand);
	}
	protected String getCreatePrimaryKeyStatementBeginning(String tableName) {
		return "alter table " + tableName + " add constraint primary key (";
	}

}
