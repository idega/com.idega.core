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
import com.idega.util.datastructures.*;

/**
*@author <a href="mailto:tryggvi@idega.is">Tryggvi Larusson</a>
*@version 1.2
*/
public  class EntityControl{

    private static HashtableDoubleKeyed relationshipTables=new HashtableDoubleKeyed();
    private static HashtableMultivalued  relationshipClasses = new HashtableMultivalued();
    private static boolean autoCreate=false;

    // throw away!!
	protected static String getInterbaseGeneratorName(GenericEntity entity){
		String entityName = entity.getTableName();
		if (entityName.endsWith("_")){
			return (entityName+"gen").toUpperCase();
		}
		else{
			return (entityName+"_gen").toUpperCase();
		}
	}

    // throw away!!
	protected static String getOracleSequenceName(GenericEntity entity){
		String entityName = entity.getTableName();
		return entityName+"_seq";
    //if (entityName.endsWith("_")){
		//	return entityName+"seq";
		//}
		//else{
		//	return entityName+"_seq";
		//}
	}


	/**
	**Creates a unique ID for the ID column
	**/
    // throw away!!
	public static int createUniqueID(GenericEntity entity) throws SQLException{
		int returnInt = -1;
		Connection conn = null;
		Statement stmt = null;
		ResultSet RS = null;
		try{

			conn = entity.getConnection();
			String datastoreType=DatastoreInterface.getDataStoreType(conn);

			if (datastoreType.equals("interbase")){
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
			}

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




  /**
	public static void insert(GenericEntity entity)throws SQLException{


    Connection conn= null;
		//Statement Stmt= null;
		PreparedStatement Stmt = null;

                Statement Stmt2= null;
		ResultSet RS = null;
		//PreparedStatement Stmt = null;
		try{
			conn = entity.getConnection();
			String datastoreType= DatastoreInterface.getDataStoreType(conn);

			if (datastoreType.equals("interbase")){
				if ( entity.isNull(entity.getIDColumnName()) ){
					entity.setID(createUniqueID(entity));
				}

				//Stmt = conn.createStatement();
				//int i = Stmt.executeUpdate("insert into "+entity.getTableName()+"("+entity.getCommaDelimitedColumnNames()+") values ("+entity.getCommaDelimitedColumnValues()+")");
                                String statement = "insert into "+entity.getTableName()+"("+entity.getCommaDelimitedColumnNames()+") values ("+entity.getQuestionmarksForColumns()+")";
                                //System.out.println(statement);
		                Stmt = conn.prepareStatement (statement);
                                setForPreparedStatement(Stmt,entity);
                                Stmt.execute();
                        }
			else if (datastoreType.equals("oracle")){
				if ( entity.isNull(entity.getIDColumnName()) ){
					entity.setID(createUniqueID(entity));
				}

				//Stmt = conn.createStatement();
				//int i = Stmt.executeUpdate("insert into "+entity.getTableName()+"("+entity.getCommaDelimitedColumnNames()+") values ("+entity.getCommaDelimitedColumnValues()+")");

                                String statement = "insert into "+entity.getTableName()+"("+entity.getCommaDelimitedColumnNames()+") values ("+entity.getQuestionmarksForColumns()+")";
                                //System.out.println(statement);
		                Stmt = conn.prepareStatement (statement);
                                setForPreparedStatement(Stmt,entity);
                                Stmt.execute();

                        }
			else if (datastoreType.equals("mysql")){
				//conn = entity.getConnection();
				//Stmt = conn.createStatement();
				//int i = Stmt.executeUpdate("insert into "+entity.getTableName()+"("+entity.getCommaDelimitedColumnNames()+") values ("+entity.getCommaDelimitedColumnValues()+")");
                                String statement = "insert into "+entity.getTableName()+"("+entity.getCommaDelimitedColumnNames()+") values ("+entity.getQuestionmarksForColumns()+")";
                                //System.out.println(statement);
		                Stmt = conn.prepareStatement (statement);
                                setForPreparedStatement(Stmt,entity);
                                Stmt.execute();

                                if (entity.getID() == -1){
					Stmt2 = conn.createStatement();
					RS = Stmt2.executeQuery("select last_insert_id()");
					RS.next();
					entity.setID(RS.getInt(1));
				}
			}

		}
		finally{
			if (RS != null){
				RS.close();
			}
			if(Stmt2 != null){
				Stmt2.close();
			}
			if(Stmt != null){
				Stmt.close();
			}
			if (conn != null){
				entity.freeConnection(conn);
			}
		}
	}
  **/

/*
  public static void update(GenericEntity entity)throws SQLException{
      try{
        DatastoreInterface.getInstance(entity).update(entity);
      }
      catch(Exception ex){
        if(ex instanceof SQLException){
          ex.printStackTrace();
          throw (SQLException)ex.fillInStackTrace();
        }
      }
  }
*/

  /*
	public static void update(GenericEntity entity)throws SQLException{
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
	}*/

  /*
	protected static String setForPreparedStatement(PreparedStatement statement,GenericEntity entity)throws SQLException{
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
  */

  /*
	private static void insertIntoPreparedStatement(String columnName,PreparedStatement statement, int index,GenericEntity entity)throws SQLException{
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
*/



	public static void deleteMultiple(GenericEntity entity, String columnName,String stringColumnValue)throws SQLException{
		Connection conn= null;
		Statement Stmt= null;
		try{
			conn = entity.getConnection();
			Stmt = conn.createStatement();
			Stmt.executeUpdate("delete from "+entity.getEntityName()+" where "+columnName+"='"+stringColumnValue+"'");

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


	public static void delete(GenericEntity entity)throws SQLException{
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
                entity.setState(entity.STATE_DELETED);
	}

	public static void delete(GenericEntity entity, Connection conn)throws Exception{
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
          entity.setState(entity.STATE_DELETED);
	}



	/**
	*Deletes everything from the table of this entity - use with CAUTION :)
	**/
	public static void clear(GenericEntity entity)throws SQLException{
		Connection conn= null;
		Statement Stmt= null;
		try{
			conn = entity.getConnection();
			Stmt = conn.createStatement();
			Stmt.executeUpdate("delete from "+entity.getEntityName());

		}
		finally{
			if(Stmt != null){
				Stmt.close();
			}
			if (conn != null){
				entity.freeConnection(conn);
			}
		}
                entity.setState(entity.STATE_DELETED);
	}



       /**
        * Attention: Beta implementation
        */
      public static void addManyToManyRelationShip(GenericEntity relatingEntity1,GenericEntity relatingEntity2,String relationShipTableName){
	/*
          if(relationshipTables==null){
            relationshipTables=new Vector();
          }

          EntityAttribute attribute = new EntityAttribute();
          attribute.setAttributeType("relationship");
          attribute.setName(relationShipTableName);

          //relationshipTables
	*/
	addManyToManyRelationShip(relatingEntity1.getClass(),relatingEntity2.getClass(),relationShipTableName);

      }

       /**
        * Attention: Beta implementation
        */
      public static void addManyToManyRelationShip(String relatingEntityClassName1,String relatingEntityClassName2,String relationShipTableName){

        try{
          addManyToManyRelationShip(Class.forName(relatingEntityClassName1),Class.forName(relatingEntityClassName2),relationShipTableName);
        }
        catch(Exception ex){
        }

      }

             /**
        * Attention: Beta implementation
        */
      public static void addManyToManyRelationShip(Class relatingEntityClass1,Class relatingEntityClass2,String relationShipTableName){
          //if(relationshipTables==null){
          //  relationshipTables=new HashtableDoubleKeyed();
          //}
          String relatingEntityClassName1 = relatingEntityClass1.getName();
          String relatingEntityClassName2 = relatingEntityClass2.getName();
          relationshipTables.put(relatingEntityClassName1,relatingEntityClassName2,relationShipTableName);
          relationshipClasses.put(relatingEntityClass1,relatingEntityClass2);
          relationshipClasses.put(relatingEntityClass2,relatingEntityClass1);
      }

      /**
       * Returns a list of Class Objects
       */
      protected static List getManyToManyRelationShipClasses(GenericEntity entity){
        return relationshipClasses.getList(entity.getClass());
      }

      /**
       * Returns a list of String Objects
       */
      protected static List getManyToManyRelationShipTables(GenericEntity entity){
        return relationshipTables.get(entity.getClass().getName());
      }

      protected static String getManyToManyRelationShipName(GenericEntity entity1,GenericEntity entity2){
          return getManyToManyRelationShipName(entity1.getClass().getName(),entity2.getClass().getName());
      }

      protected static String getManyToManyRelationShipName(String relatingEntityClassName1,String relatingEntityClassName2){
          if(relationshipTables==null){
            relationshipTables=new HashtableDoubleKeyed();
            return null;
          }
          return (String)relationshipTables.get(relatingEntityClassName1,relatingEntityClassName2);
      }



	protected static String getMiddleTableString(GenericEntity entity1,GenericEntity entity2){
                String tableToSelectFrom = "";
		if (entity1.getTableName().endsWith("_")){
			if (entity2.getTableName().endsWith("_")){
				tableToSelectFrom = entity1.getTableName()+entity2.getTableName();
				tableToSelectFrom = tableToSelectFrom.substring(0,tableToSelectFrom.length()-1);
			}
			else{
				tableToSelectFrom = entity1.getTableName()+entity2.getTableName();
			}
		}
		else{
			if (entity2.getTableName().endsWith("_")){
				tableToSelectFrom = entity1.getTableName()+"_"+entity2.getTableName();
				tableToSelectFrom = tableToSelectFrom.substring(0,tableToSelectFrom.length()-1);
			}
			else{
				tableToSelectFrom = entity1.getTableName()+"_"+entity2.getTableName();
			}
		}
              return tableToSelectFrom;
        }



        protected static String getNameOfMiddleTable(GenericEntity entity1,GenericEntity entity2){

          String tableName = getManyToManyRelationShipName(entity1,entity2);
          if (tableName==null){
                String tableToSelectFrom = getMiddleTableString(entity1,entity2);
                addManyToManyRelationShip(entity1,entity2,tableToSelectFrom);
                return tableToSelectFrom;
          }
          else{
            return tableName;
          }
        }


        /**
         * Returns the first int from the  first column of the result of the SQL Query
         */
        public static int returnSingleSQLQuery(GenericEntity entity,String SQLString){
            Connection conn = null;
            Statement stmt = null;
            ResultSet rs = null;
            int recordCount = -1;
            try {
                conn = entity.getConnection(entity.getDatasource());
                stmt = conn.createStatement();
                rs = stmt.executeQuery(SQLString);
                if(rs.next())
                    recordCount = rs.getInt(1);
                rs.close();

                //System.out.println(SQLString+"\n");

            }
            catch(SQLException e) {
                System.err.println("There was an error in EntityControl.returnSingleSQLQuery "+e.getMessage());
                e.printStackTrace(System.err);            }
            catch(Exception e) {
                System.err.println("There was an error in EntityControl.returnSingleSQLQuery "+e.getMessage());
                e.printStackTrace(System.err);
            }
            finally{
                if(stmt != null){
                    try{
                      stmt.close();
                    }
                    catch(SQLException ex){
                        System.err.println("There was an error in EntityControl.returnSingleSQLQuery "+ex.getMessage());
                        ex.printStackTrace(System.err);
                    }
                }
                if (conn != null){
                    entity.freeConnection(entity.getDatasource(),conn);
                }
            }
            return recordCount;
        }


        /**
         * Returns an int[] array out of an the first columns row of the result of the SQL Query
         */
        public static int[] returnIntArraySQLQuery(GenericEntity entity,String SQLString){
            Connection conn = null;
            Statement stmt = null;
            ResultSet rs = null;
            int size = 0;
            int[] recordCount = new int[size];
            try {
                conn = entity.getConnection(entity.getDatasource());
                stmt = conn.createStatement();
                rs = stmt.executeQuery(SQLString);
                int counter = 0;
                int recordAt=-1;
                while(rs.next()){
                    try{
                      recordAt = rs.getInt(1);
                      recordCount[counter] = recordAt;
                      counter++;
                    }
                    catch(ArrayIndexOutOfBoundsException ex){
                      size += 10;
                      int[] newArr = new int[size];
                      for (int i = 0; i < recordCount.length; i++) {
                        newArr[i] = recordCount[i];
                      }
                      newArr[counter]=recordAt;
                      recordCount = newArr;
                    }
                }
                rs.close();
                if(size>counter){
                      int[] newArr = new int[counter];
                      for (int i = 0; i < recordCount.length; i++) {
                        newArr[i] = recordCount[i];
                      }
                      recordCount = newArr;
                }

                //System.out.println(SQLString+"\n");

            }
            catch(SQLException e) {
                System.err.println("There was an error in EntityControl.returnSingleSQLQuery "+e.getMessage());
                e.printStackTrace(System.err);            }
            catch(Exception e) {
                System.err.println("There was an error in EntityControl.returnSingleSQLQuery "+e.getMessage());
                e.printStackTrace(System.err);
            }
            finally{
                if(stmt != null){
                    try{
                      stmt.close();
                    }
                    catch(SQLException ex){
                        System.err.println("There was an error in EntityControl.returnSingleSQLQuery "+ex.getMessage());
                        ex.printStackTrace(System.err);
                    }
                }
                if (conn != null){
                    entity.freeConnection(entity.getDatasource(),conn);
                }
            }
            return recordCount;
        }


        public static void setAutoCreationOfEntities(boolean ifAutoCreate){
            autoCreate=ifAutoCreate;
        }

        public static boolean getIfEntityAutoCreate(){
          return autoCreate;
        }
}
