// idega 2000 - Tryggvi Larusson
/*
 * 
 * Copyright 2001 idega.is All Rights Reserved.
 *  
 */
package com.idega.data;

import java.sql.Array;
import java.sql.Blob;
import java.sql.CallableStatement;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.NClob;
import java.sql.PreparedStatement;
import java.sql.SQLClientInfoException;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.SQLXML;
import java.sql.Statement;
import java.sql.Struct;
import java.util.Map;
import java.util.Properties;

/**
 * 
 * @author <a href="mailto:tryggvi@idega.is">Tryggvi Larusson </a>
 * 
 * @version 1.0
 *  
 */
public class DatastoreConnection implements Connection {

	Connection conn;

	DatastoreInterface dsi;

	protected String _dataSourceName;

	public DatastoreConnection(Connection conn, String datasourceName) throws SQLException {
		/*
		 * this.TRANSACTION_NONE=conn.TRANSACTION_NONE;
		 * this.TRANSACTION_READ_COMMITTED=conn.TRANSACTION_READ_COMMITTED;
		 * this.TRANSACTION_READ_UNCOMMITTED=conn.TRANSACTION_READ_UNCOMMITTED;
		 * this.TRANSACTION_REPEATABLE_READ=conn.TRANSACTION_REPEATABLE_READ;
		 * this.TRANSACTION_SERIALIZABLE=conn.TRANSACTION_SERIALIZABLE;
		 */
		this.conn = conn;
		this.dsi = DatastoreInterface.getInstance(conn);
		this.dsi.setDatabaseMetaData(conn.getMetaData());
		setDatasource(datasourceName);
		DatastoreInterface.setDatastoreInterfaceByDatasource(datasourceName, this.dsi);
		this.dsi.onConnectionCreate(this);
	}

	public void setDatasource(String datasourceName) {
		this._dataSourceName = datasourceName;
	}

	public String getDatasource() {
		return this._dataSourceName;
	}

	public DatastoreInterface getDatastoreInterface() {
		return this.dsi;
	}

	protected Connection getUnderLyingConnection() {
		return this.conn;
	}

	/**
	 * 
	 * Creates a <code>Statement</code> object for sending
	 * 
	 * SQL statements to the database.
	 * 
	 * SQL statements without parameters are normally
	 * 
	 * executed using Statement objects. If the same SQL statement
	 * 
	 * is executed many times, it is more efficient to use a
	 * 
	 * <code>PreparedStatement</code> object.
	 * 
	 * <P>
	 * 
	 * 
	 * 
	 * Result sets created using the returned <code>Statement</code>
	 * 
	 * object will by default have forward-only type and read-only concurrency.
	 * 
	 * 
	 * 
	 * @return a new Statement object
	 * 
	 * @exception SQLException
	 *                if a database access error occurs
	 *  
	 */
	public Statement createStatement() throws SQLException {
		return this.conn.createStatement();
	}

	/**
	 * 
	 * Creates a <code>PreparedStatement</code> object for sending
	 * 
	 * parameterized SQL statements to the database.
	 * 
	 * 
	 * 
	 * A SQL statement with or without IN parameters can be
	 * 
	 * pre-compiled and stored in a PreparedStatement object. This
	 * 
	 * object can then be used to efficiently execute this statement
	 * 
	 * multiple times.
	 * 
	 * 
	 * 
	 * <P>
	 * <B>Note: </B> This method is optimized for handling
	 * 
	 * parametric SQL statements that benefit from precompilation. If
	 * 
	 * the driver supports precompilation,
	 * 
	 * the method <code>prepareStatement</code> will send
	 * 
	 * the statement to the database for precompilation. Some drivers
	 * 
	 * may not support precompilation. In this case, the statement may
	 * 
	 * not be sent to the database until the <code>PreparedStatement</code> is
	 * 
	 * executed. This has no direct effect on users; however, it does
	 * 
	 * affect which method throws certain SQLExceptions.
	 * 
	 * 
	 * 
	 * 
	 * 
	 * Result sets created using the returned PreparedStatement will have
	 * 
	 * forward-only type and read-only concurrency, by default.
	 * 
	 * 
	 * 
	 * @param sql
	 *            a SQL statement that may contain one or more '?' IN
	 * 
	 * parameter placeholders
	 * 
	 * @return a new PreparedStatement object containing the
	 * 
	 * pre-compiled statement
	 * 
	 * @exception SQLException
	 *                if a database access error occurs
	 *  
	 */
	public PreparedStatement prepareStatement(String sql) throws SQLException {
		return this.conn.prepareStatement(sql);
	}

	/**
	 * 
	 * Creates a <code>CallableStatement</code> object for calling
	 * 
	 * database stored procedures.
	 * 
	 * The <code>CallableStatement</code> object provides
	 * 
	 * methods for setting up its IN and OUT parameters, and
	 * 
	 * methods for executing the call to a stored procedure.
	 * 
	 * 
	 * 
	 * <P>
	 * <B>Note: </B> This method is optimized for handling stored
	 * 
	 * procedure call statements. Some drivers may send the call
	 * 
	 * statement to the database when the method <code>prepareCall</code>
	 * 
	 * is done; others
	 * 
	 * may wait until the <code>CallableStatement</code> object
	 * 
	 * is executed. This has no
	 * 
	 * direct effect on users; however, it does affect which method
	 * 
	 * throws certain SQLExceptions.
	 * 
	 * 
	 * 
	 * 
	 * 
	 * Result sets created using the returned CallableStatement will have
	 * 
	 * forward-only type and read-only concurrency, by default.
	 * 
	 * 
	 * 
	 * @param sql
	 *            a SQL statement that may contain one or more '?'
	 * 
	 * parameter placeholders. Typically this statement is a JDBC
	 * 
	 * function call escape string.
	 * 
	 * @return a new CallableStatement object containing the
	 * 
	 * pre-compiled SQL statement
	 * 
	 * @exception SQLException
	 *                if a database access error occurs
	 *  
	 */
	public CallableStatement prepareCall(String sql) throws SQLException {
		return this.conn.prepareCall(sql);
	}

	/**
	 * 
	 * Converts the given SQL statement into the system's native SQL grammar.
	 * 
	 * A driver may convert the JDBC sql grammar into its system's
	 * 
	 * native SQL grammar prior to sending it; this method returns the
	 * 
	 * native form of the statement that the driver would have sent.
	 * 
	 * 
	 * 
	 * @param sql
	 *            a SQL statement that may contain one or more '?'
	 * 
	 * parameter placeholders
	 * 
	 * @return the native form of this statement
	 * 
	 * @exception SQLException
	 *                if a database access error occurs
	 *  
	 */
	public String nativeSQL(String sql) throws SQLException {
		return this.conn.nativeSQL(sql);
	}

	/**
	 * 
	 * Sets this connection's auto-commit mode.
	 * 
	 * If a connection is in auto-commit mode, then all its SQL
	 * 
	 * statements will be executed and committed as individual
	 * 
	 * transactions. Otherwise, its SQL statements are grouped into
	 * 
	 * transactions that are terminated by a call to either
	 * 
	 * the method <code>commit</code> or the method <code>rollback</code>.
	 * 
	 * By default, new connections are in auto-commit
	 * 
	 * mode.
	 * 
	 * 
	 * 
	 * The commit occurs when the statement completes or the next
	 * 
	 * execute occurs, whichever comes first. In the case of
	 * 
	 * statements returning a ResultSet, the statement completes when
	 * 
	 * the last row of the ResultSet has been retrieved or the
	 * 
	 * ResultSet has been closed. In advanced cases, a single
	 * 
	 * statement may return multiple results as well as output
	 * 
	 * parameter values. In these cases the commit occurs when all results and
	 * 
	 * output parameter values have been retrieved.
	 * 
	 * 
	 * 
	 * @param autoCommit
	 *            true enables auto-commit; false disables
	 * 
	 * auto-commit.
	 * 
	 * @exception SQLException
	 *                if a database access error occurs
	 *  
	 */
	public void setAutoCommit(boolean autoCommit) throws SQLException {
		this.conn.setAutoCommit(autoCommit);
	}

	/**
	 * 
	 * Gets the current auto-commit state.
	 * 
	 * 
	 * 
	 * @return the current state of auto-commit mode
	 * 
	 * @exception SQLException
	 *                if a database access error occurs
	 * 
	 * @see #setAutoCommit
	 *  
	 */
	public boolean getAutoCommit() throws SQLException {
		return this.conn.getAutoCommit();
	}

	/**
	 * 
	 * Makes all changes made since the previous
	 * 
	 * commit/rollback permanent and releases any database locks
	 * 
	 * currently held by the Connection. This method should be
	 * 
	 * used only when auto-commit mode has been disabled.
	 * 
	 * 
	 * 
	 * @exception SQLException
	 *                if a database access error occurs
	 * 
	 * @see #setAutoCommit
	 *  
	 */
	public void commit() throws SQLException {
		this.conn.commit();
	}

	/**
	 * 
	 * Drops all changes made since the previous
	 * 
	 * commit/rollback and releases any database locks currently held
	 * 
	 * by this Connection. This method should be used only when auto-
	 * 
	 * commit has been disabled.
	 * 
	 * 
	 * 
	 * @exception SQLException
	 *                if a database access error occurs
	 * 
	 * @see #setAutoCommit
	 *  
	 */
	public void rollback() throws SQLException {
		this.conn.rollback();
	}

	/**
	 * 
	 * Releases a Connection's database and JDBC resources
	 * 
	 * immediately instead of waiting for
	 * 
	 * them to be automatically released.
	 * 
	 * 
	 * 
	 * <P>
	 * <B>Note: </B> A Connection is automatically closed when it is
	 * 
	 * garbage collected. Certain fatal errors also result in a closed
	 * 
	 * Connection.
	 * 
	 * 
	 * 
	 * @exception SQLException
	 *                if a database access error occurs
	 *  
	 */
	public void close() throws SQLException {
		this.conn.close();
	}

	/**
	 * 
	 * Tests to see if a Connection is closed.
	 * 
	 * 
	 * 
	 * @return true if the connection is closed; false if it's still open
	 * 
	 * @exception SQLException
	 *                if a database access error occurs
	 *  
	 */
	public boolean isClosed() throws SQLException {
		return this.conn.isClosed();
	}

	//======================================================================
	// Advanced features:
	/**
	 * 
	 * Gets the metadata regarding this connection's database.
	 * 
	 * A Connection's database is able to provide information
	 * 
	 * describing its tables, its supported SQL grammar, its stored
	 * 
	 * procedures, the capabilities of this connection, and so on. This
	 * 
	 * information is made available through a DatabaseMetaData
	 * 
	 * object.
	 * 
	 * 
	 * 
	 * @return a DatabaseMetaData object for this Connection
	 * 
	 * @exception SQLException
	 *                if a database access error occurs
	 *  
	 */
	public DatabaseMetaData getMetaData() throws SQLException {
		return this.conn.getMetaData();
	}

	/**
	 * 
	 * Puts this connection in read-only mode as a hint to enable
	 * 
	 * database optimizations.
	 * 
	 * 
	 * 
	 * <P>
	 * <B>Note: </B> This method cannot be called while in the
	 * 
	 * middle of a transaction.
	 * 
	 * 
	 * 
	 * @param readOnly
	 *            true enables read-only mode; false disables
	 * 
	 * read-only mode.
	 * 
	 * @exception SQLException
	 *                if a database access error occurs
	 *  
	 */
	public void setReadOnly(boolean readOnly) throws SQLException {
		this.conn.setReadOnly(readOnly);
	}

	/**
	 * 
	 * Tests to see if the connection is in read-only mode.
	 * 
	 * 
	 * 
	 * @return true if connection is read-only and false otherwise
	 * 
	 * @exception SQLException
	 *                if a database access error occurs
	 *  
	 */
	public boolean isReadOnly() throws SQLException {
		return this.conn.isReadOnly();
	}

	/**
	 * 
	 * Sets a catalog name in order to select
	 * 
	 * a subspace of this Connection's database in which to work.
	 * 
	 * If the driver does not support catalogs, it will
	 * 
	 * silently ignore this request.
	 * 
	 * 
	 * 
	 * @exception SQLException
	 *                if a database access error occurs
	 *  
	 */
	public void setCatalog(String catalog) throws SQLException {
		this.conn.setCatalog(catalog);
	}

	/**
	 * 
	 * Returns the Connection's current catalog name.
	 * 
	 * 
	 * 
	 * @return the current catalog name or null
	 * 
	 * @exception SQLException
	 *                if a database access error occurs
	 *  
	 */
	public String getCatalog() throws SQLException {
		return this.conn.getCatalog();
	}

	/**
	 * 
	 * Indicates that transactions are not supported.
	 *  
	 */
	/*
	 * public int TRANSACTION_NONE = 0;
	 */
	/**
	 * 
	 * Dirty reads, non-repeatable reads and phantom reads can occur.
	 * 
	 * This level allows a row changed by one transaction to be read
	 * 
	 * by another transaction before any changes in that row have been
	 * 
	 * committed (a "dirty read"). If any of the changes are rolled back,
	 * 
	 * the second transaction will have retrieved an invalid row.
	 *  
	 */
	/*
	 * public int TRANSACTION_READ_UNCOMMITTED = 1;
	 */
	/**
	 * 
	 * Dirty reads are prevented; non-repeatable reads and phantom
	 * 
	 * reads can occur. This level only prohibits a transaction
	 * 
	 * from reading a row with uncommitted changes in it.
	 *  
	 */
	/*
	 * 
	 * public int TRANSACTION_READ_COMMITTED = 2;
	 *  
	 */
	/**
	 * 
	 * Dirty reads and non-repeatable reads are prevented; phantom
	 * 
	 * reads can occur. This level prohibits a transaction from
	 * 
	 * reading a row with uncommitted changes in it, and it also
	 * 
	 * prohibits the situation where one transaction reads a row,
	 * 
	 * a second transaction alters the row, and the first transaction
	 * 
	 * rereads the row, getting different values the second time
	 * 
	 * (a "non-repeatable read").
	 *  
	 */
	/*
	 * public int TRANSACTION_REPEATABLE_READ = 4;
	 */
	/**
	 * 
	 * Dirty reads, non-repeatable reads and phantom reads are prevented.
	 * 
	 * This level includes the prohibitions in
	 * 
	 * TRANSACTION_REPEATABLE_READ and further prohibits the
	 * 
	 * situation where one transaction reads all rows that satisfy
	 * 
	 * a WHERE condition, a second transaction inserts a row that
	 * 
	 * satisfies that WHERE condition, and the first transaction
	 * 
	 * rereads for the same condition, retrieving the additional
	 * 
	 * "phantom" row in the second read.
	 *  
	 */
	/*
	 * public int TRANSACTION_SERIALIZABLE = 8;
	 */
	/**
	 * 
	 * Attempts to change the transaction
	 * 
	 * isolation level to the one given.
	 * 
	 * The constants defined in the interface <code>Connection</code>
	 * 
	 * are the possible transaction isolation levels.
	 * 
	 * 
	 * 
	 * <P>
	 * <B>Note: </B> This method cannot be called while
	 * 
	 * in the middle of a transaction.
	 * 
	 * 
	 * 
	 * @param level
	 *            one of the TRANSACTION_* isolation values with the
	 * 
	 * exception of TRANSACTION_NONE; some databases may not support
	 * 
	 * other values
	 * 
	 * @exception SQLException
	 *                if a database access error occurs
	 * 
	 * @see DatabaseMetaData#supportsTransactionIsolationLevel
	 *  
	 */
	public void setTransactionIsolation(int level) throws SQLException {
		this.conn.setTransactionIsolation(level);
	}

	/**
	 * 
	 * Gets this Connection's current transaction isolation level.
	 * 
	 * 
	 * 
	 * @return the current TRANSACTION_* mode value
	 * 
	 * @exception SQLException
	 *                if a database access error occurs
	 *  
	 */
	public int getTransactionIsolation() throws SQLException {
		return this.conn.getTransactionIsolation();
	}

	/**
	 * 
	 * Returns the first warning reported by calls on this Connection.
	 * 
	 * 
	 * 
	 * <P>
	 * <B>Note: </B> Subsequent warnings will be chained to this
	 * 
	 * SQLWarning.
	 * 
	 * 
	 * 
	 * @return the first SQLWarning or null
	 * 
	 * @exception SQLException
	 *                if a database access error occurs
	 *  
	 */
	public SQLWarning getWarnings() throws SQLException {
		return this.conn.getWarnings();
	}

	/**
	 * 
	 * Clears all warnings reported for this <code>Connection</code> object.
	 * 
	 * After a call to this method, the method <code>getWarnings</code>
	 * 
	 * returns null until a new warning is
	 * 
	 * reported for this Connection.
	 * 
	 * 
	 * 
	 * @exception SQLException
	 *                if a database access error occurs
	 *  
	 */
	public void clearWarnings() throws SQLException {
		this.conn.clearWarnings();
	}

	//--------------------------JDBC 2.0-----------------------------
	/**
	 * 
	 * 
	 * 
	 * Creates a <code>Statement</code> object that will generate
	 * 
	 * <code>ResultSet</code> objects with the given type and concurrency.
	 * 
	 * This method is the same as the <code>createStatement</code> method
	 * 
	 * above, but it allows the default result set
	 * 
	 * type and result set concurrency type to be overridden.
	 * 
	 * 
	 * 
	 * @param resultSetType
	 *            a result set type; see ResultSet.TYPE_XXX
	 * 
	 * @param resultSetConcurrency
	 *            a concurrency type; see ResultSet.CONCUR_XXX
	 * 
	 * @return a new Statement object
	 * 
	 * @exception SQLException
	 *                if a database access error occurs
	 * 
	 * @since 1.2
	 * 
	 * @see <a href="package-summary.html#2.0 API">What Is in the JDBC 2.0 API
	 *      </a>
	 *  
	 */
	public Statement createStatement(int resultSetType, int resultSetConcurrency) throws SQLException {
		return this.conn.createStatement(resultSetType, resultSetConcurrency);
	}

	/**
	 * 
	 * 
	 * 
	 * Creates a <code>PreparedStatement</code> object that will generate
	 * 
	 * <code>ResultSet</code> objects with the given type and concurrency.
	 * 
	 * This method is the same as the <code>prepareStatement</code> method
	 * 
	 * above, but it allows the default result set
	 * 
	 * type and result set concurrency type to be overridden.
	 * 
	 * 
	 * 
	 * @param resultSetType
	 *            a result set type; see ResultSet.TYPE_XXX
	 * 
	 * @param resultSetConcurrency
	 *            a concurrency type; see ResultSet.CONCUR_XXX
	 * 
	 * @return a new PreparedStatement object containing the
	 * 
	 * pre-compiled SQL statement
	 * 
	 * @exception SQLException
	 *                if a database access error occurs
	 * 
	 * @since 1.2
	 * 
	 * @see <a href="package-summary.html#2.0 API">What Is in the JDBC 2.0 API
	 *      </a>
	 *  
	 */
	public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency) throws SQLException {
		return this.conn.prepareStatement(sql, resultSetType, resultSetConcurrency);
	}

	/**
	 * 
	 * 
	 * 
	 * Creates a <code>CallableStatement</code> object that will generate
	 * 
	 * <code>ResultSet</code> objects with the given type and concurrency.
	 * 
	 * This method is the same as the <code>prepareCall</code> method
	 * 
	 * above, but it allows the default result set
	 * 
	 * type and result set concurrency type to be overridden.
	 * 
	 * 
	 * 
	 * @param resultSetType
	 *            a result set type; see ResultSet.TYPE_XXX
	 * 
	 * @param resultSetConcurrency
	 *            a concurrency type; see ResultSet.CONCUR_XXX
	 * 
	 * @return a new CallableStatement object containing the
	 * 
	 * pre-compiled SQL statement
	 * 
	 * @exception SQLException
	 *                if a database access error occurs
	 * 
	 * @since 1.2
	 * 
	 * @see <a href="package-summary.html#2.0 API">What Is in the JDBC 2.0 API
	 *      </a>
	 *  
	 */
	public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency) throws SQLException {
		return this.conn.prepareCall(sql, resultSetType, resultSetConcurrency);
	}

	/**
	 * 
	 * 
	 * 
	 * Gets the type map object associated with this connection.
	 * 
	 * Unless the application has added an entry to the type map,
	 * 
	 * the map returned will be empty.
	 * 
	 * 
	 * 
	 * @return the <code>java.util.Map</code> object associated
	 * 
	 * with this <code>Connection</code> object
	 * 
	 * @since 1.2
	 * 
	 * @see <a href="package-summary.html#2.0 API">What Is in the JDBC 2.0 API
	 *      </a>
	 *  
	 */
	public java.util.Map getTypeMap() throws SQLException {
		return this.conn.getTypeMap();
	}

	/**
	 * 
	 * Installs the given type map as the type map for
	 * 
	 * this connection. The type map will be used for the
	 * 
	 * custom mapping of SQL structured types and distinct types.
	 * 
	 * 
	 * 
	 * @param the
	 *            <code>java.util.Map</code> object to install
	 * 
	 * as the replacement for this <code>Connection</code>
	 * 
	 * object's default type map
	 * 
	 * @since 1.2
	 * 
	 * @see <a href="package-summary.html#2.0 API">What Is in the JDBC 2.0 API
	 *      </a>
	 *  
	 */
	public void setTypeMap(Map<String, Class<?>> map) throws SQLException {
		this.conn.setTypeMap(map);
	}

	/**
	 * @todo implement this. required for jdk1.4
	 * 
	 * Comment in for JDK 1.4 compile compatability!!!
	 */
	public java.sql.Statement createStatement(int iOne, int iTwo, int iThree) {
		return null;
	}

	public void setHoldability(int h) {
	}

	public int getHoldability() {
		return -1;
	}

	public java.sql.Savepoint setSavepoint() {
		return null;
	}

	public java.sql.Savepoint setSavepoint(String savepoint) {
		return null;
	}

	public void rollback(java.sql.Savepoint s) {
	}

	public void releaseSavepoint(java.sql.Savepoint s) {
	}

	public java.sql.PreparedStatement prepareStatement(String sql, int one, int two, int three) {
		return null;
	}

	public java.sql.CallableStatement prepareCall(String sql, int one, int two, int three) {
		return null;
	}

	public java.sql.PreparedStatement prepareStatement(String sql, int one) {
		return null;
	}

	public java.sql.PreparedStatement prepareStatement(String sql, int[] one) {
		return null;
	}

	public java.sql.PreparedStatement prepareStatement(String sql, String[] one) {
		return null;
	}

	public Array createArrayOf(String typeName, Object[] elements)
			throws SQLException {
		return null;
	}

	public Blob createBlob() throws SQLException {
		return null;
	}

	public Clob createClob() throws SQLException {
		return null;
	}

	public NClob createNClob() throws SQLException {
		return null;
	}

	public SQLXML createSQLXML() throws SQLException {
		return null;
	}

	public Struct createStruct(String typeName, Object[] attributes)
			throws SQLException {
		return null;
	}

	public Properties getClientInfo() throws SQLException {
		return null;
	}

	public String getClientInfo(String name) throws SQLException {
		return null;
	}

	public boolean isValid(int timeout) throws SQLException {
		return false;
	}

	public void setClientInfo(Properties properties)
			throws SQLClientInfoException {
	}

	public void setClientInfo(String name, String value)
			throws SQLClientInfoException {
	}

	public boolean isWrapperFor(Class<?> iface) throws SQLException {
		return false;
	}

	public <T> T unwrap(Class<T> iface) throws SQLException {
		return null;
	}
}