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



	/*public void populateBlob(BlobWrapper blob){
            try{
		PreparedStatement myPreparedStatement = blob.getConnection().prepareStatement("insert into "+blob.getEntity().getTableName()+"("+blob.getTableColumnName()+") values(?) where "+blob.getEntity().getIDColumnName()+"='"+blob.getEntity().getID()+"'");
		// ByteArrayInputStream byteinstream = new ByteArrayInputStream(longbbuf);
		//InputStream byteinstream = new InputStream(longbbuf);

		//OutputStream out = blob.getOutputStream();
		InputStream byteinstream = blob.getInputStreamForBlobWrite();
                //InputStream myInputStream = new InputStream();


					//byte	buffer[]= new byte[1024];
					//int		noRead	= 0;

					//noRead	= myInputStream.read( buffer, 0, 1023 );

					//Write out the file to the browser
					//while ( noRead != -1 ){
					//	output.write( buffer, 0, noRead );
					//	noRead	= myInputStream.read( buffer, 0, 1023 );
          //


		myPreparedStatement.setBinaryStream(1, byteinstream, byteinstream.available() );

		myPreparedStatement.execute();
		myPreparedStatement.close();
              }
              catch(Exception ex){
                System.err.println("Exception in DatastoreInterface.populateBlob: "+ex.getMessage());
                ex.printStackTrace(System.err);
              }

	}*/


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



  public void insert(GenericEntity entity)throws Exception{

    this.executeBeforeInsert(entity);

    Connection conn= null;
		//Statement Stmt= null;
		PreparedStatement Stmt = null;
		ResultSet RS = null;
		try{
			conn = entity.getConnection();
      //Stmt = conn.createStatement();
      //int i = Stmt.executeUpdate("insert into "+entity.getTableName()+"("+entity.getCommaDelimitedColumnNames()+") values ("+entity.getCommaDelimitedColumnValues()+")");
      String statement = "insert into "+entity.getTableName()+"("+entity.getCommaDelimitedColumnNames()+") values ("+entity.getQuestionmarksForColumns()+")";
      //System.out.println(statement);
      Stmt = conn.prepareStatement (statement);
      setForPreparedStatement(Stmt,entity);
      Stmt.execute();
		}
		finally{
			if (RS != null){
				RS.close();
			}
			if(Stmt != null){
				Stmt.close();
			}
			if (conn != null){
				entity.freeConnection(conn);
			}
		}
    this.executeAfterInsert(entity);
	}


	/**
	**Creates a unique ID for the ID column
	**/
	public int createUniqueID(GenericEntity entity) throws Exception{
		int returnInt = -1;
		Connection conn = null;
		Statement stmt = null;
		ResultSet RS = null;
		try{

			conn = entity.getConnection();
			String datastoreType=DatastoreInterface.getDataStoreType(conn);

			/*if (datastoreType.equals("interbase")){
				stmt = conn.createStatement();
				RS = stmt.executeQuery("SELECT GEN_ID("+getInterbaseGeneratorName(entity)+", 1) FROM RDB$DATABASE");
				RS.next();
				returnInt = RS.getInt(1);
			}
			else if (datastoreType.equals("oracle")){
				stmt = conn.createStatement();
				RS = stmt.executeQuery("SELECT "+getOracleSequenceName(entity)+".nextval  FROM dual");
				RS.next();
				returnInt = RS.getInt(1);
			}*/
				stmt = conn.createStatement();
				RS = stmt.executeQuery(getCreateUniqueIDQuery(entity));
				RS.next();
				returnInt = RS.getInt(1);
		}
		finally{
			if (RS != null){
				RS.close();
			}
			if (stmt != null){
				stmt.close();
			}
			if (conn != null){
				entity.freeConnection(conn);
			}
		}
		return returnInt;
	}

  protected String getCreateUniqueIDQuery(GenericEntity entity)throws Exception{
    return "";
  }

  protected void executeBeforeInsert(GenericEntity entity)throws Exception{
  }

  protected void executeAfterInsert(GenericEntity entity)throws Exception{
    if( entity.hasLobColumn() ) insertBlob(entity);
  }

  protected void insertBlob(GenericEntity entity)throws Exception{
  }


	protected String setForPreparedStatement(PreparedStatement statement,GenericEntity entity)throws SQLException{
		String returnString = "";
		String[] names = entity.getColumnNames();
                int questionmarkCount=1;
		for (int i = 0; i < names.length; i++){
			if (!entity.isNull(names[i])){
          //if (returnString.equals("")){
          //	returnString = 	"'"+getStringColumnValue(names[i])+"'";
          //}
          //else{
          //	returnString = 	returnString + ",'" + getStringColumnValue(names[i])+"'";
          //}
          insertIntoPreparedStatement(names[i],statement,questionmarkCount,entity);
          questionmarkCount++;

      }
		}
		return returnString;
	}

	private void insertIntoPreparedStatement(String columnName,PreparedStatement statement, int index,GenericEntity entity)throws SQLException{
          String storageClassName = entity.getStorageClassName(columnName);
		if (storageClassName.equals("java.lang.Integer")){
			statement.setInt(index,entity.getIntColumnValue(columnName));
		}
		else if (storageClassName.equals("java.lang.Boolean")){
                  boolean bool = entity.getBooleanColumnValue(columnName);
                  if (bool){
                    statement.setString(index,"Y");
                  }
                  else{
                    statement.setString(index,"N");
                  }
		}
		else if (storageClassName.equals("java.lang.String")){
                    statement.setString(index,entity.getStringColumnValue(columnName));
		}
		else if (storageClassName.equals("java.lang.Float")){
                    statement.setFloat(index,entity.getFloatColumnValue(columnName));
		}
		else if (storageClassName.equals("java.lang.Double")){
                  statement.setDouble(index,entity.getDoubleColumnValue(columnName));
		}
		else if (storageClassName.equals("java.sql.Timestamp")){
                  statement.setTimestamp(index,(Timestamp)entity.getColumnValue(columnName));
		}
		else if (storageClassName.equals("java.sql.Time")){
                  statement.setTime(index,(Time)entity.getColumnValue(columnName));
		}
		else if (storageClassName.equals("java.sql.Date")){
                    statement.setDate(index,(java.sql.Date)entity.getColumnValue(columnName));
		}
		else if (storageClassName.equals("com.idega.util.Gender")){
                    statement.setString(index,entity.getColumnValue(columnName).toString());
		}
		else if (storageClassName.equals("com.idega.data.BlobWrapper")){

                    //statement.setDate(index,(java.sql.Date)getColumnValue(columnName));
		}
		else{
		  statement.setObject(index,entity.getColumnValue(columnName));
		}
	}

	public void update(GenericEntity entity)throws Exception{
		Connection conn= null;
		PreparedStatement Stmt= null;
		try{
			conn = entity.getConnection();
//			Stmt = conn.createStatement();

                                String statement = "update "+entity.getTableName()+" set "+entity.getAllColumnsAndQuestionMarks()+" where "+entity.getIDColumnName()+"="+entity.getID();
                                //System.out.println(statement);
		                Stmt = conn.prepareStatement (statement);
                                setForPreparedStatement(Stmt,entity);
                                Stmt.execute();

			//int i = Stmt.executeUpdate("update "+entity.getEntityName()+" set "+entity.getAllColumnsAndValues()+" where "+entity.getIDColumnName()+"="+entity.getID());
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


}
