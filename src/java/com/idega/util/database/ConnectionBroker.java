/*
 * $Id: ConnectionBroker.java,v 1.19 2008/05/10 14:42:30 alexis Exp $
 *
 * Copyright (C) 2000-2005 Idega hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 *
 */
package com.idega.util.database;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import com.idega.transaction.IdegaTransaction;
import com.idega.transaction.IdegaTransactionManager;
import com.idega.util.StringUtil;
/**
 *<p>
 * This class is an abstraction of the underlying Database Pool.<br>
 * It can deliver connections from the old style idegaWeb PoolManager or a J2EE style JDBC/JNDI DataSource.<br>
 * Works in conjunction with com.idega.transaction.IdegaTransactionManager,
 * com.idega.util.database.PoolManager and javax.sql.DataSource.<br>
 *
 * Whenever a connection object is gotten with getConnection(x) there should always follow a call to freeConnection(x)
 * when the Connection is not used anymore, otherwise the Pool could get empty and unused connections left out in limbo.
 * <br>
 * The implementation is such that if a db.properties file is found in the webapp under either /idegaweb/properties
 * or /WEB-INF/idegaweb/properties/ then the idegaWeb Pool is used, if it does not exist then
 * the ConnectionBroker tries to look up a DataSource with the url 'jdbc/DefaultDS' in the JNDI directory.
 * <br>
 * </p>
 *@author <a href="mailto:tryggvi@idega.is">Tryggvi Larusson</a>
 * @version $Revision: 1.19 $
*/
public class ConnectionBroker {

	private static final Logger LOGGER = Logger.getLogger(ConnectionBroker.class.getName());

	private static Context initialContext;
	public final static String DEFAULT_POOL = "default";
	public final static int POOL_MANAGER_TYPE_IDEGA = 1;
	public final static int POOL_MANAGER_TYPE_POOLMAN = 2;
	public final static int POOL_MANAGER_TYPE_JDBC_DATASOURCE = 3;
	public static final String SYSTEM_PROPERTY_DB_PROPERTIES_FILE_PATH = "idegaweb.db.properties";
	public static int POOL_MANAGER_TYPE = POOL_MANAGER_TYPE_IDEGA;
	private static String DEFAULT_JDBC_JNDI_URL = "jdbc/DefaultDS";

	private static DataSource defaultDs;
	private static Map<String, DataSource> dataSourcesMap=new HashMap<String, DataSource>();
	public static int gottenConns=0;

	/**
	 * Returns a Datastore connection from the default datasource
	 */
	public static Connection getConnection() {
		return getConnection(true);
	}

	/**
	 * Returns a Datastore connection from the default datasource
	 */
	public static Connection getConnection(boolean doTransactionCheck) {
		if (doTransactionCheck) {
			return getConnection(DEFAULT_POOL);
		} else {
			return PoolManager.getInstance().getConnection();
		}
	}

	/**
	 * Does not fully support TransactionManager
	 * Returns a Datastore connection from the datasource
	 */
	public static Connection getConnection(String dataSourceName) {
		if (LOGGER.isLoggable(Level.FINEST)) {
			LOGGER.finest("Getting database connection from: "+dataSourceName+" for "+(++gottenConns)+" time");
		}

		if (dataSourceName == null) {
			return getConnection();
		} else {
			Connection conn = null;
			IdegaTransactionManager tm = (IdegaTransactionManager) IdegaTransactionManager.getInstance(dataSourceName);
			if (tm.hasCurrentThreadBoundTransaction()) {
				try {
					//System.out.println("Getting connection from transaction for datasource: "+dataSourceName);
					conn = ((IdegaTransaction) tm.getTransaction()).getConnection();
				} catch (Exception ex) {
					LOGGER.log(Level.WARNING, "Error getting connection from JDBC data source: " + dataSourceName, ex);
				}
			} else {
				if (isUsingIdegaPool()) {
					//System.out.println("Getting connection from pool for datasource: "+dataSourceName);
					conn = PoolManager.getInstance().getConnection(dataSourceName);
				} else if (isUsingJNDIDatasource()) {
					try {
						conn = getDataSource(dataSourceName).getConnection();
					} catch (SQLException e) {
						LOGGER.log(Level.WARNING, "Error getting connection from JDBC data source: " + dataSourceName, e);
					}
				} else if (isUsingPoolManPool()) {
					//try{
					/**
					 * @todo: Commit in support for com.codestudio.util PoolMan
					 */
					//conn = ((com.codestudio.util.JDBCPool)com.codestudio.util.SQLManager.getInstance().getPool(dataSourceName)).requestConnection();
					//}
					//catch(SQLException e){
					//  throw new RuntimeException(e.getMessage());
					//}
				}
			}
			return conn;
		}
	}

	/**
	 * Frees (Reallocates) a Datastore connection to the default datasource
	 */
	public static void freeConnection(Connection connection) {
		freeConnection(connection, true);
	}

	/**
	 * Returns a Datastore connection from the default datasource
	 */
	public static void freeConnection(Connection connection, boolean doTransactionCheck) {
		if (doTransactionCheck) {
			freeConnection(DEFAULT_POOL, connection);
		} else {
			freePooledConnection(null,connection);
		}
	}

	private static void freePooledConnection(String dataSourceName, Connection connection) {
		if (isUsingIdegaPool()) {
			if (dataSourceName == null) {
				PoolManager.getInstance().freeConnection(connection);
			} else {
				PoolManager.getInstance().freeConnection(dataSourceName, connection);
			}
		} else if (isUsingJNDIDatasource()) {
			try {
				if (connection != null && !connection.isClosed()) {
					connection.close();
				}
			} catch (SQLException e) {
				LOGGER.log(Level.WARNING, "Error closing connection to data source: " + (dataSourceName == null ? DEFAULT_JDBC_JNDI_URL : dataSourceName), e);
			}
		} else if (isUsingPoolManPool()) {
			/**
			 * @todo: Commit in support for com.codestudio.util PoolMan
			 */
			//com.codestudio.util.SQLManager.getInstance().returnConnection(connection);
		}
	}

	/**
	 * Frees (Reallocates) a Datastore connection to the datasource
	 */
	public static void freeConnection(String dataSourceName, Connection connection, boolean doTransactionCheck) {
		if (StringUtil.isEmpty(dataSourceName)) {
			freeConnection(connection, doTransactionCheck);
		} else {
			if (doTransactionCheck && !((IdegaTransactionManager) IdegaTransactionManager.getInstance(dataSourceName)).hasCurrentThreadBoundTransaction()) {
				freePooledConnection(dataSourceName,connection);
			} else if (!doTransactionCheck) {
				freePooledConnection(dataSourceName,connection);
			}
		}
	}

	/**
	 * Frees (Reallocates) a Datastore connection to the datasource
	 */
	public static void freeConnection(String dataSourceName, Connection connection) {
		freeConnection(dataSourceName, connection, true);
	}

	public static String[] getDatasources() {
		return PoolManager.getInstance().getDatasources();
	}

	public static String getURL() {
		return PoolManager.getInstance().getURLForPool();
	}

	public static String getURL(String dataSourceName) {
		return PoolManager.getInstance().getURLForPool(dataSourceName);
	}

	public static String getUserName() {
		return PoolManager.getInstance().getUserNameForPool();
	}

	public static String getUserName(String dataSourceName) {
		return PoolManager.getInstance().getUserNameForPool(dataSourceName);
	}

	public static String getPassword() {
		return PoolManager.getInstance().getPasswordForPool();
	}

	public static String getPassword(String dataSourceName) {
		return PoolManager.getInstance().getPasswordForPool(dataSourceName);
	}

	public static String getDriverClass() {
		return PoolManager.getInstance().getDriverClassForPool();
	}

	public Connection recycleConnection(Connection conn, String dataSourceName) {
		return PoolManager.getInstance().recycleConnection(conn, dataSourceName);
	}

	public Connection recycleConnection(Connection conn) {
		return PoolManager.getInstance().recycleConnection(conn);
	}

	public static boolean isUsingIdegaPool() {
		return (POOL_MANAGER_TYPE == POOL_MANAGER_TYPE_IDEGA);
	}

	public static boolean isUsingPoolManPool() {
		return (POOL_MANAGER_TYPE == POOL_MANAGER_TYPE_POOLMAN);
	}

	public static boolean isUsingJNDIDatasource() {
		return (POOL_MANAGER_TYPE == POOL_MANAGER_TYPE_JDBC_DATASOURCE);
	}

	public static boolean tryDefaultJNDIDataSource(){
		/**
		 * @todo change:
		* only supports the default datasource now implement support for others
		*/
		DataSource ds;
		try{
			LOGGER.info("Trying DataSource with url: '"+DEFAULT_JDBC_JNDI_URL+"'");
			ds = getDataSource(DEFAULT_POOL);
		} catch (Exception e) {
			LOGGER.log(Level.WARNING, "Error getting connection from data source: " + DEFAULT_JDBC_JNDI_URL, e);
			return false;
		}

		if (ds!=null) {
			POOL_MANAGER_TYPE=POOL_MANAGER_TYPE_JDBC_DATASOURCE;
			LOGGER.info("Successfully enabled Database from DataSource: "+DEFAULT_JDBC_JNDI_URL);
			return true;
		}
		return false;
	}

	/**
	 * <p>
	 * Checks if the datasource from the string 'datasourceName' is available<br>
	 * If dataSourceName is 'default' it returns the default datasource from 'jdbc/DefaultDS' otherwise it tries to look up
	 * a datasource from jdbc/[datasourceName].
	 * </p>
	 * @param datasourceName
	 * @return
	 */
	public static boolean hasDataSource(String datasourceName) {
		if (datasourceName == null || datasourceName == DEFAULT_POOL || datasourceName.equals(DEFAULT_POOL)) {
			if (defaultDs == null) {
				try {
					defaultDs = (DataSource) getEnvContext().lookup(DEFAULT_JDBC_JNDI_URL);
					dataSourcesMap.put(DEFAULT_POOL, defaultDs);
				} catch (NamingException e) {
					return false;
				}
			}
			return true;
		} else {
			DataSource dataSource = dataSourcesMap.get(datasourceName);
			if (dataSource==null) {
				try {
					dataSource = (DataSource) getEnvContext().lookup("jdbc/" + datasourceName);
					dataSourcesMap.put(datasourceName, dataSource);
				} catch (NamingException e) {
					return false;
				}
			}
			return true;
		}
	}

	/**
	 * <p>
	 * Gets the datasource from the string 'datasourceName'<br>
	 * If dataSourceName is 'default' it returns the default datasource from 'jdbc/DefaultDS' otherwise it tries to look up
	 * a datasource from jdbc/[datasourceName].
	 * </p>
	 * @param datasourceName
	 * @return
	 */
	public static DataSource getDataSource(String datasourceName) {
		if (datasourceName == null || datasourceName == DEFAULT_POOL || datasourceName.equals(DEFAULT_POOL)) {
			if (defaultDs == null) {
				try {
					defaultDs = (DataSource) getEnvContext().lookup(DEFAULT_JDBC_JNDI_URL);
					dataSourcesMap.put(DEFAULT_POOL,defaultDs);
				} catch (NamingException e) {
					throw new RuntimeException("Error initializing datasource: " + datasourceName + ". Error was: " + e.getMessage());
				}
			}
			return defaultDs;
		} else {
			DataSource dataSource = dataSourcesMap.get(datasourceName);
			if (dataSource == null) {
				try {
					dataSource = (DataSource) getEnvContext().lookup("jdbc/"+datasourceName);
					dataSourcesMap.put(datasourceName,dataSource);
				} catch (NamingException e) {
					throw new RuntimeException("Error initializing datasource: " + datasourceName + ". Error was: " + e.getMessage());
				}
			}
			return dataSource;
		}
	}

	/**
	 * Sets the url to the default datasource if the JDBC datasource is the current pool type
	 * @param url
	 */
	public static void setDefaultJDBCDatasourceURL(String url) {
		DEFAULT_JDBC_JNDI_URL = url;
	}

	private static Context getEnvContext() throws NamingException {
		if (initialContext == null) {
			initialContext = new InitialContext();
		}
		return (Context) initialContext.lookup("java:comp/env");
	}

	public static String getDefaultJNDIUrl() {
		return DEFAULT_JDBC_JNDI_URL;
	}

}