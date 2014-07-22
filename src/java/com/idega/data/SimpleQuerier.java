//idega 2000 - Tryggvi Larusson
/*

*Copyright 2000 idega.is All Rights Reserved.

*/
package com.idega.data;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Vector;

import com.idega.util.database.ConnectionBroker;
/**
*A class to query/update directly to an SQL datastore. This class should only be used by data implementation classes
*
*@author <a href="mailto:tryggvi@idega.is">Tryggvi Larusson</a>
*
*@version 1.0
*/
public class SimpleQuerier {
    /**
     * Does nothing
     */
    private SimpleQuerier() {}
    private static String getDatasource() {
        return "default";
    }
    /**
     * Gets a databaseconnection identified by the datasourceName
     */
    private static Connection getConnection(String datasourceName) throws SQLException {
        return ConnectionBroker.getConnection(datasourceName);
    }
    /**
     * Gets the default database connection
     */
    private static Connection getConnection() throws SQLException {
        return ConnectionBroker.getConnection(getDatasource());
    }
    /**
     * Frees the connection used, must be done after using a databaseconnection
     */
    private static void freeConnection(String datasourceName, Connection connection) {
        ConnectionBroker.freeConnection(datasourceName, connection);
    }
    /**
     * Frees the default connection used, must be done after using a databaseconnection
     */
    private static void freeConnection(Connection connection) {
        ConnectionBroker.freeConnection(getDatasource(), connection);
    }
    /**
     * Frees the default connection used, must be done after using a databaseconnection
     */
    private static void freeConnection(Connection connection, String datasource) {
        ConnectionBroker.freeConnection(datasource, connection);
    }
    public static String[] executeStringQuery(String sqlQuery) throws Exception {
        Connection conn= null;
        try {
            conn= getConnection();
            return executeStringQuery(sqlQuery, conn);
        }
        finally {
            if (conn != null) {
                freeConnection(conn);
            }
        }
    }
    public static String[] executeStringQuery(String sqlQuery, String datasource) throws Exception {
        Connection conn= null;
        try {
            conn= getConnection(datasource);
            return executeStringQuery(sqlQuery, conn);
        }
        finally {
            if (conn != null) {
                freeConnection(conn, datasource);
            }
        }
    }
    public static String[] executeStringQuery(String sqlQuery, Connection conn) throws Exception {
        Statement Stmt= null;
        String[] theReturn= null;
        try {
            Stmt= conn.createStatement();
            ResultSet RS= Stmt.executeQuery(sqlQuery);
            Vector vector= new Vector();
            while (RS.next()) {
                vector.add(RS.getString(1));
            }
            RS.close();
            theReturn= (String[]) vector.toArray(new String[0]);
        }
        finally {
            if (Stmt != null) {
                Stmt.close();
            }
        }
        return theReturn;
    }
    
    /**
     * Gets and returns the first int in the resultset from column 'columnInResultSet'
     * @param sqlQuery
     * @param columnInResultSet
     * @param conn
     * @return
     * @throws Exception
     */
    public static int executeIntQuery(String sqlQuery, String columnInResultSet) throws Exception {
	    Connection conn= null;
	    try {
	        conn= getConnection();
	        return executeIntQuery(sqlQuery,columnInResultSet,conn);
	    }
	    finally {
	        if (conn != null) {
	            freeConnection(conn);
	        }
	    }
    }
    
    /**
     * Gets and returns the first int in the resultset from column 'columnInResultSet'
     * @param sqlQuery
     * @param columnInResultSet
     * @param conn
     * @return
     * @throws Exception
     */
    public static int executeIntQuery(String sqlQuery, String columnInResultSet, Connection conn) throws Exception {
        Statement Stmt= null;
        int theReturn= -1;
        try {
            Stmt= conn.createStatement();
            ResultSet RS= Stmt.executeQuery(sqlQuery);
            if (RS.next()) {
                theReturn= RS.getInt(columnInResultSet);
            }
            RS.close();
        }
        finally {
            if (Stmt != null) {
                Stmt.close();
            }
        }
        return theReturn;
    }

    
    public static int executeIntQuery(String sqlQuery, Connection conn) throws Exception {
        Statement Stmt= null;
        int theReturn= -1;
        try {
            Stmt= conn.createStatement();
            ResultSet RS= Stmt.executeQuery(sqlQuery);
            if (RS.next()) {
                theReturn= RS.getInt(1);
            }
            RS.close();
        }
        finally {
            if (Stmt != null) {
                Stmt.close();
            }
        }
        return theReturn;
    }
    
    public static int executeIntQuery(String sqlQuery) throws Exception{
        Connection conn= null;
        try {
            conn= getConnection();
            return executeIntQuery(sqlQuery,conn);
        }
        finally {
            if (conn != null) {
                freeConnection(conn);
            }
        }
    }
    /**
     * @deprecated Replaced with idoExecuteTableUpdate/idoExecuteGlobalUpdate in GenericEntity or executeUpdate()
     */
    public static boolean execute(String sqlString) throws Exception {
        return execute(sqlString, true);
    }
    /**
     * @deprecated Replaced with executeUpdate() or idoExecuteTableUpdate/idoExecuteGlobalUpdate in GenericEntity
     */
    private static boolean execute(String sqlString, boolean flushCache) throws Exception {
        Connection conn= null;
        Statement Stmt= null;
        boolean theReturn= false;
        try {
            conn= getConnection();
            Stmt= conn.createStatement();
            theReturn= Stmt.execute(sqlString);
        }
        finally {
            if (Stmt != null) {
                Stmt.close();
            }
            if (conn != null) {
                freeConnection(conn);
            }
        }
        if (flushCache) {
            IDOContainer.getInstance().flushAllCache();
        }
        return theReturn;
    }
    /**
     * Executes an sql update command specified by sqlString to the datastore specified and flushes all cache if there was an update
     *  @returns true if there was an update, false if there was no update
     *  @throws SQLException if there was an error
     */
    protected static boolean executeUpdate(String sqlString, String dataSource) throws SQLException {
        return executeUpdate(sqlString, dataSource, true);
    }
    /**
     * Executes an sql update command specified by sqlString to the datastore specified and flushes all cache if there was an update
     *  @returns true if there was an update, false if there was no update
     *  @throws SQLException if there was an error
     */
    public static boolean executeUpdate(String sqlString, boolean flushCache) throws SQLException {
    	return executeUpdate(sqlString, getDatasource(), flushCache);
    }
    /**
     * Executes an sql update command specified by sqlString to the datastore specified and flushes all cache if flushCache==true
     *  @returns true if there was an update, false if there was no update
     *  @throws SQLException if there was an error
     */
    protected static boolean executeUpdate(String sqlString, String dataSource, boolean flushCache)
        throws SQLException {
        Connection conn= null;
        Statement Stmt= null;
        boolean theReturn= false;
        int count= 0;
        try {
            conn= getConnection(dataSource);
            Stmt= conn.createStatement();
            count= Stmt.executeUpdate(sqlString);
            if (count > 0) {
                theReturn= true;
            }
        }
        finally {
            if (Stmt != null) {
                Stmt.close();
            }
            if (conn != null) {
                freeConnection(dataSource, conn);
            }
        }
        if (flushCache && theReturn) {
            IDOContainer.getInstance().flushAllCache();
        }
        return theReturn;
    }
}
