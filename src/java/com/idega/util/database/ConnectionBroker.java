//idega 2000 - Tryggvi Larusson
/*

*Copyright 2000 idega.is All Rights Reserved.

*/
package com.idega.util.database;
import java.sql.*;
import javax.sql.DataSource;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import com.idega.transaction.*;
/**

 *

*@author <a href="mailto:tryggvi@idega.is">Tryggvi Larusson</a>

*@version 1.3

* Class to deliver datastore connections through the PoolManager

* Works in conjunction with com.idega.transaction.IdegaTransactionManager and com.idega.util.database.PoolManager

*/
public class ConnectionBroker {
	public final static String DEFAULT_POOL = "default";
	public final static int POOL_MANAGER_TYPE_IDEGA = 1;
	public final static int POOL_MANAGER_TYPE_POOLMAN = 2;
	public final static int POOL_MANAGER_TYPE_JDBC_DATASOURCE = 3;
	
	//temprorary - change
	public static int POOL_MANAGER_TYPE = POOL_MANAGER_TYPE_IDEGA;//POOL_MANAGER_TYPE_JDBC_DATASOURCE;
	private static DataSource ds;
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
	 * Returns a Datastore connection from the default datasource
	 */
	private static Connection getConnectionOld(boolean doTransactionCheck) {
		Connection conn = null;
		IdegaTransactionManager tm = (IdegaTransactionManager) IdegaTransactionManager.getInstance();
		if (doTransactionCheck && tm.hasCurrentThreadBoundTransaction()) {
			try {
				conn = ((IdegaTransaction) tm.getTransaction()).getConnection();
			} catch (Exception ex) {
				ex.printStackTrace(System.err);
			}
		} else {
			if (isUsingIdegaPool()) {
				conn = PoolManager.getInstance().getConnection();
			} else if (isUsingPoolManPool()) {
				//try{
				//conn = com.codestudio.util.SQLManager.getInstance().requestConnection();
				/**
				
				 * @todo: Commit in support for com.codestudio.util PoolMan
				
				 */
				//}
				//catch(SQLException e){
				//  throw new RuntimeException(e.getMessage());
				//}
			} else
				throw new RuntimeException("PoolManager not available");
		}
		return conn;
	}
	/**
	 * Does not fully support TransactionManager
	 * Returns a Datastore connection from the datasource
	 */
	public static Connection getConnection(String dataSourceName) {
		if (dataSourceName == null) {
			return getConnection();
		} else {
			Connection conn = null;
			IdegaTransactionManager tm = (IdegaTransactionManager) IdegaTransactionManager.getInstance();
			if (tm.hasCurrentThreadBoundTransaction()) {
				try {
					//System.out.println("Getting connection from transaction for datasource: "+dataSourceName);
					conn = ((IdegaTransaction) tm.getTransaction()).getConnection();
				} catch (Exception ex) {
					ex.printStackTrace(System.err);
				}
			} else {
				if (isUsingIdegaPool()) {
					//System.out.println("Getting connection from pool for datasource: "+dataSourceName);
					conn = PoolManager.getInstance().getConnection(dataSourceName);
				} else if (isUsingJDBCDatasource()) {
					try {
						conn = getDataSource(dataSourceName).getConnection();
					} catch (SQLException e) {
						System.out.println("Error getting connection from JDBC datasource : " + dataSourceName);
						e.printStackTrace(System.err);
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
			PoolManager.getInstance().freeConnection(connection);
		}
	}
	/**
	
	 * Frees (Reallocates) a Datastore connection to the default datasource
	
	 */
	private static void freeConnectionOld(Connection connection, boolean doTransactionCheck) {
		if (doTransactionCheck && !((IdegaTransactionManager) IdegaTransactionManager.getInstance()).hasCurrentThreadBoundTransaction()) {
			//PoolManager.getInstance().freeConnection(connection);
			if (isUsingIdegaPool()) {
				PoolManager.getInstance().freeConnection(connection);
			} else if (isUsingPoolManPool()) {
				/**
				
				 * @todo: Commit in support for com.codestudio.util PoolMan
				
				 */
				//com.codestudio.util.SQLManager.getInstance().returnConnection(connection);
			}
		} else if (!doTransactionCheck) {
			if (isUsingIdegaPool()) {
				PoolManager.getInstance().freeConnection(connection);
			} else if (isUsingPoolManPool()) {
				/**
				
				 * @todo: Commit in support for com.codestudio.util PoolMan
				
				 */
				//com.codestudio.util.SQLManager.getInstance().returnConnection(connection);
			}
		}
	}
	/**
	
	 * Frees (Reallocates) a Datastore connection to the datasource
	
	 */
	public static void freeConnection(String dataSourceName, Connection connection, boolean doTransactionCheck) {
		if (dataSourceName == null) {
			freeConnection(connection, doTransactionCheck);
		} else {
			if (doTransactionCheck
				&& !((IdegaTransactionManager) IdegaTransactionManager.getInstance()).hasCurrentThreadBoundTransaction()) {
				if (isUsingIdegaPool()) {
					PoolManager.getInstance().freeConnection(dataSourceName, connection);
				} else if (isUsingJDBCDatasource()) {
					try {
						connection.close();
					} catch (SQLException e) {
						e.printStackTrace();
					}
				} else if (isUsingPoolManPool()) {
					/**
					
					 * @todo: Commit in support for com.codestudio.util PoolMan
					
					 */
					//com.codestudio.util.SQLManager.getInstance().returnConnection(connection);
				}
			} else if (!doTransactionCheck) {
				if (isUsingIdegaPool()) {
					PoolManager.getInstance().freeConnection(dataSourceName, connection);
				} else if (isUsingJDBCDatasource()) {
					try {
						connection.close();
					} catch (SQLException e) {
						e.printStackTrace();
					}
				} else if (isUsingPoolManPool()) {
					/**
					
					 * @todo: Commit in support for com.codestudio.util PoolMan
					
					 */
					//com.codestudio.util.SQLManager.getInstance().returnConnection(connection);
				}
			}
		}
	}
	/**
	
	 * Frees (Reallocates) a Datastore connection to the datasource
	
	 */
	public static void freeConnection(String dataSourceName, Connection connection) {
		/*if(dataSourceName==null){
		
		  freeConnection(connection);
		
		}
		
		else{
		
		      if (!((IdegaTransactionManager)IdegaTransactionManager.getInstance()).hasCurrentThreadBoundTransaction()){
		
		        //PoolManager.getInstance().freeConnection(dataSourceName,connection);
		
		        if(isUsingIdegaPool()){
		
		          PoolManager.getInstance().freeConnection(dataSourceName,connection);
		
		        }
		
		        else if(isUsingPoolManPool()){
		
		            //((com.codestudio.util.JDBCPool)com.codestudio.util.SQLManager.getInstance().getPool(dataSourceName)).returnObject(connection);
		
		        }
		
		      }
		
		}*/
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
	static boolean isUsingIdegaPool() {
		return (POOL_MANAGER_TYPE == POOL_MANAGER_TYPE_IDEGA);
	}
	static boolean isUsingPoolManPool() {
		return (POOL_MANAGER_TYPE == POOL_MANAGER_TYPE_POOLMAN);
	}
	static boolean isUsingJDBCDatasource() {
		return (POOL_MANAGER_TYPE == POOL_MANAGER_TYPE_JDBC_DATASOURCE);
	}
	static DataSource getDataSource(String datasourceName) {
		/**
		 * @todo change:
		* only supports the default datasource now implement support for others
		*/
		if (ds == null) {
			try {
				//ds = (DataSource) new InitialContext().lookup("java:comp/env/datasource");
				ds = (DataSource) new InitialContext().lookup("java:/DefaultDS");
			} catch (NamingException e) {
				throw new RuntimeException("Error initializing datasource: " + datasourceName + ". Error was: " + e.getMessage());
			}
		}
		return ds;
	}
}
