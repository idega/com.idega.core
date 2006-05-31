/*
 * $Id: SQLSchemaAdapter.java,v 1.12 2006/05/31 10:51:36 tryggvil Exp $
 *
 * Copyright (C) 2001 Idega hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 *
 */
package com.idega.util.dbschema;

import java.sql.Array;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Collection;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.idega.idegaweb.IWMainApplication;
import com.idega.repository.data.MutableClass;
import com.idega.util.IWTimestamp;
import com.idega.util.database.ConnectionBroker;
import com.idega.util.logging.LoggingHelper;

/**
 * 
 * 
 *  Last modified: $Date: 2006/05/31 10:51:36 $ by $Author: tryggvil $
 * 
 * @author <a href="mailto:aron@idega.com">aron</a>
 * @version $Revision: 1.12 $
 */
public abstract class SQLSchemaAdapter implements MutableClass {

	private static final boolean DEFAULT_VALUE_USE_PREPARED_STATEMENT = true;
	private static Hashtable interfacesHashtable = null;
	private static Map interfacesByDatasourcesMap = null;
	public static boolean usePreparedStatement = DEFAULT_VALUE_USE_PREPARED_STATEMENT;
	
	protected boolean useTransactionsInSchemaCreation = true;
	private boolean useIndexes = true;
	private boolean supportsSlide = true;
	protected SQLSchemaCreator _TableCreator;
	protected DatabaseMetaData _databaseMetaData;
	private String dataStoreType;
	private String dataSourceName;
	
	
	public final static String DBTYPE_ORACLE = "oracle";
	public final static String DBTYPE_INTERBASE = "interbase";
	public final static String DBTYPE_HSQL = "hsql";
	public final static String DBTYPE_MCKOI = "mckoi";
	public final static String DBTYPE_MYSQL = "mysql";
	public final static String DBTYPE_SAPDB = "sapdb";
	public final static String DBTYPE_DB2 = "db2";
	public final static String DBTYPE_MSSQLSERVER = "mssqlserver";
	public final static String DBTYPE_INFORMIX = "informix";
	public final static String DBTYPE_UNIMPLEMENTED = "unimplemented";
	public final static String DBTYPE_DERBY = "derby";
	public final static String DBTYPE_H2 = "h2";

	
	public static void unload()	{
		interfacesHashtable = null;
		interfacesByDatasourcesMap = null;
		usePreparedStatement = DEFAULT_VALUE_USE_PREPARED_STATEMENT;
	}
	
	
	public static SQLSchemaAdapter getInstance(String datastoreType) {
		SQLSchemaAdapter theReturn = null;
		
		Class className = null;
		if (interfacesHashtable == null) {
			interfacesHashtable = new Hashtable(2);
		}
		theReturn = (SQLSchemaAdapter) interfacesHashtable.get(datastoreType);
		if (theReturn == null) {
				
			if (datastoreType.equals(DBTYPE_ORACLE)) {
				className = OracleSchemaAdapter.class;
			}
			else if (datastoreType.equals(DBTYPE_INTERBASE)) {
				className = InterbaseSchemaAdapter.class;
			}
			else if (datastoreType.equals(DBTYPE_MYSQL)) {
				className = MySQLSchemaAdapter.class;
			}
			else if (datastoreType.equals(DBTYPE_SAPDB)) {
				className = SapDBSchemaAdapter.class;
			}
			else if (datastoreType.equals(DBTYPE_MSSQLSERVER)) {
				className = MSSQLServerSchemaAdapter.class;
			}
			else if (datastoreType.equals(DBTYPE_DB2)) {
				className = DB2SchemaAdapter.class;
			}
			else if (datastoreType.equals(DBTYPE_INFORMIX)) {
				className = InformixSchemaAdapter.class;
			}
			else if (datastoreType.equals(DBTYPE_HSQL)) {
				className = HSQLSchemaAdapter.class;
			}
			else if (datastoreType.equals(DBTYPE_DERBY)) {
				className = DerbySchemaAdapter.class;
			}
			else if (datastoreType.equals(DBTYPE_MCKOI)) {
				className = McKoiSchemaAdapter.class;
			}
			else if (datastoreType.equals(DBTYPE_H2)) {
				className = H2SchemaAdapter.class;
			}
			else {
				//className = "unimplemented DatastoreInterface";
				throw new NoSchemaAdapter();
			}
			
			try {
				theReturn = (SQLSchemaAdapter) className.newInstance();
				theReturn.dataStoreType = datastoreType;
				interfacesHashtable.put(datastoreType, theReturn);
			}
			catch (Exception ex) {
				System.err.println("There was an error in com.idega.data.DatastoreInterface.getInstance(String className): " + ex.getMessage());
			}
		}
	
		return theReturn;
	}
	
	public String getDataSourceName(){
		return this.dataSourceName;
	}
	
	protected void setDataSourceName(String dataSourceName){
		this.dataSourceName = dataSourceName;
	}
	
	public String getDataStoreType(){
		return this.dataStoreType;
	}

	public static String getDatastoreType(String datasourceName) {
		Connection conn = null;
		String theReturn = "";
		try {
			conn = ConnectionBroker.getConnection(datasourceName);
			theReturn = detectDataStoreType(conn);
		}
		finally {
			ConnectionBroker.freeConnection(datasourceName, conn);
		}
		return theReturn;
	}

	public boolean useIndexes() {
		return this.useIndexes;
	}

	/**
	 * This method gets the correct instance of DatastoreInterface for the default
	 * datasource
	 * 
	 * @return the instance of DatastoreInterface for the current application
	 */
	public static SQLSchemaAdapter getInstance() {
		Connection conn = null;
		try {
			conn = ConnectionBroker.getConnection();
			return getInstanceDetected(conn);
		}
		finally {
			if (conn != null) {
				ConnectionBroker.freeConnection(conn);
			}
		}
	}

	/**
	 * This method gets the correct instance of DatastoreInterface for the
	 * Connection connection
	 * 
	 * @param connection
	 *          the connection to get the DatastoreInterface implementation for
	 * @return
	 */
	public static SQLSchemaAdapter getInstanceDetected(Connection connection) {
		return getInstance(detectDataStoreType(connection));
	}

	

	/**
	 * 
	 * Returns the type of the underlying datastore - returns: "mysql",
	 * "interbase", "oracle", "unimplemented"
	 *  
	 */
	public static String detectDataStoreType(Connection connection) {
		String dataStoreType;
		if (connection != null) {
			 {
				String checkString = null;
				try {
					checkString = connection.getMetaData().getDatabaseProductName().toLowerCase();
				}
				catch (SQLException e) {
					//Old Check
					e.printStackTrace();
					checkString = connection.getClass().getName();
				}
				if (checkString.indexOf("oracle") != -1) {
					dataStoreType = DBTYPE_ORACLE;
				}
				else if (checkString.indexOf("interbase") != -1 || checkString.indexOf("firebird") != -1) {
					dataStoreType = DBTYPE_INTERBASE;
				}
				else if (checkString.indexOf(DBTYPE_HSQL) != -1 || checkString.indexOf("hypersonicsql") != -1) {
					dataStoreType = DBTYPE_HSQL;
				}
				else if (checkString.indexOf(DBTYPE_DERBY) != -1) {
					dataStoreType = DBTYPE_DERBY;
				}
				else if (checkString.indexOf("mckoi") != -1) {
					dataStoreType = DBTYPE_MCKOI;
				}
				else if (checkString.indexOf("mysql") != -1) {
					dataStoreType = DBTYPE_MYSQL;
				}
				else if (checkString.indexOf("sap") != -1) {
					dataStoreType = DBTYPE_SAPDB;
				}
				else if (checkString.indexOf("db2") != -1) {
					dataStoreType = DBTYPE_DB2;
				}
				else if (checkString.indexOf("microsoft sql") != -1 || checkString.indexOf("microsoftsql") != -1) {
					dataStoreType = DBTYPE_MSSQLSERVER;
				}
				else if (checkString.indexOf("informix") != -1) {
					dataStoreType = DBTYPE_INFORMIX;
				}
				else if (checkString.indexOf("h2") != -1) {
					dataStoreType = DBTYPE_H2;
				}
				else if (checkString.indexOf("idega") != -1) {
					dataStoreType = DBTYPE_UNIMPLEMENTED;
				}
				else {
					dataStoreType = DBTYPE_UNIMPLEMENTED;
				}
			}
		}
		else {
			dataStoreType = DBTYPE_UNIMPLEMENTED;
		}
		return dataStoreType;
	}

	public String getSQLType(Class javaClass, int maxlength) {
		return getSQLType(javaClass.getName(), maxlength);
	}

	public abstract String getSQLType(String javaClassName, int maxlength);

	public String getIDColumnType(Schema entity) {
		return "INTEGER";
	}
	
	public void executeBeforeSchemaCreation( Schema schema) throws Exception {
	}

	public void executeAfterSchemaCreation( Schema entityDefinition) throws Exception {
	}
	
	public void removeSchema(Schema schema )throws Exception {
		getTableCreator().removeSchema( schema);
	}
	
	protected SQLSchemaCreator tableCreator;
	public SQLSchemaCreator getTableCreator() {
		if (this.tableCreator == null) {
			this.tableCreator = new SQLSchemaCreator(this);
		}
		return this.tableCreator;
	}
	
	public boolean createSchema(Schema schema) throws Exception{
		return getTableCreator().generateSchema(schema);
	}

	
	public abstract void createTrigger( Schema schema) throws Exception;


	/**
	 * Executes a query to the datasource and returns the first result
	 * (ResultSet.getObject(1)). Returns null if there was no result.
	 * 
	 * @param dataSourceName
	 * @param SQLCommand
	 * @return @throws
	 *         Exception
	 */
	public Object executeQuery( String SQLCommand) throws Exception {
		Connection conn = null;
		Statement Stmt = null;
		ResultSet rs = null;
		Object theReturn = null;
		try {
			conn = getConnection();
			Stmt = conn.createStatement();
			//System.out.println(SQLCommand);
			rs = Stmt.executeQuery(SQLCommand);
			if (rs != null && rs.next()) {
				theReturn = rs.getObject(1);
			}
		}
		finally {
			if (rs != null) {
				rs.close();
			}
			if (Stmt != null) {
				Stmt.close();
			}
			if (conn != null) {
				freeConnection(conn);
			}
		}
		return theReturn;
	}
	
	protected Connection getConnection(){
		return ConnectionBroker.getConnection(getDataSourceName());
	}
	
	protected void freeConnection( Connection conn){
		ConnectionBroker.freeConnection(getDataSourceName(),conn);
	}
	
		
    public int executeUpdate( String SQLCommand)throws Exception{
		Connection conn = null;
		Statement Stmt = null;
		int theReturn = 0;
		try {
			conn = getConnection();
			//conn.commit();
			Stmt = conn.createStatement();
			log(SQLCommand);
			theReturn = Stmt.executeUpdate(SQLCommand);
		}
		finally {
			if (Stmt != null) {
				Stmt.close();
			}
			if (conn != null) {
				freeConnection(conn);
			}
		}
		return theReturn;
	}

	/*
	 * public void populateBlob(BlobWrapper blob){
	 * 
	 * try{
	 * 
	 * PreparedStatement myPreparedStatement =
	 * blob.getConnection().prepareStatement("insert into
	 * "+blob.getEntity().getTableName()+"("+blob.getTableColumnName()+")
	 * values(?) where
	 * "+blob.getEntity().getIDColumnName()+"='"+blob.getEntity().getID()+"'");
	 *  // ByteArrayInputStream byteinstream = new ByteArrayInputStream(longbbuf);
	 * 
	 * //InputStream byteinstream = new InputStream(longbbuf);
	 * 
	 * 
	 * 
	 * //OutputStream out = blob.getOutputStream();
	 * 
	 * InputStream byteinstream = blob.getInputStreamForBlobWrite();
	 * 
	 * //InputStream myInputStream = new InputStream();
	 * 
	 * 
	 * 
	 * 
	 * 
	 * //byte buffer[]= new byte[1024];
	 * 
	 * //int noRead = 0;
	 * 
	 * 
	 * 
	 * //noRead = myInputStream.read( buffer, 0, 1023 );
	 * 
	 * 
	 * 
	 * //Write out the file to the browser
	 * 
	 * //while ( noRead != -1 ){
	 *  // output.write( buffer, 0, noRead );
	 *  // noRead = myInputStream.read( buffer, 0, 1023 );
	 *  //
	 * 
	 * 
	 * 
	 * 
	 * 
	 * myPreparedStatement.setBinaryStream(1, byteinstream,
	 * byteinstream.available() );
	 * 
	 * 
	 * 
	 * myPreparedStatement.execute();
	 * 
	 * myPreparedStatement.close();
	 *  }
	 * 
	 * catch(Exception ex){
	 * 
	 * System.err.println("Exception in DatastoreInterface.populateBlob:
	 * "+ex.getMessage());
	 * 
	 * ex.printStackTrace(System.err);
	 *  }
	 * 
	 * 
	 *  }
	 */
	public boolean isConnectionOK(Connection conn) {
		Statement testStmt = null;
		try {
			if (!conn.isClosed()) {
				// Try to createStatement to see if it's really alive
				testStmt = conn.createStatement();
				testStmt.close();
			}
			else {
				return false;
			}
		}
		catch (Exception e) {
			if (testStmt != null) {
				try {
					testStmt.close();
				}
				catch (Exception se) {
				}
			}
			//logWriter.log(e, "Pooled Connection was not okay",LogWriter.ERROR);
			return false;
		}
		return true;
	}

	

	/*
	 * public void insert(IDOLegacyEntity entity) throws Exception {
	 * this.executeBeforeInsert(entity); Connection conn = null; //Statement Stmt=
	 * null; PreparedStatement Stmt = null; ResultSet RS = null; try { conn =
	 * entity.getConnection(); //Stmt = conn.createStatement(); //int i =
	 * Stmt.executeUpdate("insert into
	 * "+entity.getTableName()+"("+entity.getCommaDelimitedColumnNames()+") values
	 * ("+entity.getCommaDelimitedColumnValues()+")"); StringBuffer statement =
	 * new StringBuffer(""); statement.append("insert into ");
	 * statement.append(entity.getTableName()); statement.append("(");
	 * statement.append(getCommaDelimitedColumnNamesForInsert(entity));
	 * statement.append(") values (");
	 * statement.append(getQuestionmarksForColumns(entity));
	 * statement.append(")"); if (isDebugActive()) debug(statement.toString());
	 * Stmt = conn.prepareStatement(statement.toString());
	 * setForPreparedStatement(STATEMENT_INSERT, Stmt, entity); Stmt.execute();
	 * 
	 * if(updateNumberGeneratedValueAfterInsert()){
	 * updateNumberGeneratedValue(entity,conn); }
	 *  } finally { if (RS != null) { RS.close(); } if (Stmt != null) {
	 * Stmt.close(); } if (conn != null) { entity.freeConnection(conn); } }
	 * this.executeAfterInsert(entity);
	 * entity.setEntityState(entity.STATE_IN_SYNCH_WITH_DATASTORE); }
	 */
	

	/**
	 * 
	 * *Creates a unique ID for the ID column
	 *  
	 */
	public int createUniqueID( Schema schema) throws Exception {
		int returnInt = -1;
		Connection conn = null;
		Statement stmt = null;
		ResultSet RS = null;
		try {
			conn = getConnection();
			stmt = conn.createStatement();
			String sql = getCreateUniqueIDQuery(schema);
			logSQL(sql);
			RS = stmt.executeQuery(sql);
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

	protected String getCreateUniqueIDQuery(Schema entity) throws Exception {
		return "";
	}

	public boolean supportsBlobInUpdate() {
		return true;
	}


	protected void createForeignKey( String baseTableName, String columnName, String refrencingTableName, String referencingColumnName) throws Exception {
		String SQLCommand = "ALTER TABLE " + baseTableName + " ADD FOREIGN KEY (" + columnName + ") REFERENCES " + refrencingTableName + "(" + referencingColumnName + ")";
		executeUpdate( SQLCommand);
	}

	protected String getCreatePrimaryKeyStatementBeginning(String tableName) {
		return "alter table " + tableName + " add primary key (";
	}

	public void setNumberGeneratorValue(Schema schema, int value) {
		throw new RuntimeException("setNumberGeneratorValue() not implemented for " + this.getClass().getName());
	}

	public void setDatabaseMetaData(DatabaseMetaData meta) {
		this._databaseMetaData = meta;
	}

	public DatabaseMetaData getDatabaseMetaData() {
		return this._databaseMetaData;
	}

	public static SQLSchemaAdapter getDatastoreInterfaceByDatasource(String datasource) {
		return (SQLSchemaAdapter) getInterfacesByDatasourcesMap().get(datasource);
	}

	protected static void setDatastoreInterfaceByDatasource(String datasource, SQLSchemaAdapter sa) {
		getInterfacesByDatasourcesMap().put(datasource, sa);
	}

	private static Map getInterfacesByDatasourcesMap() {
		if (interfacesByDatasourcesMap == null) {
			interfacesByDatasourcesMap = new HashMap();
		}
		return interfacesByDatasourcesMap;
	}

	

	/**
	 * Override in subclasses
	 */
	public void onConnectionCreate(Connection newConn) {
		/*
		 * try{ Statement stmt = newConn.createStatement(); stmt.execute("") }
		 * catch(SQLException sqle){ }
		 */
	}

	/**
	 * Queries given datasource for table existance
	 * 
	 * @param dataSourceName
	 * @param tableName
	 * @return @throws
	 *         Exception
	 */
	public boolean doesTableExist( String tableName) throws Exception {

		// old impl
		/*
		 * String checkQuery = "select count(*) from " + tableName; try {
		 * executeQuery(dataSourceName, checkQuery); return true; } catch (Exception
		 * e) { //e.printStackTrace(); return false; }
		 */

		//A connection friendler version and faster
		String[] tablesTypes = { "TABLE", "VIEW"};
		Connection conn = null;
		boolean tableExists = false;
		try {

			conn = getConnection();
			DatabaseMetaData dbMetaData = conn.getMetaData();
			ResultSet rs = null;

			//Check for upper case
			rs = dbMetaData.getTables(null, null, tableName.toUpperCase(), tablesTypes);
			if (rs.next()) {
				//table exists
				tableExists = true;
			}
			rs.close();

			//Check for lower case
			if (!tableExists) {
				rs = dbMetaData.getTables(null, null, tableName.toLowerCase(), tablesTypes);
				if (rs.next()) {
					//table exists
					tableExists = true;
				}
				rs.close();
			}

			//Check without any case manipulating, this can be removed if we always
			// force uppercase
			if (!tableExists) {

				rs = dbMetaData.getTables(null, null, tableName, tablesTypes);
				if (rs.next()) {
					//table exists
					tableExists = true;
				}
				rs.close();
			}

		}
		catch (SQLException e) {
			e.printStackTrace();
		}
		finally {
			if (conn != null) {
				freeConnection(conn);
			}
		}

		return tableExists;
	}

	
	/**
	 * Queries given datasource for view existance
	 * @param dataSourceName
	 * @param tableName
	 * @return
	 * @throws Exception
	 */
	public boolean doesViewExist( String tableName) throws Exception {
		String checkQuery = "select count(*) from " + tableName;
		try {
			executeQuery( checkQuery);
			return true;
		} catch (Exception e) {
			//e.printStackTrace();
		}
		return false;
	}
	
	
	public boolean updateTriggers( Schema schema, boolean createIfNot) throws Exception {
		return true;
	}
	
	private String[] getColumnArrayFromMetaData(String tableName){

		Connection conn = null;
		ResultSet rs = null;
		Vector v = new Vector();
		try {
			conn = getConnection();
			//conn = entity.getConnection();

			//String tableName = entity.getTableName();
			DatabaseMetaData metadata = conn.getMetaData();

			//		Check for upper case
			rs = metadata.getColumns(null, null, tableName.toUpperCase(), "%");
			//System.out.println("Table: "+tableName+" has the following columns:");
			while (rs.next()) {
				String column = rs.getString("COLUMN_NAME");
				v.add(column);
				//System.out.println("\t\t"+column);
			}
			rs.close();

			//		Check for lower case
			if (v.isEmpty()) {
				rs = metadata.getColumns(null, null, tableName.toLowerCase(), "%");
				//System.out.println("Table: "+tableName+" has the following
				// columns:");
				while (rs.next()) {
					String column = rs.getString("COLUMN_NAME");
					v.add(column);
					//System.out.println("\t\t"+column);
				}
				rs.close();
			}

			//		Check without any case manipulating, this can be removed if we always
			// force uppercase
			if (v.isEmpty()) {
				rs = metadata.getColumns(null, null, tableName, "%");
				//System.out.println("Table: "+tableName+" has the following
				// columns:");
				while (rs.next()) {
					String column = rs.getString("COLUMN_NAME");
					v.add(column);
					//System.out.println("\t\t"+column);
				}
				rs.close();
			}

		}
		catch (SQLException e) {
			e.printStackTrace();
		}
		finally {
			if (conn != null) {
				freeConnection(conn);
			}
		}
		if (v != null && !v.isEmpty()) {
			return (String[]) v.toArray(new String[0]);
		}
		return null;
	}

	/**
	 * Queries the given data source for table columns using database metadata by
	 * default
	 * 
	 * @param dataSourceName
	 * @param tableName
	 * @return
	 */
	public String[] getTableColumnNames( String tableName) {
		return getColumnArrayFromMetaData( tableName);
	}

	private Index[] getIndexHashMapFromMetaData( String tableName) {
		Connection conn = null;
		ResultSet rs = null;
		HashMap hm = new HashMap();
		try {
			conn = getConnection();
			//conn = entity.getConnection();

			//String tableName = entity.getTableName();
			DatabaseMetaData metadata = conn.getMetaData();

			//			Check for upper case
			rs = metadata.getIndexInfo(null, null, tableName.toUpperCase(), false, false);
			//			  System.out.println("Table: "+tableName+" has the following columns indexed:");
			handleIndexRS(rs, hm);
			rs.close();

			//			Check for lower case
			if (hm.isEmpty()) {
				rs = metadata.getIndexInfo(null, null, tableName.toLowerCase(), false, false);
				//			  System.out.println("Table: "+tableName+" has the following columns indexed:");
				handleIndexRS(rs, hm);
				rs.close();
			}

			//			Check without any case manipulating, this can be removed if we always
			// force uppercase
			if (hm.isEmpty()) {
				rs = metadata.getIndexInfo(null, null, tableName, false, false);
				//			  System.out.println("Table: "+tableName+" has the following columns indexed:");
				handleIndexRS(rs, hm);
				rs.close();
			}

		}
		catch (SQLException e) {
			e.printStackTrace();
		}
		finally {
			if (conn != null) {
				freeConnection(conn);
			}
		}
		
		Index[] defs = new Index[hm.size()];
		Iterator iter = hm.entrySet().iterator();
		int j = 0;
		for (; iter.hasNext();) {
            Map.Entry entry = (Map.Entry) iter.next();
            IndexImpl index = new IndexImpl(entry.getKey().toString(),tableName);
            String[] cols = (String[]) entry.getValue();
            for (int i = 0; i < cols.length; i++) {
                index.addField(cols[i]);
            }
            defs[j++]=index;
        }
		

		return defs;
		/*
		 * if(v!=null && !v.isEmpty()) return (String[])v.toArray(new String[0]);
		 * return null;
		 */
	}

	protected void handleIndexRS(ResultSet rs, HashMap hm) throws SQLException {
		String prevIndexName = null;
		Vector cols = null;
		
		while (rs.next()) {
			String index = rs.getString("INDEX_NAME");
			if (index == null) {
				// null when TYPE is tableIndexStatistic
				return;
			}
			String column = rs.getString("COLUMN_NAME");
			if (index.equals(prevIndexName)) {
				cols.add(column);
			} else {
				prevIndexName = index;
				cols = new Vector();
				cols.add(column);
			}
			hm.put(index, cols.toArray(new String[]{}));
		}
		
		
		
	}

	/**
	 * @param dataSourceName
	 * @param tableName
	 * @return map where Key is String and Value is String[]
	 */
	public Index[] getTableIndexes( String tableName) {
		return getIndexHashMapFromMetaData( tableName);
	}

	//STANDARD LOGGING METHODS:

	/**
	 * Logs out to the default log level (which is by default INFO)
	 * 
	 * @param msg
	 *          The message to log out
	 */
	protected void log(String msg) {
		//System.out.println(string);
		getLogger().log(getDefaultLogLevel(), msg);
	}

	/**
	 * Logs out to the error log level (which is by default WARNING) to the
	 * default Logger
	 * 
	 * @param e
	 *          The Exception to log out
	 */
	protected void log(Exception e) {
		LoggingHelper.logException(e, this, getLogger(), getErrorLogLevel());
	}

	/**
	 * Logs out to the specified log level to the default Logger
	 * 
	 * @param level
	 *          The log level
	 * @param msg
	 *          The message to log out
	 */
	protected void log(Level level, String msg) {
		//System.out.println(msg);
		getLogger().log(level, msg);
	}

	/**
	 * Logs out to the error log level (which is by default WARNING) to the
	 * default Logger
	 *  t FINER) to the default
	 * Logger
	 * 
	 * @param msg
	 *          The message to log out
	 */
	protected void logDebug(String msg) {
		//System.err.println(msg);
		getLogger().log(getDebugLogLevel(), msg);
	}

	/**
	 * Logs out to the SEVERE log level to the default Logger
	 * 
	 * @param msg
	 *          The message to log out
	 */
	protected void logSevere(String msg) {
		//System.err.println(msg);
		getLogger().log(Level.SEVERE, msg);
	}
	
	protected void logError(String msg) {
		//System.err.println(msg);
		getLogger().log(Level.WARNING, msg);
	}

	/**
	 * Logs out to the WARNING log level to the default Logger
	 * 
	 * @param msg
	 *          The message to log out
	 */
	protected void logWarning(String msg) {
		//System.err.println(msg);
		getLogger().log(Level.WARNING, msg);
	}

	/**
	 * Logs out to the CONFIG log level to the default Logger
	 * 
	 * @param msg
	 *          The message to log out
	 */
	protected void logConfig(String msg) {
		//System.err.println(msg);
		getLogger().log(Level.CONFIG, msg);
	}

	/**
	 * Logs out to the debug log level to the default Logger
	 * 
	 * @param msg
	 *          The message to log out
	 */
	protected void debug(String msg) {
		logDebug(msg);
	}

	/**
	 * Gets the default Logger. By default it uses the package and the class name
	 * to get the logger. <br>
	 * This behaviour can be overridden in subclasses.
	 * 
	 * @return the default Logger
	 */
	protected Logger getLogger() {
		return Logger.getLogger(this.getClass().getName());
	}

	/**
	 * Gets the log level which messages are sent to when no log level is given.
	 * 
	 * @return the Level
	 */
	protected Level getDefaultLogLevel() {
		return Level.INFO;
	}

	/**
	 * Gets the log level which debug messages are sent to.
	 * 
	 * @return the Level
	 */
	protected Level getDebugLogLevel() {
		return Level.FINER;
	}

	/**
	 * Gets the log level which error messages are sent to.
	 * 
	 * @return the Level
	 */
	protected Level getErrorLogLevel() {
		return Level.WARNING;
	}

	//ENTITY SPECIFIC LOG MEHTODS:

	///**
	// * This method outputs the outputString to System.out if the Application
	// property
	// * "debug" is set to "TRUE"
	// */
	//public void debug(String outputString) {
	//	if (isDebugActive()) {
	//		//System.out.println("[DEBUG] \"" + outputString + "\" : " +
	// this.getEntityName());
	//	}
	//}
	/**
	 * This method logs the sqlCommand if the Log Level is low enough
	 */
	public void logSQL(String sqlCommand) {
		log(Level.FINEST, sqlCommand);
		//if (isDebugActive()) {
		//System.out.println("[DEBUG] \"" + outputString + "\" : " +
		// this.getEntityName());
		//}
	}

	protected boolean isDebugActive() {
		return getIWMainApplication().getSettings().isDebugActive();
	}
	
	public IWMainApplication getIWMainApplication(){
		return IWMainApplication.getDefaultIWMainApplication();
	}

	//END STANDARD LOGGING METHODS

	/**
	 * This method outputs the outputString to System.out if the Application
	 * property "debug" is set to "TRUE"
	 */
	/*
	 * protected static void debug(String outputString) { if
	 * (IWMainApplicationSettings.isDebugActive()) { System.out.println("[DEBUG]
	 * \"" + outputString + "\" : DatastoreInterface"); } }
	 */

	/**
	 * Formats the date to a string for use as is in a SQL query quotes and
	 * casting included
	 * 
	 * @param date
	 * @return
	 */
	public String format(java.sql.Date date) {
		IWTimestamp stamp = new IWTimestamp(date);
		return " '" + (stamp.toSQLString()) + "' ";
	}

	/**
	 * Formats the date to a string for use as is in a SQL query quotes and
	 * casting included
	 * 
	 * @param timestamp
	 * @return
	 */
	public String format(java.sql.Timestamp timestamp) {
		IWTimestamp stamp = new IWTimestamp(timestamp);
		return " '" + (stamp.toSQLString()) + "' ";
	}

	/**
	 * Returns the string "CREATE TABLE [tableName]" by default.<br>
	 * This is done to be overrided for some databases, such as HSQLDB.
	 * @param tableName
	 * @return
	 */
	public String getCreateTableCommand(String tableName){
		return "CREATE TABLE "+tableName;
	}

	/**
	 * Returns the command for "ALTER TABLE [tableName] ADD [columnName] [dataType] by default.<br>
	 * This is done to be overrided for some databases, such as HSQLDB.
	 * @param columnName
	 * @param entity
	 * @return
	 */
	public String getAddColumnCommand(SchemaColumn field, Schema entityDef) {
		String SQLString = "alter table "+entityDef.getSQLName()+" add "+getColumnSQLDefinition(field,entityDef);
		return SQLString;
	}
	
	
	
	protected String getColumnSQLDefinition(SchemaColumn field,Schema definition){
	    boolean isPrimaryKey = field.isPartOfPrimaryKey();
	    boolean isCompositePK = definition.getPrimaryKey().isComposite();

	    String type;
	    
	    if(isPrimaryKey && !isCompositePK && field.getDataTypeClass()==Integer.class){
	      type = getIDColumnType(definition);
	    }
	    else{
	      type = getSQLType(field.getDataTypeClass(),field.getMaxLength());
	    }

	    String returnString = field.getSQLName()+" "+type;

	    if (!field.isNullAllowed()){
	      returnString = 	returnString + " NOT NULL";
	    }
	    /* DOES NOT WORK WITH COMPOSITE PKS, MOVED TO getCreationStatement(entity)
	    if (isPrimaryKey) {
	      returnString = 	returnString + " PRIMARY KEY";
	    }*/
	    if (field.isUnique() &&supportsUniqueConstraintInColumnDefinition()){
	      returnString = 	returnString + " UNIQUE";
	    }
	    return returnString;
	  }
	
	public boolean supportsUniqueConstraintInColumnDefinition(){
		return true;
	}
	
	public boolean isCabableOfRSScroll(){
		return false;
	}
	
	/**
	 * returns the optimal or allowed fetch size when going to database to load IDOEntities using 'where primarikey_name in (list_of_priamrykeys)'
	 */
	public int getOptimalEJBLoadFetchSize(){
		return 500;
	}
	
	public Object executeGetProcedure(String dataSourceName, Procedure procedure, Object[] parameters) throws SQLException {
		return executeProcedure(dataSourceName, procedure, parameters,false);
	}
	
	public Collection executeFindProcedure(String dataSourceName, Procedure procedure, Object[] parameters) throws SQLException {
		return (Collection)executeProcedure(dataSourceName, procedure, parameters,true);
	}

	
	public Object executeProcedure(String dataSourceName, Procedure procedure, Object[] parameters,boolean returnCollection) throws SQLException {
		Connection conn = null;
		CallableStatement Stmt = null;
		ResultSet rs = null;
		Object theReturn = null;
		try {
			conn = getConnection();
			String prepareArgString = "";
			if(parameters !=null&&parameters.length>0){
				prepareArgString = " (";
				for (int i = 0; i < parameters.length; i++) {
					prepareArgString += " ?";
				}
				prepareArgString += " )";
			}
			String sql = "{"+((!returnCollection)?" ? =":"")+" call "+procedure.getName()+prepareArgString+" }";
			//System.out.println("[DatastorInterface]: "+sql);
			Stmt = conn.prepareCall(sql);
			
			Class[] parameterTypes = procedure.getParameterTypes();
			int length = Math.min(parameterTypes.length,parameters.length);
			for (int i = 0; i < length; i++) {
				try {
					insertIntoCallableStatement(Stmt,i+1,parameterTypes[i],parameters[i]);
				}
				catch (Exception e) {
					System.out.println("Original error message");
					e.printStackTrace();
					throw new SQLException("IDOProcedure: " + procedure.getName() + "; parameter:  " + i + "; value:  " + parameters[i] + " - " + e.getMessage());
				}
			}
			
			theReturn = procedure.processResultSet(Stmt.executeQuery());
			
//			rs = Stmt.executeQuery();
//			if(returnCollection){
//				Collection c = new ArrayList();
//				if (rs != null ){
//					while(rs.next()) {
//						c.add(rs.getObject(1));
//					}
//				}
//				theReturn = c;
//			} else {
//				if (rs != null && rs.next()) {
//					theReturn = rs.getObject(1);
//				}
//			}
		}
		finally {
			if (rs != null) {
				rs.close();
			}
			if (Stmt != null) {
				Stmt.close();
			}
			if (conn != null) {
				freeConnection(conn);
			}
		}
		return theReturn;

	}
	
	private void insertIntoCallableStatement(CallableStatement stmt, int index, Class type, Object parameter) throws SQLException{
		if (type.equals(Integer.class)) {
			stmt.setInt(index,((Integer)parameter).intValue());
		}
		else if (type.equals(Boolean.class)) {
			stmt.setBoolean(index,((Boolean)parameter).booleanValue());
		}
		else if (type.equals(String.class)) {
			stmt.setString(index,(String)parameter);
		}
		else if (type.equals(Float.class)) {
			stmt.setFloat(index, ((Float)parameter).floatValue());
		}
		else if (type.equals(Double.class)) {
			stmt.setDouble(index, ((Double)parameter).doubleValue());
		}
		else if (type.equals(Timestamp.class)) {
			Timestamp stamp = (Timestamp) parameter;
			stmt.setTimestamp(index, stamp);
		}
		else if (type.equals(Time.class)) {
			stmt.setTime(index, (Time) parameter);
		}
		else if (type.equals(Date.class)) {
			stmt.setDate(index, (java.sql.Date) parameter);
		}
		else if (type.equals(Array.class)) {
			stmt.setArray(index, (Array) parameter);
		}
//			else if (type.equals("com.idega.util.Gender")) {
//				stmt.setString(index, entity.getColumnValue(columnName).toString());
//			}
//			else if (type.equals("com.idega.data.BlobWrapper")) {
//				handleBlobUpdate(columnName, stmt, index, entity);
//				//stmt.setDate(index,(java.sql.Date)getColumnValue(columnName));
//			}
		else {
			stmt.setObject(index, parameter);
		}
	}
	
	public boolean allowsStoredProcedure(){
		return true;
	}
	
	public boolean hasStoredProcedure(String procedureName) throws SQLException{
		if(!allowsStoredProcedure()){
			return false;
		}
		boolean toReturn = false;
		ResultSet rs = null;
		try {
			rs = getDatabaseMetaData().getProcedures(null,null,procedureName);
			toReturn = rs.next();
		}
		catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if(rs!=null){
				rs.close();
			}
		}
		return toReturn;
	}
	
	public boolean isUsingPreparedStatements(){
	    return usePreparedStatement;
	}

	/**
	 * @param schema
	 * @param columns
	 */
	public String getCreateUniqueKeyStatement(Schema schema, SchemaColumn[] columns) {
		StringBuffer sql = new StringBuffer(" UNIQUE (");
		for (int i = 0; i < columns.length; i++) {
			if(i>0) {
				sql.append(",");
			}
			sql.append(columns[i].getSQLName());
		}
		sql.append(")");
		return sql.toString();
		
	}
	
	/**
	 * <p>
	 * This method returns the max length of a column to be part of a (composite) primary key.<br>
	 * This method by default returns -1 which is no limit, but this is overridden for MySQL.
	 * </p>
	 * @return
	 */
	public int getMaxColumnPrimaryKeyLength(SchemaColumn column){
		return -1;
	}
	/**
	 * <p>
	 * This methods returns true in the Adaptor supports the slide db table, false is it doesnt
	 * </p>
	 * @return
	 */
	public boolean getSupportsSlide() {
		return this.supportsSlide;
	}
	
}