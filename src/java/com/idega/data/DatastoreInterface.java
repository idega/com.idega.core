//idega 2000-2001 - Tryggvi Larusson
/*
*Copyright 2000-2001 idega.is All Rights Reserved.
*/

package com.idega.data;




import java.sql.*;
import javax.naming.*;
import javax.sql.*;
import java.util.*;
import com.idega.util.database.*;
import java.io.*;

/**
*@author <a href="mailto:tryggvi@idega.is">Tryggvi Larusson</a>
*@version 1.3
*/
public abstract class DatastoreInterface{

  private static Hashtable interfacesHashtable;

  public static DatastoreInterface getInstance(String datastoreType){
    DatastoreInterface theReturn = null;
    String className;
    if (interfacesHashtable == null){
      interfacesHashtable = new Hashtable();
    }

    if (datastoreType.equals("oracle")){
       className = "com.idega.data.OracleDatastoreInterface";
    }
    else if (datastoreType.equals("interbase")){
       className = "com.idega.data.InterbaseDatastoreInterface";
    }
    else if (datastoreType.equals("mysql")){
        className = "com.idega.data.MySQLDatastoreInterface";
    }
    else{
        className = "unimplemented";
    }

    theReturn = (DatastoreInterface)interfacesHashtable.get(className);

    if (theReturn == null){
            try{
              theReturn = (DatastoreInterface)Class.forName(className).newInstance();
              interfacesHashtable.put(className,theReturn);
            }
            catch(Exception ex){
              System.err.println("There was an error in com.idega.data.DatastoreInterface.getInstance(String className): "+ex.getMessage());
            }

    }

    return theReturn;

  }

  public static String getDatastoreType(String datasourceName){
    Connection conn=null;
    String theReturn="";
    try{
      conn = ConnectionBroker.getConnection(datasourceName);
      theReturn = getDataStoreType(conn);
    }
    finally{
      ConnectionBroker.freeConnection(datasourceName,conn);
    }
    return theReturn;
  }

  public static DatastoreInterface getInstance(Connection connection){
      //String datastoreType = getDataStoreType(connection);
      //if(datastoreType.equals("idega"){
      if(connection instanceof com.idega.data.DatastoreConnection){
         return ((DatastoreConnection)connection).getDatastoreInterface();
      }
      return getInstance(getDataStoreType(connection));
  }

  /**
   * <b>This</b> function is bla
   */
  public static DatastoreInterface getInstance(GenericEntity entity){
    String datastoreType=null;
    try{
    Connection conn = entity.getConnection();
    datastoreType=getDataStoreType(conn);
    entity.freeConnection(conn);

    }
    catch(SQLException ex){
      System.err.println("Exception in DatastoreInterface.getInstance(GenericEntity entity): "+ex.getMessage());
    }

    return getInstance(datastoreType);
  }


	/**
	 * Returns the type of the underlying datastore - returns: "mysql", "interbase", "oracle", "unimplemented"
	 */
	public static String getDataStoreType(Connection connection){
            String dataStoreType;
			if (connection != null){
		 		if(connection instanceof com.idega.data.DatastoreConnection){
					return getDataStoreType(((DatastoreConnection)connection).getUnderLyingConnection());
				}
				else{
					if (connection.getClass().getName().indexOf("oracle") != -1 ){
						dataStoreType = "oracle";
					}
					else if (connection.getClass().getName().indexOf("interbase") != -1 ){
						dataStoreType = "interbase";
					}
					else if (connection.getClass().getName().indexOf("mysql") != -1 ){
						dataStoreType =  "mysql";
					}
            				else if (connection.getClass().getName().indexOf("idega") != -1 ){
               					dataStoreType = "idega";
            				}
					else{
						dataStoreType = "unimplemented";
					}
				}
			}
			else{
			  dataStoreType =  "";
			}

		return dataStoreType;
	}



  public abstract String getSQLType(String javaClassName,int maxlength);


  public void createEntityRecord(GenericEntity entity)throws Exception{
		Connection conn= null;
		Statement Stmt= null;
		try{
			conn = entity.getConnection();
			Stmt = conn.createStatement();
			int i = Stmt.executeUpdate(getCreationStatement(entity));

		}
                /*catch(SQLException ex){
                  System.out.println("There was an error in DatastoreInterface.createEntityRecord(GenericEntity entity): "+ex.getMessage());
                }*/
		finally{
			if(Stmt != null){
				Stmt.close();
			}
			if (conn != null){
				entity.freeConnection(conn);
			}
		}
      createTrigger(entity);
      createForeignKeys(entity);
  }


  protected String getCreationStatement(GenericEntity entity){
		String returnString = "create table "+entity.getTableName()+"(";
		String[] names = entity.getColumnNames();
		for (int i = 0; i < names.length; i++){

                    /*if (entity.getMaxLength(names[i]) == -1){
                      if (entity.getStorageClassName(names[i]).equals("java.lang.String")){
                        returnString = 	returnString + names[i]+" "+getSQLType(entity.getStorageClassName(names[i]))+"(255)";
                      }
                      else{
                        returnString = 	returnString + names[i]+" "+getSQLType(entity.getStorageClassName(names[i]));
                      }

                    }
                    else{

                      returnString = 	returnString + names[i]+" "+getSQLType(entity.getStorageClassName(names[i]))+"("+entity.getMaxLength(names[i])+")";
                    }*/

                    returnString = 	returnString + names[i]+" "+getSQLType(entity.getStorageClassName(names[i]),entity.getMaxLength(names[i]));

		    if (!entity.getIfNullable(names[i])){
                      returnString = 	returnString + " NOT NULL";
                    }
                    if (entity.isPrimaryKey(names[i])){
                      returnString = 	returnString + " PRIMARY KEY";
                    }

                    if (i!=names.length-1){
                      returnString = returnString+",";
                    }
		}
                returnString = returnString +")";
                System.out.println(returnString);
		return returnString;
}


  public void deleteEntityRecord(GenericEntity entity)throws Exception{
    deleteTable(entity);
  }

  protected void deleteTable(GenericEntity entity)throws Exception{
		Connection conn= null;
		Statement Stmt= null;
		try{
			conn = entity.getConnection();
			Stmt = conn.createStatement();
			int i = Stmt.executeUpdate("drop table "+entity.getTableName());
		}
		finally{
			if(Stmt != null){
				Stmt.close();
			}
			if (conn != null){
				entity.freeConnection(conn);
			}
		}
  }



	public abstract void createTrigger(GenericEntity entity)throws Exception;

	public abstract void createForeignKeys(GenericEntity entity)throws Exception;



	public void populateBlob(BlobWrapper blob){
            try{
		PreparedStatement myPreparedStatement = blob.getConnection().prepareStatement("insert into "+blob.getEntity().getTableName()+"("+blob.getTableColumnName()+") values(?) where "+blob.getEntity().getIDColumnName()+"='"+blob.getEntity().getID()+"'");
		// ByteArrayInputStream byteinstream = new ByteArrayInputStream(longbbuf);
		//InputStream byteinstream = new InputStream(longbbuf);

		//OutputStream out = blob.getOutputStream();
		InputStream byteinstream = blob.getInputStreamForBlobWrite();
                //InputStream myInputStream = new InputStream();


					/*byte	buffer[]= new byte[1024];
					int		noRead	= 0;

					noRead	= myInputStream.read( buffer, 0, 1023 );*/

					//Write out the file to the browser
					/*while ( noRead != -1 ){
						output.write( buffer, 0, noRead );
						noRead	= myInputStream.read( buffer, 0, 1023 );

					}*/


		myPreparedStatement.setBinaryStream(1, byteinstream, byteinstream.available() );

		myPreparedStatement.execute();
		myPreparedStatement.close();
              }
              catch(Exception ex){
                System.err.println("Exception in DatastoreInterface.populateBlob: "+ex.getMessage());
                ex.printStackTrace(System.err);
              }

	}


   public boolean isConnectionOK(Connection conn)
   {
      Statement testStmt = null;
      try
      {
         if (!conn.isClosed())
         {
            // Try to createStatement to see if it's really alive
            testStmt = conn.createStatement();
            testStmt.close();
         }
         else
         {
            return false;
         }
      }
      catch (SQLException e)
      {
         if (testStmt != null)
         {
            try
            {
               testStmt.close();
            }
            catch (SQLException se)
            { }
         }
         //logWriter.log(e, "Pooled Connection was not okay",
         //                  LogWriter.ERROR);
         return false;
      }
      return true;
   }




}
