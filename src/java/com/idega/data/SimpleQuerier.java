//idega 2000 - Tryggvi Larusson
/*
*Copyright 2000 idega.is All Rights Reserved.
*/

package com.idega.data;


import java.sql.*;
import javax.naming.*;
import javax.sql.*;
import java.util.*;
import com.idega.util.database.*;
import com.idega.util.*;
/**
*@author <a href="mailto:tryggvi@idega.is">Tryggvi Larusson</a>
*@version 1.0
*/
public class SimpleQuerier{

        /**
         * Does nothing
         */
        private SimpleQuerier(){

        }


        private static String getDatasource(){
          return "default";
        }

	/**
	 * Gets a databaseconnection identified by the datasourceName
	 */
	private static Connection getConnection(String datasourceName)throws SQLException{
		return ConnectionBroker.getConnection(datasourceName);
	}

	/**
	 * Gets the default database connection
	 */
	private static Connection getConnection()throws SQLException{
		return ConnectionBroker.getConnection(getDatasource());
	}

	/**
	 * Frees the connection used, must be done after using a databaseconnection
	 */
	private static void freeConnection(String datasourceName,Connection connection){
		ConnectionBroker.freeConnection(datasourceName,connection);
	}

	/**
	 * Frees the default connection used, must be done after using a databaseconnection
	 */
	private static void freeConnection(Connection connection){
		ConnectionBroker.freeConnection(getDatasource(),connection);
	}




	public static String[] executeStringQuery(String sqlQuery)throws Exception{
		Connection conn= null;
		Statement Stmt= null;
                String[] theReturn = null;
		try{
			conn = getConnection();
			Stmt = conn.createStatement();
                        ResultSet RS = Stmt.executeQuery(sqlQuery);

                        Vector vector = new Vector();

                        while(RS.next()){
                          vector.add(RS.getString(1));
                        }

			RS.close();
                        theReturn = (String[]) vector.toArray(new String[0]);

		}
		finally{
			if(Stmt != null){
				Stmt.close();
			}
			if (conn != null){
				freeConnection(conn);
			}
		}
                return theReturn;
	}




        public static boolean execute(String sqlString)throws Exception{
		Connection conn= null;
		Statement Stmt= null;
                boolean theReturn = false;
		try{
			conn = getConnection();
			Stmt = conn.createStatement();
                        theReturn = Stmt.execute(sqlString);
		}
		finally{
			if(Stmt != null){
				Stmt.close();
			}
			if (conn != null){
				freeConnection(conn);
			}
		}
                return theReturn;
	}
}
