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
import javax.transaction.Transaction;
import javax.transaction.TransactionManager;
import com.idega.transaction.IdegaTransactionManager;
import javax.transaction.NotSupportedException;
import com.idega.util.ThreadContext;

/**
*@author <a href="mailto:tryggvi@idega.is">Tryggvi Larusson</a>
*@version 1.3
*/
public abstract class DatastoreInterface{

  private static Hashtable interfacesHashtable;
  private static String recordCreationKey="datastoreinterface_entity_record_creation";

  private final static int STATEMENT_INSERT=1;
  private final static int STATEMENT_UPDATE=2;

  protected boolean useTransactionsInEntityCreation=true;

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
        className = "unimplemented DatastoreInterface";
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


  public void createTable(GenericEntity entity)throws Exception{
    //if(!doesTableExist(entity,entity.getTableName())){
      executeUpdate(entity,getCreationStatement(entity));
    //}
  }


  public boolean doesTableExist(GenericEntity entity,String tableName){
    boolean theReturner=true;
    try{
        executeQuery(entity,"select * from "+tableName);
    }
    catch(Exception se){
      //String message = se.getMessage();
      //if(message.toLowerCase().indexOf("table")!=-1){
        theReturner=false;
      //}
      //else{
      //  se.printStackTrace();
      //}
    }

    return theReturner;
  }

  public void executeBeforeCreateEntityRecord(GenericEntity entity)throws Exception{
  }

  public void executeAfterCreateEntityRecord(GenericEntity entity)throws Exception{
  }

  public void createEntityRecord(GenericEntity entity)throws Exception{
      //System.out.println("Trying to create record for "+entity.getClass().getName()+" tablename: "+entity.getTableName());

      if(!doesTableExist(entity,entity.getTableName())){

          TransactionManager trans=null;
          boolean canCommit=false;
            if(useTransactionsInEntityCreation){
              trans = com.idega.transaction.IdegaTransactionManager.getInstance();
              if(!((IdegaTransactionManager)trans).hasCurrentThreadBoundTransaction()){
                executeBeforeCreateEntityRecord(entity);
                trans.begin();
                canCommit=true;
              }
              else{
                canCommit=false;
              }
            }

            try{
                List alreadyInCreation=(List)ThreadContext.getInstance().getAttribute(recordCreationKey);
                if(alreadyInCreation==null){
                  alreadyInCreation=new Vector();
                  ThreadContext.getInstance().setAttribute(recordCreationKey,alreadyInCreation);
                }


                if(alreadyInCreation.contains(entity.getClass().getName())){
                  //try{
                   if(!this.doesTableExist(entity,entity.getTableName())){
                      createTable(entity);
                      createTrigger(entity);
                      try{
                        createForeignKeys(entity);
                      }
                      catch(Exception e){
                        //e.printStackTrace();
                        System.err.println("Exception in creating Foreign Keys for: "+entity.getClass().getName());
                        System.err.println("  Error was: "+e.getMessage());
                      }
                      createMiddleTables(entity);
                      entity.insertStartData();
                  }
                  //}
                  //catch(Exception ex){

                  //}
                }
                else{
                  alreadyInCreation.add(entity.getClass().getName());
                  createRefrencedTables(entity);
                  if(!this.doesTableExist(entity,entity.getTableName())){
                    createTable(entity);
                    createTrigger(entity);
                    try{
                      createForeignKeys(entity);
                    }
                    catch(Exception e){
                        //e.printStackTrace();
                        System.err.println("Exception in creating Foreign Keys for: "+entity.getClass().getName());
                        System.err.println("  Error was: "+e.getMessage());
                    }
                    createMiddleTables(entity);
                    entity.insertStartData();
                  }
                }

            if(useTransactionsInEntityCreation){
              if(canCommit){
                trans.commit();
                ThreadContext.getInstance().removeAttribute(recordCreationKey);
                executeAfterCreateEntityRecord(entity);
                //ThreadContext.getInstance().releaseThread(Thread.currentThread());
              }
            }
        }
        catch(Exception ex){
          if(useTransactionsInEntityCreation){
            if(canCommit){
              trans.rollback();
              //ThreadContext.getInstance().releaseThread(Thread.currentThread());
              ThreadContext.getInstance().removeAttribute(recordCreationKey);

              System.out.println();
              System.err.println("Exception and rollback for: "+entity.getClass().getName());
              System.out.println();
              ex.printStackTrace();

              executeAfterCreateEntityRecord(entity);
            }
            else{
              throw (Exception)ex.fillInStackTrace();
            }
          }
        }
      }
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
                    if (entity.getIfUnique(names[i])){
                      returnString = 	returnString + " UNIQUE";
                    }
                    if (i!=names.length-1){
                      returnString = returnString+",";
                    }
		}
                returnString = returnString +")";
                //System.out.println(returnString);
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

  public void createRefrencedTables(GenericEntity entity)throws Exception{
      /*String[] names = entity.getColumnNames();
      for (int i = 0; i < names.length; i++) {
        String relationShipClass = entity.getRelationShipClassName(names[i]);
        if (!relationShipClass.equals("")) {
          try{
            GenericEntity relationShipEntity = (GenericEntity)Class.forName(relationShipClass).newInstance();
            createEntityRecord(relationShipEntity);
          }
          catch(Exception ex){
            ex.printStackTrace();
          }
        }
      }*/
      List list = getRelatedEntityClasses(entity);
      Iterator iter = list.iterator();
      while (iter.hasNext()) {
        //String className = (String)iter.next();
        Class myClass = (Class)iter.next();
          //try{
            //GenericEntity relationShipEntity = (GenericEntity)Class.forName(className).newInstance();
            GenericEntity relationShipEntity = (GenericEntity)myClass.newInstance();
            createEntityRecord(relationShipEntity);
          //}
          //catch(Exception ex){
          //  ex.printStackTrace();
          //}
      }
  }

  /**
   * Gets the entities that are related by  one-to many and many-to-many relationships
   * Returns a List of Class Objects
   */
  private List getRelatedEntityClasses(GenericEntity entity){
      List returnNames = new Vector();
      String[] names = entity.getColumnNames();
      for (int i = 0; i < names.length; i++) {
        Class relationShipClass = entity.getRelationShipClass(names[i]);
        if ( relationShipClass!=null ) {
          try{
            returnNames.add(relationShipClass);
          }
          catch(Exception ex){
            ex.printStackTrace();
          }
        }
      }
      returnNames.addAll(getManyToManyRelatedEntityClasses(entity));
      return returnNames;
  }

  /**
   * Gets the entities that are related by many-to-many relationships
   * Returns a List of Class Objects
   */
  private List getManyToManyRelatedEntityClasses(GenericEntity entity){
      List list = new Vector();
      List classList = EntityControl.getManyToManyRelationShipClasses(entity);
      if(classList!=null){
        Iterator iter = classList.iterator();
        while (iter.hasNext()) {
          Class item = (Class)iter.next();
          //String className = item.getName();
          //list.add(className);
          list.add(item);
        }
      }
      return list;
  }

  public void createMiddleTables(GenericEntity entity)throws Exception{

    //List classList = EntityControl.getManyToManyRelationShipClasses(entity);
    List relationshipList = EntityControl.getManyToManyRelationShips(entity);

    /*
    if(classList==null){
      System.out.println("classList==null for "+entity.getClass().getName());
    }
    if(tableList==null){
      System.out.println("tableList==null for "+entity.getClass().getName());
    }*/

    if(relationshipList!=null){
      //System.out.println("inside 1 for "+entity.getClass().getName());
      //Iterator iter = classList.iterator();
      Iterator relIter = relationshipList.iterator();
      while (relIter.hasNext()) {
        //System.out.println("inside 2 for "+entity.getClass().getName());
        //Class item = (Class)iter.next();
        EntityRelationship relation = (EntityRelationship)relIter.next();
        Map relMap = relation.getColumnsAndReferencingClasses();
        String tableName = relation.getTableName();
        GenericEntity relatingEntity = null;
        try{
          if(!doesTableExist(entity,tableName)){
            String creationStatement = "CREATE TABLE ";
            creationStatement += tableName;
            creationStatement += "(";

            Set set;
            Iterator iter;

            set = relMap.keySet();
            iter = set.iterator();
            boolean mayAddComma = false;
            while (iter.hasNext()) {
              if(mayAddComma){
                creationStatement += ",";
              }
              String column = (String)iter.next();
              creationStatement += column + " INTEGER NOT NULL";
              mayAddComma = true;
            }
            creationStatement += ")";
            executeUpdate(entity,creationStatement);



             set = relMap.keySet();
             iter = set.iterator();
            while (iter.hasNext()) {
              String column = (String)iter.next();
              Class relClass = (Class)relMap.get(column);
              try{
                GenericEntity entity1 = (GenericEntity)relClass.newInstance();
                createEntityRecord(entity1);
                createForeignKey(entity,tableName,column,entity1.getTableName(),entity1.getIDColumnName());
              }
              catch(Exception e){
                e.printStackTrace();
              }
            }


        }




          /*
          relatingEntity = (GenericEntity)item.newInstance();
          if(!this.doesTableExist(entity,tableName)){
            String creationStatement = "CREATE TABLE "+tableName+" ( "+entity.getIDColumnName() + " INTEGER NOT NULL,"+relatingEntity.getIDColumnName() + " INTEGER NOT NULL , PRIMARY KEY("+entity.getIDColumnName() + "," + relatingEntity.getIDColumnName() +") )";
            executeUpdate(entity,creationStatement);
            createForeignKey(entity,tableName,entity.getIDColumnName(),entity.getTableName());
            createForeignKey(entity,tableName,relatingEntity.getIDColumnName(),relatingEntity.getTableName());
          }*/
        }
        catch(Exception ex){
          System.err.println("Failed creating middle-table: "+tableName);
          ex.printStackTrace();
        }


        //}
        //catch(Exception ex){
        //  System.err.println("Failed creating middle-table: "+tableName);
        //  ex.printStackTrace();
        //}
      }
    }

  }

  public abstract void createTrigger(GenericEntity entity)throws Exception;

  //public abstract void createForeignKeys(GenericEntity entity)throws Exception;

  public void createForeignKeys(GenericEntity entity) throws Exception {
    /*Connection conn = null;
    Statement Stmt = null;
    try {
      conn = entity.getConnection();
      conn.commit();

      String[] names = entity.getColumnNames();
      for (int i = 0; i < names.length; i++) {
        if (!entity.getRelationShipClassName(names[i]).equals("")) {
          Stmt = conn.createStatement();
          int n = Stmt.executeUpdate("ALTER TABLE " + entity.getTableName() + " ADD FOREIGN KEY (" + names[i] + ") REFERENCES " + ((GenericEntity)Class.forName(entity.getRelationShipClassName(names[i])).newInstance()).getTableName() + " ");
          if (Stmt != null) {
            Stmt.close();
          }
        }
      }
    }
    finally {
      if (Stmt != null) {
        Stmt.close();
      }
      if (conn != null) {
        entity.freeConnection(conn);
      }
    }*/
    String[] names = entity.getColumnNames();
    for (int i = 0; i < names.length; i++) {
        //try{
          Class relationShipClass = entity.getRelationShipClass(names[i]);
          if (relationShipClass!=null) {
            String table1=entity.getTableName();
            GenericEntity entityToReference = (GenericEntity)relationShipClass.newInstance();
            String tableToReference=entityToReference.getTableName();
            if(!doesTableExist(entity,tableToReference)){
              createEntityRecord(entityToReference);
            }
            String columnInTableToReference=entityToReference.getIDColumnName();
            String columnName = names[i];
            createForeignKey(entity,table1,columnName,tableToReference,columnInTableToReference);
          }
        //}
        //catch(Exception ex){
        //  ex.printStackTrace();
        //}
    }
  }

  protected void createForeignKey(GenericEntity entity,String baseTableName,String columnName, String refrencingTableName)throws Exception{
      createForeignKey(entity,baseTableName,columnName,refrencingTableName,columnName);
  }

  protected void createForeignKey(GenericEntity entity,String baseTableName,String columnName, String refrencingTableName,String referencingColumnName)throws Exception{
      String SQLCommand = "ALTER TABLE " + baseTableName + " ADD FOREIGN KEY (" + columnName + ") REFERENCES " + refrencingTableName + "(" + referencingColumnName + ")";
      executeUpdate(entity,SQLCommand);
  }

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
                  String statement = "insert into "+entity.getTableName()+"("+entity.getCommaDelimitedColumnNames()+") values ("+entity.getQuestionmarksForColumns()+")";
                  //System.out.println(statement);
                  Stmt = conn.prepareStatement (statement);
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
			//String datastoreType=DatastoreInterface.getDataStoreType(conn);

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

    if( entity.hasMetaDataRelationship() ) crunchMetaData(entity);
  }

  protected void executeBeforeUpdate(GenericEntity entity)throws Exception{
  }

  protected void executeAfterUpdate(GenericEntity entity)throws Exception{
    //if( entity.hasLobColumn() ) insertBlob(entity); copied from insert not sure if used
    if( entity.hasMetaDataRelationship() ) crunchMetaData(entity);
  }

  protected void executeBeforeDelete(GenericEntity entity)throws Exception{

  }


  protected void executeAfterDelete(GenericEntity entity)throws Exception{
  }

  protected void crunchMetaData(GenericEntity entity)throws SQLException{
            System.out.println("CRUNCH!");
    if( entity.metaDataHasChanged() ){//else do nothing
      System.out.println("CRUNCHING");
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
      else       System.out.println("insert is null");

      if( update!= null ){
        length = update.size();
        System.out.println("update size: "+length);
        for (int i = 0; i < length; i++) {
          System.out.println("updating: "+i);
          data = new MetaData();//do not construct with id to avoid database access
          if(ids==null) System.out.println("ids is null");
          data.setID((Integer) ids.get(update.elementAt(i)));
          System.out.println("ID: "+data.getID());
          data.setMetaDataNameAndValue((String) update.elementAt(i), (String) metadata.get((String) update.elementAt(i)));
          updater.add(data,EntityBulkUpdater.update);
        }
      }
      else       System.out.println("update is null");

      if( delete!= null ){
        length = delete.size();
        for (int i = 0; i < length; i++) {
          data = new MetaData();
          data.setID((Integer) ids.get(delete.elementAt(i)));
          updater.add(data,EntityBulkUpdater.delete);
        }
      }
      else       System.out.println("delete is null");

      updater.execute();


    }

  }


  protected void insertBlob(GenericEntity entity)throws Exception{

    String statement ;
    Connection Conn = null;

    try{

      statement = "update " + entity.getTableName() + " set " + entity.getLobColumnName() + "=? where " + entity.getIDColumnName() + " = '" + entity.getID()+"'";
      System.out.println(statement);
      System.out.println("In insertBlob() in DatastoreInterface");
      BlobWrapper wrapper = entity.getBlobColumnValue(entity.getLobColumnName());
      if(wrapper!=null){
        System.out.println("In insertBlob() in DatastoreInterface wrapper!=null");
        //Conn.setAutoCommit(false);
        InputStream instream = wrapper.getInputStreamForBlobWrite();
        if(instream!=null){
          System.out.println("In insertBlob() in DatastoreInterface instream != null");
          Conn = entity.getConnection();
          if(Conn== null){ System.out.println("In insertBlob() in DatastoreInterface conn==null"); return;}
          BufferedInputStream bin = new BufferedInputStream(instream);
          PreparedStatement PS = Conn.prepareStatement(statement);
          System.out.println("bin.available(): "+bin.available());
          PS.setBinaryStream(1, bin, 0 );
          //PS.setBinaryStream(1, instream, instream.available() );
          PS.executeUpdate();
          PS.close();
          System.out.println("bin.available(): "+bin.available());
          instream.close();
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
                          if (entity.isValidColumnForUpdateList(names[i])){
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
                          if (entity.isValidColumnForInsertList(names[i])){
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
          if(wrapper!=null){
            InputStream stream = wrapper.getInputStreamForBlobWrite();
            if(stream!=null){
              try{
                //BufferedInputStream bin = new BufferedInputStream( stream );
                //statement.setBinaryStream(index, bin, bin.available() );
                statement.setBinaryStream(index, stream, stream.available() );
              }
              catch(Exception e){
                System.err.println("Error updating BLOB field in "+entity.getClass().getName());
                e.printStackTrace();
              }
            }
          }

        }

	public void update(GenericEntity entity)throws Exception{
          executeBeforeUpdate(entity);
		Connection conn= null;
		PreparedStatement Stmt= null;
		try{
			conn = entity.getConnection();
//			Stmt = conn.createStatement();

                                String statement = "update "+entity.getTableName()+" set "+entity.getAllColumnsAndQuestionMarks()+" where "+entity.getIDColumnName()+"="+entity.getID();
                                //System.out.println(statement);
		                Stmt = conn.prepareStatement (statement);
                                setForPreparedStatement(STATEMENT_UPDATE,Stmt,entity);
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

                executeAfterUpdate(entity);

                entity.setEntityState(entity.STATE_IN_SYNCH_WITH_DATASTORE);

	}

	public void update(GenericEntity entity, Connection conn)throws Exception{
          executeBeforeUpdate(entity);
		PreparedStatement Stmt = null;
		try {
                  String statement = "update "+entity.getTableName()+" set "+entity.getAllColumnsAndQuestionMarks()+" where "+entity.getIDColumnName()+"="+entity.getID();
                  Stmt = conn.prepareStatement (statement);
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
        String statement = "insert into "+entity.getTableName()+"("+entity.getCommaDelimitedColumnNames()+") values ("+entity.getQuestionmarksForColumns()+")";
        //System.out.println(statement);
        Stmt = conn.prepareStatement (statement);
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
              int i = Stmt.executeUpdate("delete from "+entity.getEntityName()+" where "+entity.getIDColumnName()+"="+entity.getID());

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
      String statement = "delete from  "+entity.getTableName()+" where "+entity.getIDColumnName()+"="+entity.getID();
      Stmt.executeUpdate(statement);
    }
    finally{
            if(Stmt != null){
                    Stmt.close();
            }
    }
    executeAfterInsert(entity);
    entity.setEntityState(entity.STATE_DELETED);
    }
  }
