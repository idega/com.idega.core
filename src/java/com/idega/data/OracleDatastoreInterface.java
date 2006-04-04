//idega 2000 - Tryggvi Larusson
/*
*Copyright 2000-2002 idega.is All Rights Reserved.
*/
package com.idega.data;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Vector;

import oracle.jdbc.OracleResultSet;
import oracle.sql.CLOB;

import com.idega.idegaweb.IWMainApplication;
import com.idega.util.database.ConnectionBroker;

/**
 * A class for database abstraction for the Oracle Database.
 * This is an implemention that overrides implementations from com.idega.data.DatastoreInterface 
 * and performs specific functionality to the Oracle JDBC driver and database.
 * Copyright 2000-2002 idega software All Rights Reserved.
 * @author <a href="mailto:tryggvi@idega.is">Tryggvi Larusson</a>
 * @version 1.0
 */
public class OracleDatastoreInterface extends DatastoreInterface {

	public static Locale oracleLocale = null;
	
	protected OracleDatastoreInterface() {
		super();
		EntityControl.limitTableNameToThirtyCharacters = true;
	}
	
	public String getSQLType(String javaClassName, int maxlength) {
		String theReturn;
		if (javaClassName.equals("java.lang.Integer")) {
			theReturn = "NUMBER";
		}
		else
			if (javaClassName.equals("java.lang.String")) {
				if (maxlength < 0) {
					theReturn = "VARCHAR2(255)";
				}
				else
					if (maxlength <= 4000) {
						theReturn = "VARCHAR2(" + maxlength + ")";
					}
					else {
						theReturn = "CLOB";
					}
			}
			else
				if (javaClassName.equals("java.lang.Boolean")) {
					theReturn = "CHAR(1)";
				}
				else
					if (javaClassName.equals("java.lang.Float")) {
						theReturn = "FLOAT";
					}
					else
						if (javaClassName.equals("java.lang.Double")) {
							theReturn = "FLOAT(15)";
						}
						else
							if (javaClassName.equals("java.sql.Timestamp")) {
								theReturn = "DATE";
							}
							else
								if (javaClassName.equals("java.sql.Date") || javaClassName.equals("java.util.Date")) {
									theReturn = "DATE";
								}
								else
									if (javaClassName.equals("java.sql.Blob")) {
										theReturn = "BLOB";
									}
									else
										if (javaClassName.equals("java.sql.Time")) {
											theReturn = "TIME";
										}
										else
											if (javaClassName.equals("com.idega.util.Gender")) {
												theReturn = "VARCHAR(1)";
											}
											else
												if (javaClassName.equals("com.idega.data.BlobWrapper")) {
													theReturn = "BLOB";
												}
												else {
													theReturn = "";
												}
		return theReturn;
	}
	
	public void createTrigger(GenericEntity entity) throws Exception {
		createSequence(entity);
		Connection conn = null;
		Statement Stmt = null;
		try {
			conn = entity.getConnection();
			Stmt = conn.createStatement();
			Stmt.executeUpdate("CREATE TRIGGER " + entity.getTableName() + "_trig BEFORE INSERT ON " + entity.getTableName() + " FOR EACH ROW WHEN (NEW." + entity.getIDColumnName() + " is null) DECLARE TEMP INTEGER; BEGIN SELECT " + entity.getTableName() + "_seq.NEXTVAL INTO TEMP FROM DUAL; :NEW." + entity.getIDColumnName() + ":=TEMP;END;");
		}
		finally {
			if (Stmt != null) {
				Stmt.close();
			}
			if (conn != null) {
				entity.freeConnection(conn);
			}
		}
	}
	
	/* Not Tested
	public boolean updateTriggers(GenericEntity entity, boolean createIfNot) throws Exception {
		Connection conn = null;
		Statement Stmt = null;
		ResultSet rs = null;
		ResultSet rs2 = null;
		boolean returner = false;
		try {
			conn = entity.getConnection();
			Stmt = conn.createStatement();
			String seqSQL = "select LAST_NUMBER from user_sequences where SEQUENCE_NAME like '" + entity.getTableName() + "'";
			rs = Stmt.executeQuery(seqSQL);
			if (rs != null && rs.next()) {
				returner = true;
			}
			if (!returner && createIfNot) {
				String maxSQL = "select max ("+entity.getIDColumnName()+" as MAX from "+entity.getEntityName();
				int valueToSet = 1;
				rs2 = Stmt.executeQuery(maxSQL);
				if (rs2 != null && rs2.next()) {
					valueToSet = Integer.parseInt(rs2.getString("MAX"));
				}
				
				createSequence(entity, valueToSet);
				returner = true;
			}
		}
		finally {
			if (Stmt != null) {
				Stmt.close();
			}
			if (rs != null) {
				rs.close();
			}
			if (rs2 != null) {
				rs2.close();
			}
			if (conn != null) {
				entity.freeConnection(conn);
			}
		}
		return returner;
	}*/
	
	public void createSequence(GenericEntity entity) throws Exception {
		createSequence(entity, 1);
	}
	
	public void createSequence(GenericEntity entity, int startNumber) throws Exception {
		Connection conn = null;
		Statement Stmt = null;
		try {
			conn = entity.getConnection();
			Stmt = conn.createStatement();
			String seqCreate = "create sequence " + entity.getTableName() + "_seq INCREMENT BY 1 START WITH " + startNumber + " MAXVALUE 1.0E28 MINVALUE 0 NOCYCLE CACHE 20 NOORDER";
			Stmt.executeUpdate(seqCreate);
		}
		finally {
			if (Stmt != null) {
				Stmt.close();
			}
			if (conn != null) {
				entity.freeConnection(conn);
			}
		}
	}

	public void deleteEntityRecord(GenericEntity entity) throws Exception {
		super.deleteEntityRecord(entity);
		deleteTrigger(entity);
		deleteSequence(entity);
	}
	
	protected void deleteTrigger(GenericEntity entity) throws Exception {
		Connection conn = null;
		Statement Stmt = null;
		try {
			conn = entity.getConnection();
			Stmt = conn.createStatement();
			Stmt.executeUpdate("drop trigger " + entity.getTableName() + "_trig");
		}
		finally {
			if (Stmt != null) {
				Stmt.close();
			}
			if (conn != null) {
				entity.freeConnection(conn);
			}
		}
	}
	
	protected void deleteSequence(GenericEntity entity) throws Exception {
		Connection conn = null;
		Statement Stmt = null;
		try {
			conn = entity.getConnection();
			Stmt = conn.createStatement();
			Stmt.executeUpdate("drop sequence " + entity.getTableName() + "_seq");
		}
		finally {
			if (Stmt != null) {
				Stmt.close();
			}
			if (conn != null) {
				entity.freeConnection(conn);
			}
		}
	}
	
	protected void executeBeforeInsert(GenericEntity entity) throws Exception {
		if (entity.isNull(entity.getIDColumnName())) {
			entity.setID(createUniqueID(entity));
		}
	}

	protected String getCreateUniqueIDQuery(GenericEntity entity) {
		return "SELECT " + getOracleSequenceName(entity) + ".nextval FROM dual";
	}
	
	private static String getSequenceName(GenericEntity entity) {
		return getOracleSequenceName(entity);
	}
	
	private static String getOracleSequenceName(GenericEntity entity) {
		String entityName = entity.getTableName();
		return entityName + "_seq";
	}

	public void setNumberGeneratorValue(GenericEntity entity, int value) {
		String statement = "drop sequence " + OracleDatastoreInterface.getSequenceName(entity);
		try {
			this.executeUpdate(entity, statement);
			this.createSequence(entity, value + 1);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Override in subclasses
	 **/
	public void onConnectionCreate(Connection newConn) {
		try {
			Locale locale = getDefaultLocale();
			if (locale != null) {
				String country = locale.getDisplayCountry(Locale.ENGLISH).toUpperCase();
				String language = locale.getDisplayLanguage(Locale.ENGLISH).toUpperCase();
	
				Statement stmt = newConn.createStatement();
				stmt.execute("ALTER SESSION SET NLS_LANGUAGE='" + language + "'");
				stmt.close();
				stmt = newConn.createStatement();
				stmt.execute("ALTER SESSION SET NLS_TERRITORY='" + country + "'");
				stmt.close();
				System.out.println("OracleDatastoreInterface: Setting language environment variable for Oracle to " + language + "/" + country + ".");
			}
			
			Statement stmt = newConn.createStatement();
			stmt.execute("ALTER SESSION SET NLS_DATE_FORMAT='YYYY-MM-DD HH24:MI:SS'");
			stmt.close();
			stmt = newConn.createStatement();
			stmt.execute("ALTER SESSION SET NLS_TIMESTAMP_FORMAT='YYYY-MM-DD HH24:MI:SS'");
			stmt.close();
			System.out.println("OracleDatastoreInterface: Setting date format environment variable for Oracle.");
		}
		catch (SQLException sqle) {
			System.err.println("OracleDatastoreInterface: Error when changing environment variable: " + sqle.getMessage());
			sqle.printStackTrace();
		}
	}
	
	/**
	 * This is a callback method and is called by the idegaWeb when it starts up and connects to the Oracle database first<br>.
	 * This is overrided to create the 'set_nls_date_formats' logon trigger.
	 */
	public void onApplicationStart(Connection newConn) {
		try {
			onConnectionCreate(newConn);

			Locale locale = getDefaultLocale();
			if (locale != null) {
				String country = locale.getDisplayCountry(Locale.ENGLISH).toUpperCase();
				String language = locale.getDisplayLanguage(Locale.ENGLISH).toUpperCase();
				
				Statement stmt = newConn.createStatement();
				stmt.execute("CREATE OR REPLACE TRIGGER set_nls_language "+
						"AFTER LOGON ON SCHEMA "+
						"BEGIN "+
						"EXECUTE IMMEDIATE ('ALTER SESSION SET NLS_LANGUAGE=''" + language + "'''); "+
						"EXECUTE IMMEDIATE ('ALTER SESSION SET NLS_TERRITORY=''" + country + "''');"+
						"END;");
				stmt.close();
				System.out.println("OracleDatastoreInterface: Creating logon trigger 'set_nls_language' for setting NLS_LANGUAGE='" + language + "' and NLS_TERRITORY='" + country + "'");
			}
			
			Statement stmt = newConn.createStatement();
			stmt.execute("CREATE OR REPLACE TRIGGER set_nls_date_formats "+
					"AFTER LOGON ON SCHEMA "+
					"BEGIN "+
					"EXECUTE IMMEDIATE ('ALTER SESSION SET NLS_DATE_FORMAT=''YYYY-MM-DD HH24:MI:SS'''); "+
					"EXECUTE IMMEDIATE ('ALTER SESSION SET NLS_TIMESTAMP_FORMAT=''YYYY-MM-DD HH24:MI:SS''');"+
					"END;");
			stmt.close();
			System.out.println("OracleDatastoreInterface: Creating logon trigger 'set_nls_date_formats' for setting NLS_DATE_FORMAT and NLS_TIMESTAMP_FORMAT");
		}
		catch (SQLException sqle) {
			System.err.println("OracleDatastoreInterface: creating logon trigger: " + sqle.getMessage());
			sqle.printStackTrace();
		}
	}
	
	private Locale getDefaultLocale() {
		if (oracleLocale == null) {
			oracleLocale = IWMainApplication.getDefaultIWApplicationContext().getApplicationSettings().getDefaultLocaleFromIWPropertyList();
		}
		return oracleLocale;
	}

	/**
	 * Varchar is limited to 4000 chars need to use clob for larger fields. Great example http://www.experts-exchange.com/Databases/Oracle/Q_20358143.html
	 * @see com.idega.data.DatastoreInterface#fillStringColumn(GenericEntity, String, ResultSet)
	 */
	protected void fillStringColumn(GenericEntity entity, String columnName, ResultSet rs) throws SQLException {

		int maxlength = entity.getMaxLength(columnName);
		if (maxlength <= 4000) {
			String string = rs.getString(columnName);
			if (string != null) {
				entity.setColumn(columnName, string);
			}
		}
		else {
			try {
				Reader chrInstream; // Unicode clob reader
				char chrBuffer[]; // Clob buffer
				try{
					CLOB clob = ((OracleResultSet) rs).getCLOB(columnName);
					if (clob != null) {
						//set buffersize
						chrBuffer = new char[(int) clob.length()];
	
						// Now get as a unicode stream.
						chrInstream = clob.getCharacterStream();
	
						if (chrInstream != null) {
							chrInstream.read(chrBuffer);
	
							String value = new String(chrBuffer);
							entity.setColumn(columnName, value);
						}
					}
				}
				catch(ClassCastException cce){
					String eMessage = cce.getMessage();
					String message = "Error filling CLOB column for Oracle. Unexpected JDBC Resultset implementation class: ";
					if(eMessage!=null){
						message += eMessage;
					}
					System.err.println(message);
				}

			}
			catch (IOException io) {
				throw new SQLException("IOException: " + io.getMessage());
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	protected void setStringForPreparedStatement(String columnName, PreparedStatement statement, int index, GenericEntity entity) throws SQLException {
		try {
			int maxlength = entity.getMaxLength(columnName);
			if (maxlength <= 4000) {
				statement.setString(index, entity.getStringColumnValue(columnName));
			}
			else {
				//collect clobs
				String stringValue = entity.getStringColumnValue(columnName);

				Reader reader = new StringReader(stringValue);
				statement.setCharacterStream(index, reader, stringValue.length());
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	/* (non-Javadoc)
	 * @see com.idega.data.DatastoreInterface#getTableColumnNames(java.lang.String, java.lang.String)
	 */
	public String[] getTableColumnNames(String dataSourceName, String tableName) {
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		List columns = new Vector();
		try {
			conn = ConnectionBroker.getConnection(dataSourceName);
			stmt = conn.createStatement();
			rs = stmt.executeQuery("SELECT * FROM USER_TAB_COLUMNS WHERE TABLE_NAME = '" + tableName.toUpperCase() + "'");
			while (rs.next()) {
				columns.add(rs.getString("COLUMN_NAME"));
			}
			rs.close();
			return (String[]) columns.toArray(new String[0]);
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
		finally {
			if (conn != null) {
				ConnectionBroker.freeConnection(conn);
			}
		}
		if (columns != null && !columns.isEmpty())
			return (String[]) columns.toArray(new String[0]);
		return null;
	}
	/* (non-Javadoc)
	 * @see com.idega.data.DatastoreInterface#doesTableExist(java.lang.String, java.lang.String)
	 */
	public boolean doesTableExist(String dataSourceName, String tableName) throws Exception {
		 String checkQuery = "select count(*) from " + tableName;
			try {
				executeQuery(dataSourceName, checkQuery);	
				return true;
			} catch (Exception e) {
				//e.printStackTrace();
				return false;
			}
	}

	public HashMap getTableIndexes(String dataSourceName, String tableName) {
		Connection conn = null;
		ResultSet rs = null;
		Statement Stmt = null;
		HashMap hm = new HashMap();
		try {
			conn = ConnectionBroker.getConnection(dataSourceName);
			Stmt = conn.createStatement();

			rs = Stmt.executeQuery("select * from user_ind_columns where TABLE_NAME = '"+tableName.toUpperCase()+"'");
			//			Check for upper case
			handleIndexRS(rs, hm);
			rs.close();

			//			Check for lower case
			if (hm.isEmpty()) {
				rs = Stmt.executeQuery("select * from user_ind_columns where TABLE_NAME = '"+tableName.toLowerCase()+"'");
				handleIndexRS(rs, hm);
				rs.close();
			}

			//			Check without any case manipulating, this can be removed if we always
			// force uppercase
			if (hm.isEmpty()) {
				rs = Stmt.executeQuery("select * from user_ind_columns where TABLE_NAME = '"+tableName+"'");
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
				logError("Failed to close ResultSet or Statement ("+e.getMessage()+")");
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
	
	public boolean isCabableOfRSScroll(){
		return true;
	}

	/**
	 * returns the optimal or allowed fetch size when going to database to load IDOEntities using 'where primarikey_name in (list_of_priamrykeys)'
	 */
	public int getOptimalEJBLoadFetchSize(){
		return 1000;
	}

}