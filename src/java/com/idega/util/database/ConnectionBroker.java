//idega 2000 - Tryggvi Larusson
/*
*Copyright 2000 idega.is All Rights Reserved.
*/

package com.idega.util.database;



import java.sql.*;
import com.idega.transaction.*;

/**
 *
*@author <a href="mailto:tryggvi@idega.is">Tryggvi Larusson</a>
*@version 1.3
* Class to deliver datastore connections through the PoolManager
* Works in conjunction with com.idega.transaction.IdegaTransactionManager and com.idega.util.database.PoolManager
*/
public class ConnectionBroker{

        /**
         * Returns a Datastore connection from the default datasource
         */
	public static Connection getConnection(){
                Connection conn = null;
                IdegaTransactionManager tm  = (IdegaTransactionManager)IdegaTransactionManager.getInstance();
                if (tm.hasCurrentThreadBoundTransaction()){
                  try{
                    conn = ((IdegaTransaction)tm.getTransaction()).getConnection();
                  }
                  catch(Exception ex){

                  }
                }
                else{
                  conn = PoolManager.getInstance().getConnection();
                }
		return conn;
	}

        /**
         * Does not fully support TransactionManager
         * Returns a Datastore connection from the datasource
         */
	public static Connection getConnection(String dataSourceName){
                Connection conn = null;
                IdegaTransactionManager tm  = (IdegaTransactionManager)IdegaTransactionManager.getInstance();
                if (tm.hasCurrentThreadBoundTransaction()){
                  try{
                    conn = ((IdegaTransaction)tm.getTransaction()).getConnection();
                  }
                  catch(Exception ex){

                  }
                }
                else{
                  conn = PoolManager.getInstance().getConnection();
                }
		return conn;
	}


        /**
         * Frees (Reallocates) a Datastore connection to the default datasource
         */
	public static void freeConnection(Connection connection){

                if (!((IdegaTransactionManager)IdegaTransactionManager.getInstance()).hasCurrentThreadBoundTransaction()){
                  PoolManager.getInstance().freeConnection(connection);
	        }
        }


        /**
         * Does not fully support TransactionManager
         * Frees (Reallocates) a Datastore connection to the datasource
         */
	public static void freeConnection(String dataSourceName, Connection connection){
                if (!((IdegaTransactionManager)IdegaTransactionManager.getInstance()).hasCurrentThreadBoundTransaction()){
                  PoolManager.getInstance().freeConnection(dataSourceName,connection);
	        }
	}


        public static String[] getDatasources(){
            return PoolManager.getInstance().getDatasources();
        }



	public static String getURL(){
		return PoolManager.getInstance().getURLForPool();
	}


	public static String getURL(String dataSourceName){
		return PoolManager.getInstance().getURLForPool(dataSourceName);
	}


        public static String getUserName(){
		return PoolManager.getInstance().getUserNameForPool();
	}


	public static String getUserName(String dataSourceName){
		return PoolManager.getInstance().getUserNameForPool(dataSourceName);
	}


        public static String getPassword(){
		return PoolManager.getInstance().getPasswordForPool();
	}


	public static String getPassword(String dataSourceName){
		return PoolManager.getInstance().getPasswordForPool(dataSourceName);
	}


        public static String getDriverClass(){
		return PoolManager.getInstance().getDriverClassForPool();
	}


        public Connection recycleConnection(Connection conn,String dataSourceName){
        return PoolManager.getInstance().recycleConnection(conn,dataSourceName);
        }


        public Connection recycleConnection(Connection conn){
            return PoolManager.getInstance().recycleConnection(conn);
        }

}
