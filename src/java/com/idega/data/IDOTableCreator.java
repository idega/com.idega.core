package com.idega.data;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.transaction.TransactionManager;

import com.idega.idegaweb.IWMainApplicationSettings;
import com.idega.transaction.IdegaTransactionManager;
import com.idega.util.ThreadContext;
import com.idega.util.logging.LoggingHelper;


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
  private List _entityWithStartData = new Stack();

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
          ((IdegaTransactionManager)trans).setEntity(entity);
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
    	long start = System.currentTimeMillis();
    	/**
    	 * @todo: Change to doTableCheckDatabaseMetadata()
    	 **/
    	//doTableCheckSelectStar(entity,tableName);
    	doTableCheckDatastoreInterface(entity,tableName);
        //doTableCheckDatabaseMetadata(entity,tableName);
        long end = System.currentTimeMillis();
		debug("doesTableExist() check took "+((end-start))+" milliseconds"+"  ("+tableName+")");
      
    }
    catch(Exception se){
      //String message = se.getMessage();
      //if(message.toLowerCase().indexOf("table")!=-1){
        theReturner=false;
        //System.out.println("Table: "+tableName+" does not exist, exception:"+se.getClass().getName());
      //}
      //else{
        //se.printStackTrace();
      //}
    }
    return theReturner;
  }
  
  
  private void doTableCheckSelectStar(GenericEntity entity,String tableName)throws Exception{
    	String checkQuery = "select * from "+tableName;
        executeQuery(entity,checkQuery);  	
  }
  
  private void doTableCheckDatastoreInterface(GenericEntity entity,String tableName)throws Exception{
	  if(!_dsi.doesTableExist(entity.getDatasource(),tableName))
	  	throw new Exception("Table "+tableName+"does not exists");
  }


	
	private void doTableCheckDatabaseMetadata(GenericEntity entity,String tableName)throws Exception{
		/**
		 * @todo: Finish implementation
		 **/
	    Connection conn = null;
	    ResultSet rs = null;
	    boolean tableExists = false;
	    try{
	      conn = entity.getConnection();
	      List v = new ArrayList();
	      java.sql.DatabaseMetaData metadata = conn.getMetaData();
	      rs = metadata.getTables("","",tableName.toLowerCase(),null);
	      //System.out.println("Table: "+tableName+" has the following columns:");
	      while (rs.next()) {
	        String table = rs.getString("TABLE_NAME");
	        v.add(table);
	        tableExists=true;
	        //System.out.println("\t\t"+column);
	      }
	      rs.close();
	      if(v.isEmpty()){
	        rs = metadata.getTables("","",tableName.toUpperCase(),null);
	        //System.out.println("Table: "+tableName+" has the following columns:");
	        while (rs.next()) {
		        String table = rs.getString("TABLE_NAME");
		        v.add(table);
		        tableExists=true;
	        }
	        rs.close();
	      }
	     
	    }
	    finally{
	    	if(conn!=null){
	    		entity.freeConnection(conn);
	    	}
	    }
	    if(tableExists==false){
	    	throw new Exception("Table "+tableName+" does not exist");	
	    }
	}

  /**
   * Creates an entity record (table) that represents the entity in the datastore
   */
  public void createEntityRecord(GenericEntity entity)throws Exception{
  	
    if(!doesTableExist(entity,entity.getTableName())){
		//if(this.isDebugActive())
    	debug("Creating "+entity.getClass().getName()+" - tablename: "+entity.getTableName());

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
          try{
            createForeignKeys(entity);
          }
          catch(Exception e){
           e.printStackTrace();
            //System.err.println("Exception in creating Foreign Keys for: "+entity.getClass().getName());
            //System.err.println("  Error was: "+e.getMessage());
          }
          createIndexes(entity);
					createMiddleTables(entity);
          if(entity.getIfInsertStartData()){
	      		_entityWithStartData.add(entity);
          }
        }
        
        
      	this.endEntityCreationTransaction(entity,canCommit,true);

      	try {
	      	if (canCommit || !_dsi.useTransactionsInEntityCreation && !_entityWithStartData.isEmpty()) {
	      		Iterator iter = _entityWithStartData.iterator();
	      		while (iter.hasNext()) {
	        		GenericEntity tmpEnt = (GenericEntity) iter.next();
		      		System.out.println("IDOTableCreator : Inserting start data for entity : "+tmpEnt.getEntityName());
	      			((GenericEntity) tmpEnt).insertStartData();
	      		}
	      		_entityWithStartData = new Stack();
	      	}
        } catch (Exception e) {
        	System.out.println("===========================================");
        	System.out.println("============"+e.getMessage()+"=============");
        	if (e.getMessage() == null) {
        		e.printStackTrace();
        	}
        	System.out.println("===========================================");
        }

      }
      catch(Exception ex){
		//if(this.isDebugActive()){
        	System.err.println("===");
        	System.err.println("Exception and rollback for: "+entity.getClass().getName());
        	System.err.println("\tMessage: "+ex.getMessage());
        	ex.printStackTrace();
        	System.err.println("===");
		//}
        //ex.printStackTrace();
        /**@todo fix this Tryggvi so that we can use it!**/
        this.endEntityCreationTransaction(entity,canCommit,false);

        //tmp fix
        //this.endEntityCreationTransaction(entity,canCommit,true);
      }
    }
    else{
    		debug("Synchronizing  "+entity.getClass().getName()+" - tablename: "+entity.getTableName());

      boolean canCommit = false;
      canCommit = this.startEntityCreationTransaction(entity,canCommit);
      updateColumns(entity);
      updateIndexes(entity);
      createMiddleTables(entity);
      this.endEntityCreationTransaction(entity,canCommit,true);
    }//End if(!doesTableExist())
    
	
  }


  protected String getCreationStatement(GenericEntity entity){
  	IDOEntityField[] pkFields = entity.getEntityDefinition().getPrimaryKeyDefinition().getFields();
  	StringBuffer returnString = new StringBuffer("CREATE TABLE ").append(entity.getTableName()).append("(");
		String[] names = entity.getColumnNames();
		for (int i = 0; i < names.length; i++){
                    String columnName = names[i];
                    returnString.append(getColumnSQLDefinition(columnName,entity));

                    if (i!=names.length-1){
                      returnString.append(",");
                    } else if (pkFields != null && pkFields.length > 0) {
	                		returnString.append(", PRIMARY KEY (");
	                		for (int j = 0; j < pkFields.length; j++) {
	                			if (j != 0) {
	                				returnString.append(",");
	                			}
	                			returnString.append(pkFields[j].getSQLFieldName());
	                		}
	                		returnString.append(")");
                    }
		}
                returnString.append(")");
                //System.out.println(returnString);
		return returnString.toString();
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
			Stmt.executeUpdate("drop table "+entity.getTableName());
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

        boolean doCreateMiddleTable = !doesTableExist(entity,tableName);

        if(doCreateMiddleTable){
             Set tempSet = relMap.keySet();
             Iterator tempIter = tempSet.iterator();
            while (tempIter.hasNext() && doCreateMiddleTable) {
              String column = (String)tempIter.next();
              Class relClass = (Class)relMap.get(column);
              //GenericEntity entity1 = (GenericEntity)relClass.newInstance();
              GenericEntity entity1 = (GenericEntity)IDOContainer.getInstance().instanciateBean(relClass);
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
              Class relClass = (Class)relMap.get(column);
              //GenericEntity entity1 = (GenericEntity)relClass.newInstance();
              GenericEntity entity1 = (GenericEntity)IDOContainer.getInstance().instanciateBean(relClass);

              //creationStatement += column + " INTEGER NOT NULL";
              creationStatement += column+this.getPrimaryKeyReferenceForManyToManyRelationship(entity1,column,entity1.getIDColumnName());
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
                GenericEntity entity1 = (GenericEntity)IDOLookup.getBeanClassFor(relClass).newInstance();
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

  protected void createIndexes(GenericEntity entity) throws Exception {
	  	try {
	  		HashMap map = entity.getEntityDefinition().getIndexes();
	  		Set keys = map.keySet();
	  		if (keys != null) {
	  			Iterator iter = keys.iterator();
	  			String key;
	  			String[] values;
	  			while (iter.hasNext()) {
	  				try {
		  				key = (String) iter.next();
		  				values = (String[]) map.get(key);
		  				createIndex(entity, key, values);
	  				} catch (Exception e) {
	  					e.printStackTrace();
	  				}
	  			}
	  		}
	  	} catch (NoIndexException ignore) {}
  }
  
  private void dropIndex(GenericEntity entity, String name) throws Exception {
  		String sql = "DROP INDEX "+entity.getTableName()+"."+name;
  		executeUpdate(entity, sql);
  }
  
  private void createIndex(GenericEntity entity, String name, String[] fields) throws Exception {
		if (_dsi.useIndexes()) {
	  		StringBuffer sql = new StringBuffer("CREATE INDEX ")
			.append(name).append(" ON ").append(entity.getTableName()).append(" (");
	  		for (int i = 0; i < fields.length; i++) {
	  			if (i > 0) {
	  				sql.append(", ");
	  			}
	  			sql.append(fields[i]);
	  		}
	  		sql.append(")");
	  		executeUpdate(entity, sql.toString());
		}
  }

  protected void createForeignKeys(IDOEntity entity) throws Exception {
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
    //String[] names = entity.getColumnNames();
    IDOEntityField[] fields = entity.getEntityDefinition().getFields();
    //for (int i = 0; i < names.length; i++) {
	for (int i = 0; i < fields.length; i++) {
        //try{
          //Class relationShipClass = entity.getRelationShipClass(names[i]);
          //if (relationShipClass!=null) {
          if(fields[i].isPartOfManyToOneRelationship()){
            //String table1=entity.getTableName();

            //Class intefaceClass = IDOLookup.getInterfaceClassFor(relationShipClass);
			Class intefaceClass = fields[i].getManyToOneRelated().getInterfaceClass();
            GenericEntity entityToReference = (GenericEntity)IDOLookup.instanciateEntity(intefaceClass);
            //GenericEntity entityToReference = (GenericEntity)relationShipClass.newInstance();
            //String tableToReference=entityToReference.getTableName();
            //if(!doesTableExist(entity,tableToReference)){
            //  createEntityRecord(entityToReference);
            //}

            //String columnInTableToReference=entityToReference.getIDColumnName();
            //String columnName = names[i];
			String columnName = fields[i].getSQLFieldName();
			try{
				createForeignKey((GenericEntity)entity,entityToReference,columnName);
			}
			catch(Exception e){
				logError("Error Creating foreign key for entity "+entity.getEntityDefinition().getSQLTableName()+" and field "+columnName+". Error message was : "+e.getMessage());
			}
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
      createForeignKey(entity,entity.getEntityDefinition().getSQLTableName(),columnName,entityToReference.getEntityDefinition().getSQLTableName(),entityToReference.getIDColumnName());
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
    String[] columnArrayFromDB = getColumnArrayFromDataBase(entity);
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
          //e.printStackTrace();
        }
      }
    }
  }

  private void updateIndexes(GenericEntity entity) {
  		if (_dsi.useIndexes()) {
			Collection indexesFromDB = _dsi.getTableIndexes(entity.getDatasource(), entity.getTableName());
	  		try {
					HashMap map = entity.getEntityDefinition().getIndexes();
					Set indexesFromEntity = map.keySet();
					Vector temp = new Vector(indexesFromEntity);
					
					if (indexesFromDB != null) {
						indexesFromEntity.removeAll(indexesFromDB);
						indexesFromDB.removeAll(temp);
					}
	
					// CREATING
					Iterator iter = indexesFromEntity.iterator();
					String indexName;
					while (iter.hasNext()) {
						indexName = (String) iter.next();
						try {
							this.createIndex(entity, indexName, (String[]) map.get(indexName));
						}
						catch (Exception e1) {
							System.out.println("IDOTableCreator : failed to create index : "+indexName+" ("+e1.getMessage()+")");
						}
					}
					// REMOVING - Not active since it tries to removed the PRIMARY_KEY index....
					/*iter = indexesFromDB.iterator();
					while (iter.hasNext()) {
						indexName = (String) iter.next();
						try {
							dropIndex(entity, indexName);
						} catch (Exception e1) {
							System.out.println("IDOTableCreator : failed to drop index : "+indexName);
						}
					}*/
					
				}
				catch (NoIndexException e) {
					// REMOVING ALL INDEXES - Not active since it tries to removed the PRIMARY_KEY index....
					/*
					if (indexesFromDB != null) {
						Iterator iter = indexesFromDB.iterator();
						String indexName;
						while (iter.hasNext()) {
							indexName = (String) iter.next();
							try {
								dropIndex(entity, indexName);
							} catch (Exception e1) {
								System.out.println("IDOTableCreator : failed to drop index : "+indexName);
							}
						}
					}*/
				}
  		}

  }

  
  private String[] getColumnArrayFromDataBase(GenericEntity entity){
  	return _dsi.getTableColumnNames(entity.getDatasource(),entity.getTableName());
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
    boolean isCompositePK = entity.getEntityDefinition().getPrimaryKeyDefinition().isComposite();

    String type;
    
    if(isPrimaryKey && !isCompositePK &&entity.getStorageClassType(columnName)==EntityAttribute.TYPE_JAVA_LANG_INTEGER){
      type = _dsi.getIDColumnType();
    }
    else{
      type = _dsi.getSQLType(entity.getStorageClassName(columnName),entity.getMaxLength(columnName));
    }

    String returnString = columnName+" "+type;

    if (!entity.getIfNullable(columnName)){
      returnString = 	returnString + " NOT NULL";
    }
    /* DOES NOT WORK WITH COMPOSITE PKS, MOVED TO getCreationStatement(entity)
    if (isPrimaryKey) {
      returnString = 	returnString + " PRIMARY KEY";
    }*/
    if (entity.getIfUnique(columnName)){
      returnString = 	returnString + " UNIQUE";
    }
    return returnString;
  }

  private boolean doesColumnHaveRelationship(String columnName,GenericEntity entity){
    return (entity.getRelationShipClass(columnName)!=null);
  }

  protected String getPrimaryKeyReferenceForManyToManyRelationship(GenericEntity entity,String column,String referencingColumn){
    try{
      EntityAttribute attr = entity.getColumn(referencingColumn);
      Class storageClass = attr.getStorageClass();
      int maxLength = attr.getMaxLength();
      String sqlType = this._dsi.getSQLType(storageClass,maxLength);
      return " "+sqlType + " NOT NULL";
    }
    catch(NullPointerException ne){
      ne.printStackTrace();
      System.err.println("---");
      System.err.println("Nullpointer where entity="+entity.getClass().getName()+" and column="+column);
      System.err.println("---");
      return " INTEGER NOT NULL";
    }
  }
  
  //STANDARD LOGGING METHODS:
  
  /**
   * Logs out to the default log level (which is by default INFO)
   * @param msg The message to log out
   */
  protected void log(String msg) {
  	//System.out.println(string);
  	getLogger().log(getDefaultLogLevel(),msg);
  }

  /**
   * Logs out to the error log level (which is by default WARNING) to the default Logger
   * @param e The Exception to log out
   */
  protected void log(Exception e) {
  	LoggingHelper.logException(e,this,getLogger(),getErrorLogLevel());
  }
  
  /**
   * Logs out to the specified log level to the default Logger
   * @param level The log level
   * @param msg The message to log out
   */
  protected void log(Level level,String msg) {
  	//System.out.println(msg);
  	getLogger().log(level,msg);
  }
  
  /**
   * Logs out to the error log level (which is by default WARNING) to the default Logger
   * @param msg The message to log out
   */
  protected void logError(String msg) {
  	//System.err.println(msg);
  	getLogger().log(getErrorLogLevel(),msg);
  }

  /**
   * Logs out to the debug log level (which is by default FINER) to the default Logger
   * @param msg The message to log out
   */
  protected void logDebug(String msg) {
  	//System.err.println(msg);
  	getLogger().log(getDebugLogLevel(),msg);
  }
  
  /**
   * Logs out to the SEVERE log level to the default Logger
   * @param msg The message to log out
   */
  protected void logSevere(String msg) {
  	//System.err.println(msg);
  	getLogger().log(Level.SEVERE,msg);
  }	
  
  
  /**
   * Logs out to the WARNING log level to the default Logger
   * @param msg The message to log out
   */
  protected void logWarning(String msg) {
  	//System.err.println(msg);
  	getLogger().log(Level.WARNING,msg);
  }
  
  /**
   * Logs out to the CONFIG log level to the default Logger
   * @param msg The message to log out
   */
  protected void logConfig(String msg) {
  	//System.err.println(msg);
  	getLogger().log(Level.CONFIG,msg);
  }	
  
  /**
   * Logs out to the debug log level to the default Logger
   * @param msg The message to log out
   */
  protected void debug(String msg) {
  	String logMsg = "[idoTableCreator] : "+msg;
  	logDebug(logMsg);
  }	
  
  /**
   * Gets the default Logger. By default it uses the package and the class name to get the logger.<br>
   * This behaviour can be overridden in subclasses.
   * @return the default Logger
   */
  protected Logger getLogger(){
  	return Logger.getLogger(this.getClass().getName());
  }
  
  /**
   * Gets the log level which messages are sent to when no log level is given.
   * @return the Level
   */
  protected Level getDefaultLogLevel(){
  	return Level.INFO;
  }
  /**
   * Gets the log level which debug messages are sent to.
   * @return the Level
   */
  protected Level getDebugLogLevel(){
  	return Level.FINER;
  }
  /**
   * Gets the log level which error messages are sent to.
   * @return the Level
   */
  protected Level getErrorLogLevel(){
  	return Level.WARNING;
  }
  
  //ENTITY SPECIFIC LOG MEHTODS:
  
  ///**
  // * This method outputs the outputString to System.out if the Application property
  // * "debug" is set to "TRUE"
  // */
  //public void debug(String outputString) {
  //	if (isDebugActive()) {
  //		//System.out.println("[DEBUG] \"" + outputString + "\" : " + this.getEntityName());
  //	}
  //}
  /**
   * This method logs the sqlCommand if the Log Level is low enough 
   */
  public void logSQL(String sqlCommand) {
  	log(Level.FINEST,sqlCommand);
  	//if (isDebugActive()) {
  	//System.out.println("[DEBUG] \"" + outputString + "\" : " + this.getEntityName());
  	//}
  }
  
  protected boolean isDebugActive() {
  	return IWMainApplicationSettings.isDebugActive();
  }
  //END STANDARD LOGGING METHODS
  


}
