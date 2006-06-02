package com.idega.util.dbschema;

import java.sql.Connection;
import java.sql.Statement;

import com.idega.data.EntityAttribute;
import com.idega.util.IWTimestamp;
/**
 * 
 * 
 *  Last modified: $Date: 2006/06/02 10:19:13 $ by $Author: tryggvil $
 * 
 * @author <a href="mailto:aron@idega.com">aron</a>
 * @version $Revision: 1.4 $
 */
public class InformixSchemaAdapter extends SQLSchemaAdapter {
	
	InformixSchemaAdapter() {
		this.useTransactionsInSchemaCreation = false;
		IWTimestamp.CUT_MILLISECONDS_OFF_IN_TOSTRING=false;
	}
	public String getSQLType(String javaClassName, int maxlength) {
		if (javaClassName.equals("java.lang.Integer")) {
			return "INTEGER";
		}
		if (javaClassName.equals("java.lang.String")) {
			if (maxlength == EntityAttribute.UNLIMITED_LENGTH) {
				return "TEXT";
			}
			if (maxlength < 0) {
				return "VARCHAR(255)";
			}
			if (maxlength <= 255) {
				return "VARCHAR(" + maxlength + ")";
			}
			if (maxlength <= 2000) {
				return "LVARCHAR";
			}
			return "TEXT";
		}
		if (javaClassName.equals("java.lang.Boolean")) {
			return "CHAR(1)";
		}
		if (javaClassName.equals("java.lang.Float")) {
			return "FLOAT";
		}
		if (javaClassName.equals("java.lang.Double")) {
			return "FLOAT(15)";
		}
		if (javaClassName.equals("java.sql.Timestamp")) {
			return "DATETIME YEAR TO FRACTION";
		}
		if (javaClassName.equals("java.sql.Date") || javaClassName.equals("java.util.Date")) {
			return "DATE";
		}
		if (javaClassName.equals("java.sql.Blob")) {
			return "BYTE";
		}
		if (javaClassName.equals("java.sql.Time")) {
			return "DATETIME HOUR TO FRACTION";
		}
		if (javaClassName.equals("com.idega.util.Gender")) {
			return "VARCHAR(1)";
		}
		if (javaClassName.equals("com.idega.data.BlobWrapper")) {
			return "BYTE";
		}
		return "";
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
	
	public boolean getSupportsSlide() {
		return false;
	}

}