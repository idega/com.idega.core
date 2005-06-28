/*
 * $Id: ConnectionBroker.java,v 1.10 2005/06/28 11:23:17 tryggvil Exp $
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
import java.util.logging.Logger;

import javax.naming.InitialContext;
import javax.naming.Context;
import javax.naming.NamingException;
import javax.sql.DataSource;

import com.idega.transaction.IdegaTransaction;
import com.idega.transaction.IdegaTransactionManager;
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
 * </p>
 *@author <a href="mailto:tryggvi@idega.is">Tryggvi Larusson</a>
 * @version $Revision: 1.10 $
*/
public class ConnectionBroker
{
	private static Context initialContext;
	public final static String DEFAULT_POOL = "default";
	public final static int POOL_MANAGER_TYPE_IDEGA = 1;
	public final static int POOL_MANAGER_TYPE_POOLMAN = 2;
	public final static int POOL_MANAGER_TYPE_JDBC_DATASOURCE = 3;
	//temprorary - change
	public static int POOL_MANAGER_TYPE = POOL_MANAGER_TYPE_IDEGA; //POOL_MANAGER_TYPE_JDBC_DATASOURCE;
	private static String DEFAULT_JDBC_JNDI_URL = "jdbc/DefaultDS";
	
	private static DataSource defaultDs;
	private static Logger log = Logger.getLogger(ConnectionBroker.class.getName());
	/**	
	 * Returns a Datastore connection from the default datasource
	 */
	public static Connection getConnection()
	{
		return getConnection(true);
	}
	/**
	 * Returns a Datastore connection from the default datasource
	 */
	public static Connection getConnection(boolean doTransactionCheck)
	{
		if (doTransactionCheck)
		{
			return getConnection(DEFAULT_POOL);
		}
		else
		{
			return PoolManager.getInstance().getConnection();
		}
	}
	/**
	 * Returns a Datastore connection from the default datasource
	 */
	private static Connection getConnectionOld(boolean doTransactionCheck)
	{
		Connection conn = null;
		IdegaTransactionManager tm = (IdegaTransactionManager) IdegaTransactionManager.getInstance();
		if (doTransactionCheck && tm.hasCurrentThreadBoundTransaction())
		{
			try
			{
				conn = ((IdegaTransaction) tm.getTransaction()).getConnection();
			}
			catch (Exception ex)
			{
				ex.printStackTrace(System.err);
			}
		}
		else
		{
			if (isUsingIdegaPool())
			{
				conn = PoolManager.getInstance().getConnection();
			}
			else if (isUsingPoolManPool())
			{
				//try{
				//conn = com.codestudio.util.SQLManager.getInstance().requestConnection();
				/**
				 * @todo: Commit in support for com.codestudio.util PoolMan
				 */
				//}
				//catch(SQLException e){
				//  throw new RuntimeException(e.getMessage());
				//}
			}
			else
				throw new RuntimeException("PoolManager not available");
		}
		return conn;
	}
	/**
	 * Does not fully support TransactionManager
	 * Returns a Datastore connection from the datasource
	 */
	public static Connection getConnection(String dataSourceName)
	{
		if (dataSourceName == null)
		{
			return getConnection();
		}
		else
		{
			Connection conn = null;
			IdegaTransactionManager tm = (IdegaTransactionManager) IdegaTransactionManager.getInstance();
			if (tm.hasCurrentThreadBoundTransaction())
			{
				try
				{
					//System.out.println("Getting connection from transaction for datasource: "+dataSourceName);
					conn = ((IdegaTransaction) tm.getTransaction()).getConnection();
				}
				catch (Exception ex)
				{
					ex.printStackTrace(System.err);
				}
			}
			else
			{
				if (isUsingIdegaPool())
				{
					//System.out.println("Getting connection from pool for datasource: "+dataSourceName);
					conn = PoolManager.getInstance().getConnection(dataSourceName);
				}
				else if (isUsingJNDIDatasource())
				{
					try
					{
						conn = getDataSource(dataSourceName).getConnection();
					}
					catch (SQLException e)
					{
						System.out.println("Error getting connection from JDBC datasource : " + dataSourceName);
						e.printStackTrace(System.err);
					}
				}
				else if (isUsingPoolManPool())
				{
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
	public static void freeConnection(Connection connection)
	{
		freeConnection(connection, true);
	}
	/**
	
	 * Returns a Datastore connection from the default datasource
	
	 */
	public static void freeConnection(Connection connection, boolean doTransactionCheck)
	{
		if (doTransactionCheck)
		{
			freeConnection(DEFAULT_POOL, connection);
		}
		else
		{
			freePooledConnection(null,connection);
		}
	}
	/**
	
	 * Frees (Reallocates) a Datastore connection to the default datasource
	
	 */
	private static void freeConnectionOld(Connection connection, boolean doTransactionCheck)
	{
		if (doTransactionCheck && !((IdegaTransactionManager) IdegaTransactionManager.getInstance()).hasCurrentThreadBoundTransaction())
		{
			freePooledConnection(null,connection);
		}
		else if (!doTransactionCheck)
		{
			freePooledConnection(null,connection);
		}
	}
	
	
	private static void freePooledConnection(String dataSourceName,Connection connection){
		if (isUsingIdegaPool())
		{
			if(dataSourceName==null){
				PoolManager.getInstance().freeConnection(connection);
			}
			else{
				PoolManager.getInstance().freeConnection(dataSourceName,connection);
			}
		}
		else if (isUsingJNDIDatasource())
		{
			try {
				connection.close();
			}
			catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		else if (isUsingPoolManPool())
		{
			/**
			
			 * @todo: Commit in support for com.codestudio.util PoolMan
			
			 */
			//com.codestudio.util.SQLManager.getInstance().returnConnection(connection);
		}
	}
	/**
	
	 * Frees (Reallocates) a Datastore connection to the datasource
	
	 */
	public static void freeConnection(String dataSourceName, Connection connection, boolean doTransactionCheck)
	{
		if (dataSourceName == null)
		{
			freeConnection(connection, doTransactionCheck);
		}
		else
		{
			if (doTransactionCheck && !((IdegaTransactionManager) IdegaTransactionManager.getInstance()).hasCurrentThreadBoundTransaction())
			{
				freePooledConnection(dataSourceName,connection);
			}
			else if (!doTransactionCheck)
			{
				freePooledConnection(dataSourceName,connection);
			}
		}
	}
	/**
	
	 * Frees (Reallocates) a Datastore connection to the datasource
	
	 */
	public static void freeConnection(String dataSourceName, Connection connection)
	{
		freeConnection(dataSourceName, connection, true);
	}
	public static String[] getDatasources()
	{
		return PoolManager.getInstance().getDatasources();
	}
	public static String getURL()
	{
		return PoolManager.getInstance().getURLForPool();
	}
	public static String getURL(String dataSourceName)
	{
		return PoolManager.getInstance().getURLForPool(dataSourceName);
	}
	public static String getUserName()
	{
		return PoolManager.getInstance().getUserNameForPool();
	}
	public static String getUserName(String dataSourceName)
	{
		return PoolManager.getInstance().getUserNameForPool(dataSourceName);
	}
	public static String getPassword()
	{
		return PoolManager.getInstance().getPasswordForPool();
	}
	public static String getPassword(String dataSourceName)
	{
		return PoolManager.getInstance().getPasswordForPool(dataSourceName);
	}
	public static String getDriverClass()
	{
		return PoolManager.getInstance().getDriverClassForPool();
	}
	public Connection recycleConnection(Connection conn, String dataSourceName)
	{
		return PoolManager.getInstance().recycleConnection(conn, dataSourceName);
	}
	public Connection recycleConnection(Connection conn)
	{
		return PoolManager.getInstance().recycleConnection(conn);
	}
	public static boolean isUsingIdegaPool()
	{
		return (POOL_MANAGER_TYPE == POOL_MANAGER_TYPE_IDEGA);
	}
	public static boolean isUsingPoolManPool()
	{
		return (POOL_MANAGER_TYPE == POOL_MANAGER_TYPE_POOLMAN);
	}
	public static boolean isUsingJNDIDatasource()
	{
		return (POOL_MANAGER_TYPE == POOL_MANAGER_TYPE_JDBC_DATASOURCE);
	}
	
	
	public static boolean tryDefaultJNDIDataSource(){
		/**
		 * @todo change:
		* only supports the default datasource now implement support for others
		*/
		DataSource ds;
		try{
			log.info("Trying DataSource with url: '"+DEFAULT_JDBC_JNDI_URL+"'");
			ds = getDataSource(DEFAULT_POOL);
		}
		catch(Exception e){
			e.printStackTrace();
			return false;
		}

		if(ds!=null){
			POOL_MANAGER_TYPE=POOL_MANAGER_TYPE_JDBC_DATASOURCE;
			log.info("Successfully enabled Database from DataSource: "+DEFAULT_JDBC_JNDI_URL);
			return true;
		}
		return false;
	}
	
	static DataSource getDataSource(String datasourceName)
	{
		/**
		 * @todo change:
		* only supports the default datasource now implement support for others
		*/
		if (defaultDs == null)
		{
			try
			{
				//ds = (DataSource) new InitialContext().lookup("java:comp/env/datasource");
				
				defaultDs = (DataSource) getEnvContext().lookup(DEFAULT_JDBC_JNDI_URL);
			}
			catch (NamingException e)
			{
				throw new RuntimeException("Error initializing datasource: " + datasourceName + ". Error was: " + e.getMessage());
			}
		}
		return defaultDs;
	}
	/**
	 * Sets the url to the default datasource if the JDBC datasource is the current pool type
	 * @param url
	 */
	public static void setDefaultJDBCDatasourceURL(String url){
		DEFAULT_JDBC_JNDI_URL=url;
	}
	
	private static Context getEnvContext()throws NamingException{
		if(initialContext==null){
			initialContext = new InitialContext();
		}
		return (Context) initialContext.lookup("java:comp/env");
	}
	
	public static String getDefaultJNDIUrl(){
		return DEFAULT_JDBC_JNDI_URL;
	}
}
