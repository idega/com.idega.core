package com.idega.util.dbschema;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

import com.idega.data.SimpleQuerier;
import com.idega.util.database.ConnectionBroker;
/**
 * 
 * 
 *  Last modified: $Date: 2004/11/01 10:05:31 $ by $Author: aron $
 * 
 * @author <a href="mailto:aron@idega.com">aron</a>
 * @version $Revision: 1.1 $
 */
public class SapDBSchemaAdapter extends SQLSchemaAdapter {
	SapDBSchemaAdapter() {
		super.useTransactionsInSchemaCreation = false;
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
			else if (maxlength <= 8000) {
				theReturn = "VARCHAR(" + maxlength + ")";
			}
			else {
				theReturn = "LONG VARCHAR";
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
			theReturn = "TIMESTAMP";
		}
		else if (javaClassName.equals("java.sql.Date") || javaClassName.equals("java.util.Date")) {
			theReturn = "DATE";
		}
		else if (javaClassName.equals("java.sql.Blob")) {
			theReturn = "BLOB";
		}
		else if (javaClassName.equals("java.sql.Time")) {
			theReturn = "TIME";
		}
		else if (javaClassName.equals("com.idega.util.Gender")) {
			theReturn = "VARCHAR(1)";
		}
		else if (javaClassName.equals("com.idega.data.BlobWrapper")) {
			theReturn = "LONG BYTE";
		}
		else {
			theReturn = "";
		}
		return theReturn;
	}
	
	/* (non-Javadoc)
	 * @see com.idega.data.store.DatastoreInterface#createTrigger( com.idega.data.EntityDefinition)
	 */
	public void createTrigger(Schema entity) throws Exception {
		createSequence(entity);
		
	}
	public void createSequence(Schema entity) throws Exception {
		createSequence(entity, 1);
	}
	public void createSequence(Schema entity, int startNumber) throws Exception {
		String seqCreate =
			"create sequence "
				+ entity.getSQLName()
				+ "_seq INCREMENT BY 1 START WITH "
				+ startNumber
				+ " MAXVALUE 1.0E28 MINVALUE 0 NOCYCLE CACHE 20 NOORDER";
		executeUpdate(seqCreate);
	}

	public boolean updateTriggers(Schema entity, boolean createIfNot) throws Exception {
		Connection conn = null;
		Statement Stmt = null;
		ResultSet rs = null;
		boolean returner = false;
		try {
			conn = getConnection();
			Stmt = conn.createStatement();
			boolean sequenceExists = false;
			
			String seqSQL = "select * from DOMAIN.SEQUENCES where SEQUENCE_NAME = '"+getSequenceName(entity)+"'";
			try {
				rs = Stmt.executeQuery(seqSQL.toUpperCase());
				if (rs != null && rs.next()) {					
					sequenceExists = true;
				}
			} catch (Exception e) {
				e.printStackTrace();
				log("Error finding sequence table");
			} finally {
				rs.close();
			}
			
			if (sequenceExists) {
				returner = true; 
			}
			else if (createIfNot) {				
				String maxSQL = "select max ("+entity.getPrimaryKey().getColumn().getSQLName()+") as MAX_ID from "+entity.getSQLName();
				int valueToSet = 1;
				try {
					rs = Stmt.executeQuery(maxSQL);
					if (rs != null && rs.next()) {
						String sMax = rs.getString("MAX_ID");					
						if (sMax != null) {
								valueToSet = Integer.parseInt(sMax);
						}						
					}
					createSequence(entity, valueToSet);
				}
				catch (NumberFormatException e) {
					//UPDATE TRIGGER ignored
					//Not numeric value in primary key field in table "+entity.getEntityName())
				}
				catch (Exception e) {
					e.printStackTrace();
				} finally {
					rs.close();
				}
				returner = true;
			}
		}
		finally {
			if (Stmt != null) {
				Stmt.close();
			}
			if (conn != null) {
				freeConnection(conn);
			}
		}
		return returner;
	}

	public void removeSchema(Schema entity) throws Exception {
		//deleteTrigger(entity);
		deleteSequence(entity);
		super.removeSchema(entity);
	}
	protected void deleteTrigger(Schema entity) throws Exception {
		executeUpdate("drop trigger " + entity.getSQLName() + "_trig");
		
	}
	protected void deleteSequence(Schema entity) throws Exception {
		executeUpdate("drop sequence " + entity.getSQLName() + "_seq");
	}
	
	
	protected String getCreateUniqueIDQuery(Schema entity) {
		return "SELECT " + getSequenceName(entity) + ".NEXTVAL FROM dual";
	}
	private String getSequenceName(Schema entity) {
		String entityName = entity.getSQLName();
		return entityName + "_seq";
	}
	public void setNumberGeneratorValue(Schema entity, int value) {
		//throw new RuntimeException("setSequenceValue() not implemented for "+this.getClass().getName());
		//String statement = "update sequences set last_number="+value+" where sequence_name='"+this.getSequenceName(entity)+"'";
		String statement = "drop sequence " + this.getSequenceName(entity);
		try {
			this.executeUpdate( statement);
			this.createSequence(entity, value + 1);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	public static boolean correctSequenceValue(Connection conn) {
		boolean theReturn = true;
		String[] types = new String[2];
		types[0] = "TABLE";
		types[1] = "VIEW";
		try {
			java.sql.ResultSet RS = getInstanceDetected(conn).getDatabaseMetaData().getTables(null, null, "%", types);
			while (RS.next()) {
				try {
					String tableName = RS.getString("TABLE_NAME");
					System.err.println("tableName = " + tableName);
					boolean value = correctSequenceValue(conn, tableName);
					System.err.println("done = " + value);
				}
				catch (Exception ex) {
					ex.printStackTrace();
					theReturn = false;
				}
			}
		}
		catch (Exception e) {
			e.printStackTrace();
			theReturn = false;
		}
		return theReturn;
	}
	public static boolean correctSequenceValue(Connection conn, String tableName) {
		boolean theReturn = false;
		String startNumberStatement = "select max(" + tableName + "_id) from " + tableName;
		String statement = "drop sequence " + tableName + "_seq";
		try {
			int value = SimpleQuerier.executeIntQuery(startNumberStatement, conn);
			if (value != -1) {
				executeUpdate(conn, statement);
				createSequence(conn, tableName, value + 1);
				theReturn = true;
			}
			else {
				theReturn = false;
			}
		}
		catch (Exception e) {
			e.printStackTrace();
			theReturn = false;
		}
		return theReturn;
	}
	private static void createSequence(Connection conn, String tableName, int startNumber) throws Exception {
		Statement Stmt = null;
		try {
			Stmt = conn.createStatement();
			String seqCreate =
				"create sequence "
					+ tableName
					+ "_seq INCREMENT BY 1 START WITH "
					+ startNumber
					+ " MAXVALUE 1.0E28 MINVALUE 0 NOCYCLE CACHE 20 NOORDER";
			Stmt.executeUpdate(seqCreate);
		}
		finally {
			if (Stmt != null) {
				Stmt.close();
			}
			//            if (conn != null){
			//                    entity.freeConnection(conn);
			//            }
		}
	}
	private static int executeUpdate(Connection conn, String SQLCommand) throws Exception {
		Statement Stmt = null;
		int theReturn = 0;
		try {
			Stmt = conn.createStatement();
			System.out.println(SQLCommand);
			theReturn = Stmt.executeUpdate(SQLCommand);
		}
		finally {
			if (Stmt != null) {
				Stmt.close();
			}
			//      if (conn != null) {
			//        entity.freeConnection(conn);
			//      }
		}
		return theReturn;
	}
	
	public Map getTableIndexes(String dataSourceName, String tableName) {
		Connection conn = null;
		ResultSet rs = null;
		Statement Stmt = null;
		HashMap hm = new HashMap();
		try {
			conn = ConnectionBroker.getConnection(dataSourceName);
			Stmt = conn.createStatement();

			rs = Stmt.executeQuery("select INDEXNAME as INDEX_NAME, COLUMNNAME as COLUMN_NAME from DOMAIN.INDEXCOLUMNS where TABLENAME = '"+tableName.toUpperCase()+"'");
			//			Check for upper case
			handleIndexRS(rs, hm);
			rs.close();

			//			Check for lower case
			if (hm.isEmpty()) {
				rs = Stmt.executeQuery("select INDEXNAME as INDEX_NAME, COLUMNNAME as COLUMN_NAME from DOMAIN.INDEXCOLUMNS where TABLENAME = '"+tableName.toLowerCase()+"'");
				handleIndexRS(rs, hm);
				rs.close();
			}

			//			Check without any case manipulating, this can be removed if we always
			// force uppercase
			if (hm.isEmpty()) {
				rs = Stmt.executeQuery("select INDEXNAME as INDEX_NAME, COLUMNNAME as COLUMN_NAME from DOMAIN.INDEXCOLUMNS where TABLENAME = '"+tableName+"'");
				handleIndexRS(rs, hm);
				rs.close();
			}

		}
		catch (SQLException e) {
			e.printStackTrace();
		}
		finally {
			try {
				if (rs != null) {
					rs.close();
				}
				if (Stmt != null) {
					Stmt.close();
				}
			} catch (Exception e) {
				logWarning("Failed to close ResultSet or Statement ("+e.getMessage()+")");
			}
			if (conn != null) {
				ConnectionBroker.freeConnection(conn);
			}
		}

		return hm;
		/*
		 * if(v!=null && !v.isEmpty()) return (String[])v.toArray(new String[0]);
		 * return null;
		 */
	}	
}
