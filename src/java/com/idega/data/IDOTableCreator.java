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
import javax.transaction.TransactionManager;
import com.idega.transaction.IdegaTransactionManager;
import com.idega.util.ThreadContext;


/**
 * Title:        idega Data Objects
 * Description:  Idega Data Objects is a Framework for Object/Relational mapping and seamless integration between datastores
 * Copyright:    Copyright (c) 2001
 * Company:      idega
 * @author        <a href="mailto:tryggvi@idega.is">Tryggvi Larusson</a>
 * @version 1.0
 */

public class IDOTableCreator{

  private static String recordCreationKey="datastoreinterface_entity_record_creation";
  private DatastoreInterface _dsi;

  protected IDOTableCreator(DatastoreInterface dsi){
    this._dsi=dsi;
  }

  protected void executeQuery(GenericEntity entity,String SQLCode)throws Exception{
    _dsi.executeQuery(entity,SQLCode);
  }

  protected int executeUpdate(GenericEntity entity,String SQLCode)throws Exception{

    return _dsi.executeUpdate(entity,SQLCode);
  }

  /**
   * Returns a List whichs elements are the Classes that the EntityAutoCreator is still creating
   */
  protected List getCreationList(){
      List alreadyInCreation=(List)ThreadContext.getInstance().getAttribute(recordCreationKey);
      if(alreadyInCreation==null){
        alreadyInCreation=new Vector();
        ThreadContext.getInstance().setAttribute(recordCreationKey,alreadyInCreation);
      }
      return alreadyInCreation;
  }

  protected void registerEndOfCreatingEntity(GenericEntity entity){
      List alreadyInCreation=(List)ThreadContext.getInstance().getAttribute(recordCreationKey);
      if(alreadyInCreation!=null){
        alreadyInCreation.remove(entity.getClass());
        if(alreadyInCreation.isEmpty()){
          ThreadContext.getInstance().removeAttribute(recordCreationKey);
        }
      }
  }

  protected void registerStartOfCreatingEntity(GenericEntity entity){
      //Code block to prevent circular recursiveness
      //i.e. that it infinately recurses through the same entity when it is circularly referenced
      List alreadyInCreation=this.getCreationList();
      if(!hasAlreadyStartedCreatingEntity(entity)){
        alreadyInCreation.add(entity.getClass());
        //createRefrencedTables(entity);
      }
  }

  protected boolean hasAlreadyStartedCreatingEntity(GenericEntity entity){
    List alreadyInCreation=this.getCreationList();
    return alreadyInCreation.contains(entity.getClass());
  }

  protected boolean startEntityCreationTransaction(GenericEntity entity,boolean isPermittedToCommit){
    TransactionManager trans=null;
    boolean canCommit=isPermittedToCommit;
    try{
      if(_dsi.useTransactionsInEntityCreation){
        trans = com.idega.transaction.IdegaTransactionManager.getInstance();
        if(!((IdegaTransactionManager)trans).hasCurrentThreadBoundTransaction()){
          _dsi.executeBeforeCreateEntityRecord(entity);
          trans.begin();
          canCommit=true;
        }
        else{
          canCommit=false;
        }
      }
      registerStartOfCreatingEntity(entity);
    }
    catch(Exception e){
      e.printStackTrace();
    }
    return canCommit;
  }


  protected void endEntityCreationTransaction(GenericEntity entity,boolean isPermittedToCommit,boolean transactionSuccessful){
      boolean canCommit = isPermittedToCommit;
      try{
        TransactionManager trans = com.idega.transaction.IdegaTransactionManager.getInstance();
        if(_dsi.useTransactionsInEntityCreation){
          if(canCommit){
            if(transactionSuccessful){
              //System.out.println("\t\t\tCommitting!!!!");
              trans.commit();
              registerEndOfCreatingEntity(entity);
            }
            else{
              //System.out.println("\t\t\tRollbacking!!!!");
              trans.rollback();
              registerEndOfCreatingEntity(entity);
            }
            ThreadContext.getInstance().removeAttribute(recordCreationKey);
            _dsi.executeAfterCreateEntityRecord(entity);
            //ThreadContext.getInstance().releaseThread(Thread.currentThread());
          }
          else{
            if(transactionSuccessful){
              //System.out.println("\t\t\tNot permitted to commit!!");
            }
            else{
              trans.setRollbackOnly();
              //System.out.println("\t\t\tNot permitted to Rollback!!");
            }
          }
        }
      }
      catch(Exception e){
        e.printStackTrace();
      }
  }


  protected boolean doesTableExist(GenericEntity entity,String tableName){
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
        //se.printStackTrace();
      //}
    }

    return theReturner;
  }

  /**
   * Creates an entity record (table) that represents the entity in the datastore
   */
  public void createEntityRecord(GenericEntity entity)throws Exception{
    if(!doesTableExist(entity,entity.getTableName())){
    System.out.println("[idoTableCreator]  Creating "+entity.getClass().getName()+" - tablename: "+entity.getTableName());

      boolean canCommit=false;
      canCommit = this.startEntityCreationTransaction(entity,canCommit);

      try{

        //Create the records of all referenced entities (which this entity has dependent relationships on)
        if(!this.hasAlreadyStartedCreatingEntity(entity)){
          createRefrencedTables(entity);
        }

        //Check again if table exists because it could be created through createRefrencedTables(entity)
        if(!this.doesTableExist(entity,entity.getTableName())){
          createTable(entity);
          createTrigger(entity);
          //try{
            createForeignKeys(entity);
          //}
          //catch(Exception e){
            //e.printStackTrace();
            //System.err.println("Exception in creating Foreign Keys for: "+entity.getClass().getName());
            //System.err.println("  Error was: "+e.getMessage());
          //}
          createMiddleTables(entity);
          if(entity.getIfInsertStartData()){
            entity.insertStartData();
          }
        }

        this.endEntityCreationTransaction(entity,canCommit,true);
      }
      catch(Exception ex){
        System.err.println("===");
        System.err.println("Exception and rollback for: "+entity.getClass().getName());
        System.err.println("\tMessage: "+ex.getMessage());
        System.err.println("===");
        //ex.printStackTrace();
        /**@todo fix this Tryggvi so that we can use it!**/
        //this.endEntityCreationTransaction(entity,canCommit,false);
        //tmp
        this.endEntityCreationTransaction(entity,canCommit,true);
      }
    }
    else{
      System.out.println("[idoTableCreator]  Synchronizing  "+entity.getClass().getName()+" - tablename: "+entity.getTableName());

      boolean canCommit = false;
      canCommit = this.startEntityCreationTransaction(entity,canCommit);
      updateColumns(entity);
      createMiddleTables(entity);
      this.endEntityCreationTransaction(entity,canCommit,true);
    }//End if(!doesTableExist())
  }


  protected String getCreationStatement(GenericEntity entity){
		String returnString = "CREATE TABLE "+entity.getTableName()+"(";
		String[] names = entity.getColumnNames();
		for (int i = 0; i < names.length; i++){
                    String columnName = names[i];
                    returnString = 	returnString + getColumnSQLDefinition(columnName,entity);

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

  protected void createRefrencedTables(GenericEntity entity)throws Exception{
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

  protected void createMiddleTables(GenericEntity entity)throws Exception{

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


        boolean doCreateMiddleTable = !doesTableExist(entity,tableName);

        if(doCreateMiddleTable){
             Set tempSet = relMap.keySet();
             Iterator tempIter = tempSet.iterator();
            while (tempIter.hasNext() && doCreateMiddleTable) {
              String column = (String)tempIter.next();
              Class relClass = (Class)relMap.get(column);
              GenericEntity entity1 = (GenericEntity)relClass.newInstance();
              String referencingTableName = entity1.getTableName();
              doCreateMiddleTable = doesTableExist(entity,referencingTableName);
            }
        }


        //try{
          if(doCreateMiddleTable){
            String creationStatement = "CREATE TABLE ";
            creationStatement += tableName;
            creationStatement += "(";

            String primaryKeyStatement = _dsi.getCreatePrimaryKeyStatementBeginning(tableName);

            Set set;
            Iterator iter;

            set = relMap.keySet();
            iter = set.iterator();
            boolean mayAddComma = false;
            while (iter.hasNext()) {
              if(mayAddComma){
                creationStatement += ",";
                primaryKeyStatement += ",";
              }
              String column = (String)iter.next();
              creationStatement += column + " INTEGER NOT NULL";
              primaryKeyStatement +=column;
              mayAddComma = true;
            }
            creationStatement += ")";
            primaryKeyStatement +=")";
            executeUpdate(entity,creationStatement);
            executeUpdate(entity,primaryKeyStatement);


             set = relMap.keySet();
             iter = set.iterator();
            while (iter.hasNext()) {
              String column = (String)iter.next();
              Class relClass = (Class)relMap.get(column);
              //try{
                GenericEntity entity1 = (GenericEntity)relClass.newInstance();
                //createEntityRecord(entity1);
                createForeignKey(entity,tableName,column,entity1.getTableName(),entity1.getIDColumnName());
              //}
              //catch(Exception e){
              //  e.printStackTrace();
              //}
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
        //}
        //catch(Exception ex){
          //System.err.println("Failed creating middle-table: "+tableName);
          //ex.printStackTrace();
        //}


        //}
        //catch(Exception ex){
        //  System.err.println("Failed creating middle-table: "+tableName);
        //  ex.printStackTrace();
        //}
      }
    }

  }


  protected void createForeignKeys(GenericEntity entity) throws Exception {
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
            //String table1=entity.getTableName();
            GenericEntity entityToReference = (GenericEntity)relationShipClass.newInstance();
            //String tableToReference=entityToReference.getTableName();
            //if(!doesTableExist(entity,tableToReference)){
            //  createEntityRecord(entityToReference);
            //}

            //String columnInTableToReference=entityToReference.getIDColumnName();
            String columnName = names[i];
            createForeignKey(entity,entityToReference,columnName);
            //createForeignKey(entity,table1,columnName,tableToReference,columnInTableToReference);
          }
        //}
        //catch(Exception ex){
        //  ex.printStackTrace();
        //}
    }
  }


  protected void createTable(GenericEntity entity)throws Exception{
    //if(!doesTableExist(entity,entity.getTableName())){
      executeUpdate(entity,getCreationStatement(entity));
    //}
  }

  protected void createTrigger(GenericEntity entity)throws Exception{
    this._dsi.createTrigger(entity);
  }

  protected void createForeignKey(GenericEntity entity,String columnName)throws Exception{
      Class referencingClass = entity.getRelationShipClass(columnName);
      GenericEntity referencingEntity = (GenericEntity)referencingClass.newInstance();
      createForeignKey(entity,referencingEntity,columnName);
  }

  protected void createForeignKey(GenericEntity entity,GenericEntity entityToReference,String columnName)throws Exception{
      createForeignKey(entity,entity.getTableName(),columnName,entityToReference.getTableName(),entityToReference.getIDColumnName());
  }

  protected void createForeignKey(GenericEntity entity,String baseTableName,String columnName, String refrencingTableName)throws Exception{
      createForeignKey(entity,baseTableName,columnName,refrencingTableName,columnName);
  }

  /*protected void createForeignKey(GenericEntity entity,String baseTableName,String columnName, String refrencingTableName,String referencingColumnName)throws Exception{
      String SQLCommand = "ALTER TABLE " + baseTableName + " ADD CONSTRAINT FOREIGN KEY (" + columnName + ") REFERENCES " + refrencingTableName + "(" + referencingColumnName + ")";
      executeUpdate(entity,SQLCommand);
  }*/

  protected void createForeignKey(GenericEntity entity,String baseTableName,String columnName, String refrencingTableName,String referencingColumnName)throws Exception{
      //String SQLCommand = "ALTER TABLE " + baseTableName + " ADD CONSTRAINT FOREIGN KEY (" + columnName + ") REFERENCES " + refrencingTableName + "(" + referencingColumnName + ")";
      //executeUpdate(entity,SQLCommand);
      _dsi.createForeignKey(entity,baseTableName,columnName,refrencingTableName,referencingColumnName);
  }

  protected void createPrimaryKey(GenericEntity entity,String baseTableName,String columnName, String refrencingTableName,String referencingColumnName)throws Exception{
      //String SQLCommand = "ALTER TABLE " + baseTableName + " ADD CONSTRAINT FOREIGN KEY (" + columnName + ") REFERENCES " + refrencingTableName + "(" + referencingColumnName + ")";
      //executeUpdate(entity,SQLCommand);
      _dsi.createForeignKey(entity,baseTableName,columnName,refrencingTableName,referencingColumnName);
  }

  protected void updateColumns(GenericEntity entity)throws Exception{
    String[] columnArrayFromDB = getColumnArrayFromMetaData(entity);
    String[] columnArrayFromEntity = entity.getColumnNames();
    for (int i = 0; i < columnArrayFromEntity.length; i++) {
      String column = columnArrayFromEntity[i];
      if(!hasEntityColumn(column,columnArrayFromDB)){
        try{
          addColumn(column,entity);
          if(doesColumnHaveRelationship(column,entity)){
              this.createForeignKey(entity,column);
          }
        }
        catch(Exception e){
          e.printStackTrace();
        }
      }
    }
  }

  private String[] getColumnArrayFromMetaData(GenericEntity entity){
    Connection conn = null;
    ResultSet rs = null;
    try{
      conn = entity.getConnection();
      Vector v = new Vector();
      String tableName = entity.getTableName();
      java.sql.DatabaseMetaData metadata = conn.getMetaData();
      rs = metadata.getColumns("","",tableName.toLowerCase(),"%");
      //System.out.println("Table: "+tableName+" has the following columns:");
      while (rs.next()) {
        String column = rs.getString("COLUMN_NAME");
        v.add(column);
        //System.out.println("\t\t"+column);
      }
      rs.close();
      if(v.isEmpty()){
        rs = metadata.getColumns("","",tableName.toUpperCase(),"%");
        //System.out.println("Table: "+tableName+" has the following columns:");
        while (rs.next()) {
          String column = rs.getString("COLUMN_NAME");
          v.add(column);
          //System.out.println("\t\t"+column);
        }
        rs.close();
      }
      return (String[])v.toArray(new String[0]);
    }
    catch(SQLException e){
      e.printStackTrace();
    }
    finally{
      if(conn!=null){
        entity.freeConnection(conn);
      }
    }
    return null;
  }


  private boolean hasEntityColumn(String columnName,String[] columnsFromDB){
    String currentColumn = null;
    if(columnsFromDB!=null){
      for (int i = 0; i < columnsFromDB.length; i++) {
        currentColumn = columnsFromDB[i];
        if(currentColumn.equalsIgnoreCase(columnName)){
          return true;
        }
      }
    }
    return false;
  }

  private void addColumn(String columnName,GenericEntity entity)throws Exception{
    String SQLString = "alter table "+entity.getTableName()+" add "+getColumnSQLDefinition(columnName,entity);
    executeUpdate(entity,SQLString);
  }

  protected String getColumnSQLDefinition(String columnName,GenericEntity entity){
    boolean isPrimaryKey = entity.isPrimaryKey(columnName);

    String type;
    if(isPrimaryKey && entity.getStorageClassType(columnName)==EntityAttribute.TYPE_JAVA_LANG_INTEGER){
      type = _dsi.getIDColumnType();
    }
    else{
      type = _dsi.getSQLType(entity.getStorageClassName(columnName),entity.getMaxLength(columnName));
    }

    String returnString = columnName+" "+type;

    if (!entity.getIfNullable(columnName)){
      returnString = 	returnString + " NOT NULL";
    }
    if (isPrimaryKey){
      returnString = 	returnString + " PRIMARY KEY";
    }
    if (entity.getIfUnique(columnName)){
      returnString = 	returnString + " UNIQUE";
    }
    return returnString;
  }

  private boolean doesColumnHaveRelationship(String columnName,GenericEntity entity){
    return (entity.getRelationShipClass(columnName)!=null);
  }



}