/*
 * $Id: DatastoreInterface.java,v 1.38 2002/02/20 00:03:36 eiki Exp $
 *
 * Copyright (C) 2001 Idega hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 *
 */
package com.idega.data;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.sql.Time;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import com.idega.util.database.ConnectionBroker;
import java.io.InputStream;
import java.io.IOException;


/**
 * @author <a href="mailto:tryggvi@idega.is">Tryggvi Larusson</a>
 * @version 1.3
  */
public abstract class DatastoreInterface{
  private static Hashtable interfacesHashtable;

  private final static int STATEMENT_INSERT=1;
  private final static int STATEMENT_UPDATE=2;

  protected boolean useTransactionsInEntityCreation=true;
  protected IDOTableCreator _TableCreator;

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
    else if (datastoreType.equals("sapdb")){
        className = "com.idega.data.SapDBDatastoreInterface";
    }
    else if (datastoreType.equals("db2")){
        className = "com.idega.data.DB2DatastoreInterface";
    }
    else if (datastoreType.equals("informix")){
        className = "com.idega.data.InformixDatastoreInterface";
    }
    else{
        //className = "unimplemented DatastoreInterface";
        throw new IDONoDatastoreError();
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
    catch(Exception ex){
    //  System.err.println("Exception in DatastoreInterface.getInstance(GenericEntity entity): "+ex.getMessage());
    //}
    //catch(NullPointerException npe){
    //
      ex.printStackTrace();
      throw new IDONoDatastoreError();
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

                                        String checkString = null;
                                        try{
                                          checkString = connection.getMetaData().getDatabaseProductName().toLowerCase();
                                        }
                                        catch(SQLException e){
                                          //Old Check
                                          e.printStackTrace();
                                          checkString = connection.getClass().getName();
                                        }

					if (checkString.indexOf("oracle") != -1 ){
						dataStoreType = "oracle";
					}
					else if (checkString.indexOf("interbase") != -1 ){
						dataStoreType = "interbase";
					}
					else if (checkString.indexOf("mysql") != -1 ){
						dataStoreType =  "mysql";
					}
					else if (checkString.indexOf("sap") != -1 ){
						dataStoreType =  "sapdb";
					}
					else if (checkString.indexOf("db2") != -1 ){
						dataStoreType =  "db2";
					}
					else if (checkString.indexOf("informix") != -1 ){
						dataStoreType =  "informix";
					}
            				else if (checkString.indexOf("idega") != -1 ){
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

  public String getIDColumnType(){
    return "INTEGER";
  }

  public IDOTableCreator getTableCreator(){
    if(_TableCreator==null){
      _TableCreator= new IDOTableCreator(this);
    }
    return _TableCreator;
  }


    public void createEntityRecord(GenericEntity entity)throws Exception{
      getTableCreator().createEntityRecord(entity);
    }






  public void executeBeforeCreateEntityRecord(GenericEntity entity)throws Exception{
  }

  public void executeAfterCreateEntityRecord(GenericEntity entity)throws Exception{
  }




  public void deleteEntityRecord(GenericEntity entity)throws Exception{
    getTableCreator().deleteEntityRecord(entity);
  }



  public abstract void createTrigger(GenericEntity entity)throws Exception;

  //public abstract void createForeignKeys(GenericEntity entity)throws Exception;


  protected Object executeQuery(GenericEntity entity,String SQLCommand)throws Exception{
      Connection conn = null;
      Statement Stmt = null;
      Object theReturn = null;
      try{
        conn = entity.getConnection();

        Stmt = conn.createStatement();
        //System.out.println(SQLCommand);
        Stmt.executeQuery(SQLCommand);
      }
      finally {
      if (Stmt != null) {
        Stmt.close();
      }
      if (conn != null) {
        entity.freeConnection(conn);
      }
      }
      return theReturn;

  }

  protected int executeUpdate(GenericEntity entity,String SQLCommand)throws Exception{
      Connection conn = null;
      Statement Stmt = null;
      int theReturn = 0;
      try{
        conn = entity.getConnection();

        //conn.commit();
        Stmt = conn.createStatement();
        System.out.println(SQLCommand);
        theReturn= Stmt.executeUpdate(SQLCommand);
      }
      finally {
      if (Stmt != null) {
        Stmt.close();
      }
      if (conn != null) {
        entity.freeConnection(conn);
      }
      }
      return theReturn;

  }


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
                  StringBuffer statement = new StringBuffer("");
                  statement.append("insert into ");
                  statement.append(entity.getTableName());
                  statement.append("(");
                  statement.append(getCommaDelimitedColumnNames(entity));
                  statement.append(") values (");
                  statement.append(getQuestionmarksForColumns(entity));
                  statement.append(")");
                  //System.out.println(statement);
                  Stmt = conn.prepareStatement (statement.toString());
                  setForPreparedStatement(STATEMENT_INSERT,Stmt,entity);
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
            entity.setEntityState(entity.STATE_IN_SYNCH_WITH_DATASTORE);
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

    if( entity.hasMetaDataRelationship() ) crunchMetaData(entity);
  }

  protected void executeBeforeUpdate(GenericEntity entity)throws Exception{
  }

  protected void executeAfterUpdate(GenericEntity entity)throws Exception{
    if(!supportsBlobInUpdate()){
      if( entity.hasLobColumn() ) insertBlob(entity);
    }
    if( entity.hasMetaDataRelationship() ) crunchMetaData(entity);
  }

  protected void executeBeforeDelete(GenericEntity entity)throws Exception{

  }


  protected void executeAfterDelete(GenericEntity entity)throws Exception{
  }

  protected void crunchMetaData(GenericEntity entity)throws SQLException{
    if( entity.metaDataHasChanged() ){//else do nothing
      int length;
      MetaData data;

      Hashtable metadata = entity.getMetaDataAttributes();
      Hashtable ids = entity.getMetaDataIds();
      Vector insert = entity.getMetaDataInsertVector();
      Vector delete = entity.getMetaDataDeleteVector();
      Vector update = entity.getMetaDataUpdateVector();
      EntityBulkUpdater updater = new EntityBulkUpdater(entity);//could this be static?

      if( insert!= null ){
        length = insert.size();
        for (int i = 0; i < length; i++) {
          data = new MetaData();
          data.setMetaDataNameAndValue((String) insert.elementAt(i), (String) metadata.get((String) insert.elementAt(i)));
          updater.add(data,EntityBulkUpdater.insert);
        }
      }
      //else       System.out.println("insert is null");

      if( update!= null ){
        length = update.size();
        System.out.println("update size: "+length);
        for (int i = 0; i < length; i++) {
          //System.out.println("updating: "+i);
          data = new MetaData();//do not construct with id to avoid database access
          if(ids==null) System.out.println("ids is null");
          data.setID((Integer) ids.get(update.elementAt(i)));
          //System.out.println("ID: "+data.getID());
          data.setMetaDataNameAndValue((String) update.elementAt(i), (String) metadata.get((String) update.elementAt(i)));
          updater.add(data,EntityBulkUpdater.update);
        }
      }
      //else       System.out.println("update is null");

      if( delete!= null ){
        length = delete.size();
        for (int i = 0; i < length; i++) {
          data = new MetaData();
          data.setID((Integer) ids.get(delete.elementAt(i)));
          updater.add(data,EntityBulkUpdater.delete);
        }
      }
      //else       System.out.println("delete is null");

      updater.execute();
      entity.metaDataHasChanged(false);//so we don't do anything next time

    }

  }


  protected void insertBlob(GenericEntity entity)throws Exception{

    StringBuffer statement ;
    Connection Conn = null;

    try{

      statement = new StringBuffer("");
      statement.append("update ");
      statement.append(entity.getTableName());
      statement.append(" set ");
      statement.append(entity.getLobColumnName());
      statement.append("=? where ");
      statement.append(entity.getIDColumnName());
      statement.append(" = '");
      statement.append(entity.getID());
      statement.append("'");

      //System.out.println(statement);
      //System.out.println("In insertBlob() in DatastoreInterface");
      BlobWrapper wrapper = entity.getBlobColumnValue(entity.getLobColumnName());
      if(wrapper!=null){
        //System.out.println("In insertBlob() in DatastoreInterface wrapper!=null");
        //Conn.setAutoCommit(false);
        InputStream instream = wrapper.getInputStreamForBlobWrite();
        if(instream!=null){
          //System.out.println("In insertBlob() in DatastoreInterface instream != null");
          Conn = entity.getConnection();
          //if(Conn== null){ System.out.println("In insertBlob() in DatastoreInterface conn==null"); return;}
          //BufferedInputStream bin = new BufferedInputStream(instream);
          PreparedStatement PS = Conn.prepareStatement(statement.toString());
          //System.out.println("bin.available(): "+bin.available());
          //PS.setBinaryStream(1, bin, 0 );
          //PS.setBinaryStream(1, instream, instream.available() );

          this.setBlobstreamForStatement(PS,instream,1);
          PS.executeUpdate();
          PS.close();
          //System.out.println("bin.available(): "+bin.available());
          instream.close();
         // bin.close();
        }
        //Conn.commit();
        //Conn.setAutoCommit(true);
      }
    }
    catch(SQLException ex){ex.printStackTrace(); System.err.println( "error uploading blob to db for "+entity.getClass().getName());}
    catch(Exception ex){ex.printStackTrace();}
    finally{
      if(Conn != null) entity.freeConnection(Conn);
    }
  }


	protected String setForPreparedStatement(int insertOrUpdate,PreparedStatement statement,GenericEntity entity)throws SQLException{
		String returnString = "";
		String[] names = entity.getColumnNames();
                int questionmarkCount=1;
                if(insertOrUpdate==STATEMENT_UPDATE){
                  for (int i = 0; i < names.length; i++){
                          if (isValidColumnForUpdateList(entity,names[i])){
                              //if (returnString.equals("")){
                              //	returnString = 	"'"+getStringColumnValue(names[i])+"'";
                              //}
                              //else{
                              //	returnString = 	returnString + ",'" + getStringColumnValue(names[i])+"'";
                              //}
                              //System.out.println(names[i]);
                              insertIntoPreparedStatement(names[i],statement,questionmarkCount,entity);
                              questionmarkCount++;

                          }
                  }
                }
                else if(insertOrUpdate==STATEMENT_INSERT){
                  for (int i = 0; i < names.length; i++){
                          if (isValidColumnForInsertList(entity,names[i])){
                              //if (returnString.equals("")){
                              //	returnString = 	"'"+getStringColumnValue(names[i])+"'";
                              //}
                              //else{
                              //	returnString = 	returnString + ",'" + getStringColumnValue(names[i])+"'";
                              //}
                              //System.out.println(names[i]);
                              insertIntoPreparedStatement(names[i],statement,questionmarkCount,entity);
                              questionmarkCount++;
                          }
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
                    handleBlobUpdate(columnName,statement,index,entity);
                    //statement.setDate(index,(java.sql.Date)getColumnValue(columnName));
		}
		else{
		  statement.setObject(index,entity.getColumnValue(columnName));
		}
	}

        public void handleBlobUpdate(String columnName,PreparedStatement statement, int index,GenericEntity entity){
          BlobWrapper wrapper = entity.getBlobColumnValue(columnName);
          //System.out.println("DatastoreInterface, in handleBlobUpdate, columnName="+columnName+" index="+index);
          if(wrapper!=null){
            InputStream stream = wrapper.getInputStreamForBlobWrite();
            //System.out.println("DatastoreInterface, in handleBlobUpdate wrapper!=null");
            if(stream!=null){
              try{
                //System.out.println("in handleBlobUpdate, stream != null");
                //java.io.BufferedInputStream bin = new java.io.BufferedInputStream( stream );
                //statement.setBinaryStream(index, bin, bin.available() );
                //System.out.println("bin.available(): "+bin.available());
                //System.out.println("stream.available(): "+stream.available());
                //statement.setBinaryStream(index, stream, stream.available() );
                setBlobstreamForStatement(statement,stream,index);
              }
              catch(Exception e){
                //System.err.println("Error updating BLOB field in "+entity.getClass().getName());
                e.printStackTrace(System.err);
              }
            }
          }

        }


    public void setBlobstreamForStatement(PreparedStatement statement,InputStream stream,int index)throws SQLException,IOException{
      statement.setBinaryStream(index, stream, stream.available() );
    }

	public void update(GenericEntity entity)throws Exception{

      if(entity.columnsHaveChanged()){
          executeBeforeUpdate(entity);
          Connection conn= null;
          PreparedStatement Stmt= null;
          try{
            conn = entity.getConnection();
//		  Stmt = conn.createStatement();

            StringBuffer statement = new StringBuffer("");
            statement.append("update ");
            statement.append(entity.getTableName());
            statement.append(" set ");
            statement.append(getAllColumnsAndQuestionMarks(entity));
            statement.append(" where ");
            statement.append(entity.getIDColumnName());
            statement.append("=");
            statement.append(entity.getID());

            //System.out.println(statement);
            Stmt = conn.prepareStatement(statement.toString());
            setForPreparedStatement(STATEMENT_UPDATE,Stmt,entity);
            //Stmt.execute();
            Stmt.executeUpdate();

            //int i = Stmt.executeUpdate("update "+entity.getTableName()+" set "+entity.getAllColumnsAndValues()+" where "+entity.getIDColumnName()+"="+entity.getID());
          }
          finally{
            if(Stmt != null){
                    Stmt.close();
            }
            if (conn != null){
                    entity.freeConnection(conn);
            }
          }

          executeAfterUpdate(entity);

          entity.setEntityState(entity.STATE_IN_SYNCH_WITH_DATASTORE);
      }

	}

  public void update(GenericEntity entity, Connection conn)throws Exception{
    executeBeforeUpdate(entity);
    PreparedStatement Stmt = null;
    try {
      StringBuffer statement = new StringBuffer("");
      statement.append("update ");
      statement.append(entity.getTableName());
      statement.append(" set ");
      statement.append(getAllColumnsAndQuestionMarks(entity));
      statement.append(" where ");
      statement.append(entity.getIDColumnName());
      statement.append("=");
      statement.append(entity.getID());

      Stmt = conn.prepareStatement (statement.toString());
      setForPreparedStatement(STATEMENT_UPDATE,Stmt,entity);
      Stmt.execute();
    }
    finally{
      if(Stmt != null){
              Stmt.close();
      }
    }

    executeAfterUpdate(entity);
    entity.setEntityState(entity.STATE_IN_SYNCH_WITH_DATASTORE);
  }

  public void insert(GenericEntity entity, Connection conn) throws Exception {
    executeBeforeInsert(entity);
    PreparedStatement Stmt = null;
    ResultSet RS = null;
    try {
      StringBuffer statement = new StringBuffer("");
      statement.append("insert into ");
      statement.append(entity.getTableName());
      statement.append("(");
      statement.append(getCommaDelimitedColumnNames(entity));
      statement.append(") values (");
      statement.append(getQuestionmarksForColumns(entity));
      statement.append(")");

      //System.out.println(statement);
      Stmt = conn.prepareStatement (statement.toString());
      setForPreparedStatement(STATEMENT_INSERT,Stmt,entity);
      Stmt.execute();
    }
    finally {
            if (RS != null) {
                    RS.close();
            }
            if(Stmt != null) {
                    Stmt.close();
            }
    }
    executeAfterInsert(entity);
    entity.setEntityState(entity.STATE_IN_SYNCH_WITH_DATASTORE);
  }

  public void delete(GenericEntity entity)throws Exception{
    executeBeforeInsert(entity);
      Connection conn= null;
      Statement Stmt= null;
      try{
              conn = entity.getConnection();
              Stmt = conn.createStatement();
              StringBuffer statement = new StringBuffer("");
              statement.append("delete from  ");
              statement.append(entity.getTableName());
              statement.append(" where ");
              statement.append(entity.getIDColumnName());
              statement.append("=");
              statement.append(entity.getID());
              Stmt.executeUpdate(statement.toString());
          if( entity.hasMetaDataRelationship() ){
            deleteMetaData(entity,conn);
          }

      }
      finally{
              if(Stmt != null){
                      Stmt.close();
              }
              if (conn != null){
                      entity.freeConnection(conn);
              }
      }
      executeAfterInsert(entity);
      entity.setEntityState(entity.STATE_DELETED);
  }

  public void delete(GenericEntity entity, Connection conn)throws Exception{
    executeBeforeInsert(entity);
    Statement Stmt= null;
    try {
      Stmt = conn.createStatement();
      StringBuffer statement = new StringBuffer("");
      statement.append("delete from  ");
      statement.append(entity.getTableName());
      statement.append(" where ");
      statement.append(entity.getIDColumnName());
      statement.append("=");
      statement.append(entity.getID());
      Stmt.executeUpdate(statement.toString());

      if( entity.hasMetaDataRelationship() ){
        deleteMetaData(entity,conn);
      }
    }
    finally{
            if(Stmt != null){
                    Stmt.close();
            }
    }
    executeAfterInsert(entity);
    entity.setEntityState(entity.STATE_DELETED);

  }

  public void deleteMetaData(GenericEntity entity, Connection conn)throws Exception{
    Statement Stmt = null;
    Statement stmt2 = null;
    try{
      MetaData metadata = (MetaData) GenericEntity.getStaticInstance(MetaData.class);
      Stmt = conn.createStatement();
      String middletable = entity.getNameOfMiddleTable(metadata,entity);
      String metadataIdColumn = metadata.getIDColumnName();
      String metadataname = metadata.getTableName();

      //get all the id's of the metadata
      StringBuffer statement = new StringBuffer("");
      statement.append("select ");
      statement.append(metadataIdColumn);
      statement.append(" from ");
      statement.append(middletable);
      statement.append(',');
      statement.append(metadataname);
      statement.append(" where ");
      statement.append(middletable);
      statement.append('.');
      statement.append(entity.getIDColumnName());
      statement.append('=');
      statement.append(entity.getID());
      statement.append(" and ");
      statement.append(middletable);
      statement.append('.');
      statement.append(metadataIdColumn);
      statement.append('=');
      statement.append(metadataname);
      statement.append('.');
      statement.append(metadataIdColumn);

      ResultSet RS = Stmt.executeQuery(statement.toString());

      stmt2 = conn.createStatement();
      StringBuffer statement2;
      //delete thos id's
      while(RS.next()){
        statement2 = new StringBuffer("");
        statement2.append("delete from ");
        statement2.append(metadataname);
        statement2.append(" where ");
        statement2.append(metadataIdColumn);
        statement2.append('=');
        statement2.append(RS.getString(1));
        stmt2.executeUpdate(statement2.toString());
      }

      if( RS!=null ) RS.close();

      //delete from the middle table
      Stmt = conn.createStatement();
      statement = new StringBuffer("");
      statement.append("delete from ");
      statement.append(middletable);
      statement.append(" where ");
      statement.append(entity.getIDColumnName());
      statement.append('=');
      statement.append(entity.getID());
      Stmt.executeUpdate(statement.toString());
    }
    finally{
      if(Stmt != null){
        Stmt.close();
      }
      if(stmt2 != null){
        stmt2.close();
      }
    }
  }

  public void deleteMetaData(GenericEntity entity)throws Exception{
    Connection conn= null;
    try{
      conn = entity.getConnection();
      deleteMetaData(entity,conn);
    }
    finally{
      if (conn != null){
        entity.freeConnection(conn);
      }
    }
  }



  public boolean supportsBlobInUpdate(){
    return true;
  }



	/**
	*Used to generate the ?,? mark list for preparedstatement
	**/
	protected String getQuestionmarksForColumns(GenericEntity entity){
		String returnString = "";
		String[] names = entity.getColumnNames();
		for (int i = 0; i < names.length; i++){
			if(isValidColumnForInsertList(entity,names[i])){
      //if (!isNull(names[i])){
				if (returnString.equals("")){
					returnString = 	"?";
				}
				else{
					returnString = 	returnString + ",?";
				}
			}
		}
		return returnString;
	}


  boolean isValidColumnForUpdateList(GenericEntity entity,String columnName){
    boolean isIDColumn = entity.getIDColumnName().equalsIgnoreCase(columnName);
    if(isIDColumn){
      return false;
    }
    else{
      if(this.supportsBlobInUpdate()){
        if (entity.isNull(columnName)){
          return false;
        }
        else{
          if(entity.getStorageClassType(columnName)==EntityAttribute.TYPE_COM_IDEGA_DATA_BLOBWRAPPER){
            BlobWrapper wrapper = (BlobWrapper)entity.getColumnValue(columnName);
            if(wrapper==null){
              return false;
            }
            else{
              return wrapper.isReadyForUpdate();
            }
          }
          return true;
        }
      }
      else{
        if (entity.isNull(columnName)){
          return false;
        }
        else{
          if(entity.getStorageClassType(columnName)==EntityAttribute.TYPE_COM_IDEGA_DATA_BLOBWRAPPER){
            return false;
          }
          return true;
        }
      }
    }
  }


  protected static boolean isValidColumnForInsertList(GenericEntity entity,String columnName){
      if (entity.isNull(columnName)){
        return false;
      }
      else{
        if(entity.getStorageClassType(columnName)==EntityAttribute.TYPE_COM_IDEGA_DATA_BLOBWRAPPER){
          return false;
        }
        return true;
      }
  }

  protected static boolean isValidColumnForSelectList(GenericEntity entity,String columnName){
    return !(entity.getStorageClassType(columnName)==EntityAttribute.TYPE_COM_IDEGA_DATA_BLOBWRAPPER);
  }

  protected static String getCommaDelimitedColumnNamesForSelect(GenericEntity entity){
    String newCachedColumnNameList = entity.getStaticInstance()._cachedColumnNameList;
    if(newCachedColumnNameList==null){
      StringBuffer returnString = null;
      String[] names = entity.getColumnNames();
      for (int i = 0; i < names.length; i++){
        if (isValidColumnForSelectList(entity,names[i])){
          if (returnString==null){
            returnString = new StringBuffer("");
            returnString.append(names[i]);
          }
          else{
            returnString.append(",");
            returnString.append(names[i]);
          }
        }
      }
      newCachedColumnNameList = returnString.toString();
    }
    return newCachedColumnNameList;
  }

  protected static String getCommaDelimitedColumnNames(GenericEntity entity){
    String newCachedColumnNameList = entity.getStaticInstance()._cachedColumnNameList;
    if(newCachedColumnNameList==null){
      StringBuffer returnString = null;
      String[] names = entity.getColumnNames();
      for (int i = 0; i < names.length; i++){
        if (isValidColumnForInsertList(entity,names[i])){
          if (returnString==null){
            returnString = new StringBuffer("");
            returnString.append(names[i]);
          }
          else{
            returnString.append(",");
            returnString.append(names[i]);
          }
        }
      }
      newCachedColumnNameList = returnString.toString();
    }
    return newCachedColumnNameList;
  }




  protected static String getCommaDelimitedColumnValues(GenericEntity entity){
    StringBuffer returnString = null;
    String[] names = entity.getColumnNames();
    for (int i = 0; i < names.length; i++){
      if (isValidColumnForInsertList(entity,names[i])){
        if (returnString==null){
          returnString = new StringBuffer("");
          returnString.append("'");
          returnString.append(entity.getStringColumnValue(names[i]));
          returnString.append("'");
        }
        else{
          returnString.append(",'");
          returnString.append(entity.getStringColumnValue(names[i]));
          returnString.append("'");
        }
      }
    }
    return returnString.toString();
  }



  protected String getAllColumnsAndQuestionMarks(GenericEntity entity){
    StringBuffer returnString = null;
    String[] names = entity.getColumnNames();
    String questionmark = "=?";

    for(int i=0;i<names.length;i++){
    //for (Enumeration e = columns.keys(); e.hasMoreElements();){
    //for (Enumeration e = columns.elements(); e.hasMoreElements();){
      //String ColumnName = (String)e.nextElement();
      String ColumnName = names[i];

      if (isValidColumnForUpdateList(entity,ColumnName)){
        if (returnString==null){
          returnString = new StringBuffer("");
          returnString.append(ColumnName);
          returnString.append(questionmark);
        }
        else{
          returnString.append(',');
          returnString.append(ColumnName);
          returnString.append(questionmark);
        }
      }
    }

    return returnString.toString();
  }


  protected void createForeignKey(GenericEntity entity,String baseTableName,String columnName, String refrencingTableName,String referencingColumnName)throws Exception{
      String SQLCommand = "ALTER TABLE " + baseTableName + " ADD FOREIGN KEY (" + columnName + ") REFERENCES " + refrencingTableName + "(" + referencingColumnName + ")";
      executeUpdate(entity,SQLCommand);
  }

  protected String getCreatePrimaryKeyStatementBeginning(String tableName){
    return "alter table "+tableName+" add primary key (";
  }


  }
