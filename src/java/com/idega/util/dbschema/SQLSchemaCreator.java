package com.idega.util.dbschema;

import java.sql.Connection;
import java.sql.Statement;
import java.util.List;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.transaction.TransactionManager;
import com.idega.idegaweb.IWMainApplication;
import com.idega.transaction.IdegaTransactionManager;
import com.idega.util.ThreadContext;
import com.idega.util.Timer;
import com.idega.util.logging.LoggingHelper;


/**
 * 
 * 
 *  Last modified: $Date: 2005/07/28 12:44:01 $ by $Author: tryggvil $
 * 
 * @author <a href="mailto:aron@idega.com">aron</a>
 * @version $Revision: 1.4 $
 */

public class SQLSchemaCreator{

  private static String recordCreationKey="sqlschemaadapter_schema_creation";
  private SQLSchemaAdapter sa;

  protected SQLSchemaCreator(SQLSchemaAdapter sa){
    this.sa=sa;
  }
  	
  protected void executeQuery(String SQLCode)throws Exception{
    sa.executeQuery(SQLCode);
  }

  protected int executeUpdate(String SQLCode)throws Exception{
    return sa.executeUpdate(SQLCode);
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

  protected void registerEndOfCreatingSchema(Schema schema){
      List alreadyInCreation=(List)ThreadContext.getInstance().getAttribute(recordCreationKey);
      if(alreadyInCreation!=null){
        alreadyInCreation.remove(schema.getUniqueName());
        if(alreadyInCreation.isEmpty()){
          ThreadContext.getInstance().removeAttribute(recordCreationKey);
        }
      }
  }

  protected void registerStartOfCreatingSchema(Schema schema){
      //Code block to prevent circular recursiveness
      //i.e. that it infinately recurses through the same entity when it is circularly referenced
      List alreadyInCreation=this.getCreationList();
      if(!hasAlreadyStartedCreatingSchema(schema)){
        alreadyInCreation.add(schema.getUniqueName());
        //createRefrencedTables(entity);
      }
  }
  
  protected boolean hasAlreadyStartedCreatingSchema(Schema schema){
    List alreadyInCreation=this.getCreationList();
    return alreadyInCreation.contains(schema.getUniqueName());
  }
  
  protected boolean startSchemaCreationTransaction( Schema schema,boolean isPermittedToCommit){
    TransactionManager trans=null;
    boolean canCommit=isPermittedToCommit;
    try{
      if(sa.useTransactionsInSchemaCreation){
        trans = com.idega.transaction.IdegaTransactionManager.getInstance();
        if(!((IdegaTransactionManager)trans).hasCurrentThreadBoundTransaction()){
          sa.executeBeforeSchemaCreation(schema);
          //((IdegaTransactionManager)trans).setEntity(entity);
          trans.begin();
          canCommit=true;
        }
        else{
          canCommit=false;
        }
      }
      registerStartOfCreatingSchema(schema);
    }
    catch(Exception e){
      e.printStackTrace();
    }
    return canCommit;
  }

  protected void endEntityCreationTransaction( Schema schema,boolean isPermittedToCommit,boolean transactionSuccessful){
    boolean canCommit = isPermittedToCommit;
    try{
      TransactionManager trans = com.idega.transaction.IdegaTransactionManager.getInstance();
      if(sa.useTransactionsInSchemaCreation){
        if(canCommit){
          if(transactionSuccessful){
            //System.out.println("\t\t\tCommitting!!!!");
            trans.commit();
            //registerEndOfCreatingEntity(entity);
          }
          else{
            //System.out.println("\t\t\tRollbacking!!!!");
            trans.rollback();
            //registerEndOfCreatingEntity(entity);
          }
          ThreadContext.getInstance().removeAttribute(recordCreationKey);
          sa.executeAfterSchemaCreation( schema);
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
  
  protected boolean doesViewExist(String viewName){
  	boolean returner =true;
  	Timer timer = new Timer();
  	timer.start();
  	try {
		if(!sa.doesViewExist(viewName))
			throw new Exception("View "+viewName+"does not exists");
	}
	catch (Exception e) {
		returner =false;
	}
    timer.stop();
    debug("doesViewExist() check took "+(timer.getTime())+" milliseconds"+"  ("+viewName+")");
  	return returner;
  }

  protected boolean doesTableExist(String tableName){
    boolean theReturner=true;
    try{
    	long start = System.currentTimeMillis();
    
    	doTableCheckAdapter(tableName);
        
    long end = System.currentTimeMillis();
	debug("doesTableExist() check took "+((end-start))+" milliseconds"+"  ("+tableName+")");
      
    }
    catch(Exception se){
        theReturner=false;
    }
    return theReturner;
  }
  
  
  
  private void doTableCheckAdapter(String tableName)throws Exception{
	  if(!sa.doesTableExist(tableName))
	  	throw new Exception("Table "+tableName+"does not exists");
}



  
  public boolean generateSchema(Schema schema)throws Exception{
  	
    if(!doesTableExist(schema.getSQLName())){
		//if(this.isDebugActive())
    	debug("Creating "+schema.getUniqueName()+" - tablename: "+schema.getSQLName());

      boolean canCommit=false;
      canCommit = this.startSchemaCreationTransaction(schema,canCommit);

      try{

        //Create the records of all referenced entities (which this entity has dependent relationships on)
        if(!this.hasAlreadyStartedCreatingSchema(schema)){
          createRefrencedTables( schema);
        }
        //Check again if table exists because it could be created through createRefrencedTables(entity)
        if(!this.doesTableExist(schema.getSQLName())){
          createTable(schema);
          if(schema.hasAutoIncrementColumn())
          	createTrigger(schema);
          try{
            createForeignKeys(schema);
          }
          catch(Exception e){
           e.printStackTrace();
          }
          
          try{
          	createIndexes(schema);
          }
          catch(Exception e){
            e.printStackTrace();
             //this can fail but don't kill the transaction then!
           }
          
          //createMiddleTables(entityDefinition);
          /*
          if(entity.getIfInsertStartData()){
	      		_entityWithStartData.add(entity);
          }
          */
        }
        
        
      	this.endEntityCreationTransaction( schema,canCommit,true);
      	return true;
      	/*
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
        	*/

      }
      catch(Exception ex){
		//if(this.isDebugActive()){
        	System.err.println("===");
        	System.err.println("Exception and rollback for: "+schema.getUniqueName());
        	System.err.println("\tMessage: "+ex.getMessage());
        	ex.printStackTrace();
        	System.err.println("===");
		//}
        //ex.printStackTrace();
        /**@todo fix this Tryggvi so that we can use it!**/
        this.endEntityCreationTransaction(schema,canCommit,false);

        //tmp fix
        //this.endEntityCreationTransaction(entity,canCommit,true);
      }
    }
    else{
    		debug("Synchronizing  "+schema.getUniqueName()+" - tablename: "+schema.getSQLName());

      boolean canCommit = false;
      canCommit = this.startSchemaCreationTransaction( schema,canCommit);
      updateColumns( schema);
      updateIndexes( schema);
      updateTriggers(schema);
      //createMiddleTables(entityDefinition);
      this.endEntityCreationTransaction(schema,canCommit,true);
    }//End if(!doesTableExist())
    return false;
	
  }
  
  /**
	 * Creates an entity record (view) that represents the view entity in the datastore
	 */
  /*
	public void createEntityView(GenericView entityView){
		if(!doesViewExist(entityView.getViewName())){
			boolean canCommit=false;
			debug("Creating "+entityView.getClass().getName()+" - view: "+entityView.getTableName());
			try {
				canCommit = this.startEntityCreationTransaction(entityView.getEntityDefinition(),canCommit);
				//Create the records of all referenced entities (which this entity has dependent relationships on)
				if(!this.hasAlreadyStartedCreatingEntity(entityView.getEntityDefinition())){
					createRefrencedTables(entityView.getEntityDefinition());
				}
				createView(entityView);
				//commit
				this.endEntityCreationTransaction(entityView.getEntityDefinition(),canCommit,true);
			}
			catch (Exception e) {
				log(e);
				// rollback
				this.endEntityCreationTransaction(entityView.getEntityDefinition(),canCommit,false);
				e.printStackTrace();
			}
		} 
		else{
			debug("Synchronizing  "+entityView.getClass().getName()+" - viewname: "+entityView.getViewName());
		}
	}*/


  protected String getCreationStatement(Schema schema){
  	//*/EntityField[] pkFields = entity.getEntityDefinition().getPrimaryKeyDefinition().getFields();
  	SchemaColumn[] pkFields = schema.getPrimaryKey().getColumns();
  	
  	//StringBuffer returnString = new StringBuffer("CREATE TABLE ").append(entity.getTableName()).append("(");
  	// */String tableName = entity.getEntityDefinition().getSQLTableName();
  	String tableName = schema.getSQLName();
  	StringBuffer returnString = new StringBuffer(sa.getCreateTableCommand(tableName)).append(" (");
  	
  	SchemaColumn[] fields = schema.getColumns();
	for (int i = 0; i < fields.length; i++){
        //String columnName = fields[i].getSQLName();
        returnString.append(getColumnSQLDefinition(fields[i],schema));
        if (i!=fields.length-1){
          returnString.append(",");
        } 
        else if (pkFields != null && pkFields.length > 0) {
        	 returnString.append(", PRIMARY KEY (");
        	 for (int j = 0; j < pkFields.length; j++) {
    			if (j != 0) {
    				returnString.append(",");
    			}
    			returnString.append(pkFields[j].getSQLName());
    			int limit = sa.getMaxColumnPrimaryKeyLength(pkFields[j]);
    			if(limit==-1){
    			}else{
    				returnString.append("(");
    				returnString.append(limit);
    				returnString.append(")");
    			}
        	 }
        	 returnString.append(")");
        }
	}
	try {
		UniqueKey[] uniqueKeys = schema.getUniqueKeys();
		if(uniqueKeys!=null){
			
			for (int i = 0; i < uniqueKeys.length; i++) {
				returnString.append(" ,");
				returnString.append(getUniqueKeyCreationStatement(schema, uniqueKeys[i].getFields()));
			}
		}
	} catch (Exception e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
    returnString.append(")");
    //System.out.println(returnString);
    return returnString.toString();
  }

  public void removeSchema( Schema entity)throws Exception{
      deleteSchema( entity);
  }

  protected void deleteSchema(Schema schema)throws Exception{
		Connection conn= null;
		Statement Stmt= null;
		try{
			conn = sa.getConnection();
			Stmt = conn.createStatement();
			Stmt.executeUpdate("drop table "+schema.getSQLName());
		}
		finally{
			if(Stmt != null){
				Stmt.close();
			}
			if (conn != null){
				sa.freeConnection(conn);
			}
		}
  }

  
  protected void createRefrencedTables(Schema  schema)throws Exception{
    
    SchemaColumn[] fields  = schema.getColumns();
    for (int i = 0; i < fields.length; i++) {
		if(fields[i].isPartOfManyToOneRelationship())
			if(!fields[i].getManyToOneRelated().getUniqueName().equals(schema.getUniqueName()))
				generateSchema(fields[i].getManyToOneRelated());
	}
    /*
    Schema defs[] = entityDefinition.getManyToManyRelatedEntities();
    for (int i = 0; i < defs.length; i++) {
    	   if(!defs[i].getUniqueName().equals(entityDefinition.getUniqueName()))
		 createEntityRecord(defs[i]);
	}*/
}
  

  /**
   * Gets the entities that are related by  one-to many and many-to-many relationships
   * Returns a List of Class Objects
   */
  /*
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
  }*/

  /**
   * Gets the entities that are related by many-to-many relationships
   * Returns a List of Class Objects
   */
  /*
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
  }*/

 /*
  protected void createMiddleTables( EntityDefinition entityDefinition)throws Exception{
    EntityDefinition[] defs = entityDefinition.getManyToManyRelatedEntities();
    if(defs!=null){
      for (int i = 0; i < defs.length; i++) {
          
	    EntityRelationship relation = EntityControl.getManyToManyRelationShip(entityDefinition.getSQLTableName(),defs[i].getSQLTableName());
        Map relMap = relation.getColumnsAndReferencingEntityDefinitions();
        String tableName = relation.getTableName();

        boolean doCreateMiddleTable = !doesTableExist(tableName);

        if(doCreateMiddleTable){
             Set tempSet = relMap.keySet();
             Iterator tempIter = tempSet.iterator();
            while (tempIter.hasNext() && doCreateMiddleTable) {
              String column = (String)tempIter.next();
              EntityDefinition relDef = (EntityDefinition)relMap.get(column);
              //GenericEntity entity1 = (GenericEntity)relClass.newInstance();
              //GenericEntity entity1 = (GenericEntity)IDOContainer.getInstance().instanciateBean(relClass);
              //String referencingTableName = entity1.getTableName();
              String referencingTableName = entityDefinition.getMiddleTableNameForRelation(relDef.getSQLTableName());
              doCreateMiddleTable = doesTableExist(referencingTableName);
            }
        }


        //try{
          if(doCreateMiddleTable){
            String creationStatement = _dsi.getCreateTableCommand(tableName);
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
              //Class relClass = (Class)relMap.get(column);
              EntityDefinition relDef = (EntityDefinition)relMap.get(column);
              //GenericEntity entity1 = (GenericEntity)relClass.newInstance();
              //GenericEntity entity1 = (GenericEntity)IDOContainer.getInstance().instanciateBean(relClass);

              //creationStatement += column + " INTEGER NOT NULL";
              //creationStatement += column+this.getPrimaryKeyReferenceForManyToManyRelationship(entity1,column,entity1.getIDColumnName());
              creationStatement += column+getPrimaryKeyReferenceForManyToManyRelationship(relDef.getPrimaryKeyDefinition().getField());
              primaryKeyStatement +=column;
              mayAddComma = true;
            }
            creationStatement += ")";
            primaryKeyStatement +=")";
            executeUpdate(creationStatement);
            executeUpdate(primaryKeyStatement);


             set = relMap.keySet();
             iter = set.iterator();
            while (iter.hasNext()) {
              String column = (String)iter.next();
              EntityDefinition entityDef = (EntityDefinition)relMap.get(column);
              //try{
               //GenericEntity entity1 = (GenericEntity)IDOLookup.getBeanClassFor(relClass).newInstance();
                //createEntityRecord(entity1);
                createForeignKey(tableName,column,entityDef.getSQLTableName(),entityDef.getPrimaryKeyDefinition().getField().getSQLFieldName());
              
            }
        }

      }
    }

  }*/
  
  protected void createIndexes(Schema schema)throws Exception{
    Index[] indexes = schema.getIndexes();
    if(indexes!=null){
	    for (int i = 0; i < indexes.length; i++) {
	        createIndex(indexes[i]);
	    }
    }	
  }
  
  private void dropIndex(Schema entity, String name) throws Exception {
  	String sql = "DROP INDEX "+entity.getSQLName()+"."+name;
  	executeUpdate( sql);
  }
  
  private void createIndex( Index index)throws Exception{
		if (sa.useIndexes()) {
	  		StringBuffer sql = new StringBuffer("CREATE ");
	  		if(index.isUnique())
	  		    sql.append(" UNIQUE ");
	  		sql.append(" INDEX ");
			sql.append(index.getIndexName()).append(" ON ").append(index.getSchemaName()).append(" (");
			IndexColumn[] columns = index.getColumns();
	  		for (int i = 0; i < columns.length; i++) {
	  			if (i > 0) {
	  				sql.append(", ");
	  			}
	  			sql.append(columns[i].getName());
	  			if(columns[i].isDescending())
	  			    sql.append(" DESC ");
	  		}
	  		sql.append(")");
	  		
	  		executeUpdate( sql.toString());
		}
  }

  
  protected void createForeignKeys( Schema schema) throws Exception {
    SchemaColumn[] columns = schema.getColumns();
	for (int i = 0; i < columns.length; i++) {
          if(columns[i].isPartOfManyToOneRelationship()){
          	Schema entityDefToReference = columns[i].getManyToOneRelated();
			String columnName = columns[i].getSQLName();
			try{
				createForeignKey(schema,entityDefToReference,columnName);
			}
			catch(Exception e){
				logError("Error Creating foreign key for entity "+schema.getSQLName()+" and field "+columnName+". Error message was : "+e.getMessage());
			}
           
          }
    }
  }
  
  protected String getUniqueKeyCreationStatement(Schema schema,SchemaColumn[] columns)throws Exception{
  	return sa.getCreateUniqueKeyStatement(schema,columns);
  }
  
  /*
  protected void createView(GenericView viewEntity)throws Exception{
  	executeUpdate(viewEntity.getCreationSQL());
  }*/
  
  protected void createTable(Schema  schema)throws Exception{
    //if(!doesTableExist(entity,entity.getTableName())){
      executeUpdate(getCreationStatement(schema));
    //}
  }

  
  protected void createTrigger( Schema schema)throws Exception{
    this.sa.createTrigger( schema);
  }

  
  protected void createForeignKey( Schema schema,Schema schemaToReference,String columnName)throws Exception{
    createForeignKey(schema.getSQLName(),columnName,schemaToReference.getSQLName(),schemaToReference.getPrimaryKey().getColumn().getSQLName());
}
   
  
  protected void createForeignKey(String baseTableName,String columnName, String refrencingTableName)throws Exception{
    createForeignKey(baseTableName,columnName,refrencingTableName,columnName);
}

  
  
  protected void createForeignKey(String baseTableName,String columnName, String refrencingTableName,String referencingColumnName)throws Exception{
    sa.createForeignKey(baseTableName,columnName,refrencingTableName,referencingColumnName);
}

 
  
  protected void createPrimaryKey(String baseTableName,String columnName, String refrencingTableName,String referencingColumnName)throws Exception{
    sa.createForeignKey(baseTableName,columnName,refrencingTableName,referencingColumnName);
}

  
  protected void updateColumns( Schema schema)throws Exception{
  	String[] columnArrayFromDB = getColumnArrayFromDataBase(schema.getSQLName());
  	SchemaColumn[] columns = schema.getColumns();
    for (int i = 0; i < columns.length; i++) {
      String column = columns[i].getSQLName();
      if(!hasEntityColumn(column,columnArrayFromDB)){
        try{
          addColumn( columns[i], schema);
          if(columns[i].isPartOfManyToOneRelationship()){
              this.createForeignKey( schema, columns[i].getManyToOneRelated(),column);
          }
        }
        catch(Exception e){
          //e.printStackTrace();
        }
      }
    }
  }
  
  private boolean compareIndexColumns(String[] arr1, String[] arr2) {
  		if (arr1 != null && arr2 != null && arr1.length == arr2.length) {
  			boolean returner = true;
  			for (int i = 0; i < arr1.length && returner; i++) {
  				returner = false;
  				for (int j = 0; j < arr2.length && !returner; j++) {
  					if (arr1[i].equals(arr2[j])) {
  						returner = true;
  					}
  				}
  				
  				if (!returner) {
  					return returner;
  				}
  			}
  			return true;
  		}
  		return false;
  }

  
  private void updateTriggers(Schema schema) {
	try {
		sa.updateTriggers(schema, true);
	}
	catch (Exception e) {
		e.printStackTrace();
	}
  }

 
  
  private void updateIndexes( Schema schema) {
		/*if (sa.useIndexes()) {
			Index[] indexesFromDB = sa.getTableIndexes( schema.getSQLName());
  		try {
  		    
  		    		
				Index[] indexes = schema.getIndexes();
				
  		    
  		    		
				Set indexesFromEntity = map.keySet();
				Set setFromDB = indexesFromDB.keySet();
			
				// Removing keys from map that exist in DB
				Iterator dbKeyIter = setFromDB.iterator();
				String dbKey;
				while (dbKeyIter.hasNext()) {
					dbKey = (String) dbKeyIter.next();
					if (map.containsKey(dbKey)) {
						map.remove(dbKey);
					}
				}

				// Removing columns from map that are already indexed
				if (indexesFromDB != null && !indexesFromDB.isEmpty()) {
					try {
						HashMap tempMap = new HashMap(map);
						Iterator dbValues = indexesFromDB.values().iterator();
						Iterator entityKeys;// = tempMap.keySet().iterator();
						String[] columns = null;
						while (dbValues.hasNext()) {
							columns = (String[]) dbValues.next();
							entityKeys = tempMap.keySet().iterator();
							while (entityKeys.hasNext()) {
								String key = (String) entityKeys.next();
								if (compareIndexColumns(columns, (String[]) map.get( key ))) {
									map.remove(key);
								}
							}
						}
					
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
					
				// CREATING
				Iterator iter = indexesFromEntity.iterator();
				String indexName;
				while (iter.hasNext()) {
					indexName = (String) iter.next();
					try {
						this.createIndex(entityDefinition.getSQLTableName(), indexName, (String[]) map.get(indexName));
					}
					catch (Exception e1) {
						System.out.println("IDOTableCreator : failed to create index : "+indexName+" ("+e1.getMessage()+")");
					}
				}
			}
			catch (Exception e) {
		
			}
		}*/

}

  
 
  private String[] getColumnArrayFromDataBase( String sqlTablename ){
  	return sa.getTableColumnNames(sqlTablename);
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

 
  private void addColumn(SchemaColumn column,Schema schema)throws Exception{
    String SQLString = sa.getAddColumnCommand(column,schema);
  	executeUpdate(SQLString);
  }

  
  
  protected String getColumnSQLDefinition(SchemaColumn column,Schema schema){
  	return sa.getColumnSQLDefinition(column, schema);
  }

  

  
  
  protected String getPrimaryKeyReferenceForManyToManyRelationship(SchemaColumn column){
    try{
      
      Class storageClass = column.getDataTypeClass();
      int maxLength = column.getMaxLength();
      String sqlType = this.sa.getSQLType(storageClass,maxLength);
      return " "+sqlType + " NOT NULL";
    }
    catch(NullPointerException ne){
      ne.printStackTrace();
      System.err.println("---");
      System.err.println("Nullpointer where entity="+column.getSchema().getUniqueName()+" and column="+column.getSQLName());
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
		return getIWMainApplication().getSettings().isDebugActive();
	}
	
	public IWMainApplication getIWMainApplication(){
		return IWMainApplication.getDefaultIWMainApplication();
	}
  //END STANDARD LOGGING METHODS
  


}
