//idega 2000 - Tryggvi Larusson
/*
*Copyright 2000 idega.is All Rights Reserved.
*/
package com.idega.data;
import java.sql.SQLException;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;
import java.util.Collection;
import java.util.List;
import java.util.Vector;
import java.util.Iterator;
import com.idega.util.StringHandler;
import com.idega.util.datastructures.HashtableDoubleKeyed;
import com.idega.util.datastructures.HashtableMultivalued;
/**
 * Title:        idega Data Objects
 * Description:  Idega Data Objects is a Framework for Object/Relational mapping and seamless integration between datastores
 * Copyright:    Copyright (c) 2001
 * Company:      idega
 * @author        <a href="mailto:tryggvi@idega.is">Tryggvi Larusson</a>
 * @version 1.0
 */
public class EntityControl {
	private static HashtableDoubleKeyed relationshipTables = new HashtableDoubleKeyed();
	private static HashtableMultivalued relationshipClasses = new HashtableMultivalued();
	private static boolean autoCreate = false;
	protected static boolean limitTableNameToThirtyCharacters = false;
	/**
	**Creates a unique ID for the ID column
	*@deprecated moved to InterbaseDatastoreInterface. This method will be
	*removed in future versions */
	// throw away!!
	protected static String getInterbaseGeneratorName(IDOLegacyEntity entity) {
		String entityName = entity.getTableName();
		if (entityName.endsWith("_")) {
			return (entityName + "gen").toUpperCase();
		}
		else {
			return (entityName + "_gen").toUpperCase();
		}
	}
	/**
	**Creates a unique ID for the ID column
	*@deprecated moved to OracleDatastoreInterface. This method will be removed
	*in future versions */
	// throw away!!
	protected static String getOracleSequenceName(IDOLegacyEntity entity) {
		String entityName = entity.getTableName();
		return entityName + "_seq";
		//if (entityName.endsWith("_")){
		//	return entityName+"seq";
		//}
		//else{
		//	return entityName+"_seq";
		//}
	}
	/**
	**Creates a unique ID for the ID column
	*@deprecated moved to DatastoreInterface and its subclasses
	*This method will be removed in future versions
	**/
	// throw away!!
	public static int createUniqueID(IDOLegacyEntity entity) throws SQLException {
		int returnInt = -1;
		Connection conn = null;
		Statement stmt = null;
		ResultSet RS = null;
		try {
			conn = entity.getConnection();
			String datastoreType = DatastoreInterface.getDataStoreType(conn);
			if (datastoreType.equals("interbase")) {
				stmt = conn.createStatement();
				RS = stmt.executeQuery("SELECT GEN_ID(" + getInterbaseGeneratorName(entity) + ", 1) FROM RDB$DATABASE");
				RS.next();
				returnInt = RS.getInt(1);
			}
			else if (datastoreType.equals("oracle")) {
				stmt = conn.createStatement();
				RS = stmt.executeQuery("SELECT " + getOracleSequenceName(entity) + ".nextval  FROM dual");
				RS.next();
				returnInt = RS.getInt(1);
			}
		}
		finally {
			if (RS != null) {
				RS.close();
			}
			if (stmt != null) {
				stmt.close();
			}
			if (conn != null) {
				entity.freeConnection(conn);
			}
		}
		return returnInt;
	}
	/**
	
		public static void insert(IDOLegacyEntity entity)throws SQLException{
	
	
	
	
	
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
	
	  public static void update(IDOLegacyEntity entity)throws SQLException{
	
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
	
		public static void update(IDOLegacyEntity entity)throws SQLException{
	
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
	
		protected static String setForPreparedStatement(PreparedStatement statement,IDOLegacyEntity entity)throws SQLException{
	
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
	
		private static void insertIntoPreparedStatement(String columnName,PreparedStatement statement, int index,IDOLegacyEntity entity)throws SQLException{
	
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
	public static void deleteMultiple(IDOLegacyEntity entity, String columnName, String stringColumnValue) throws SQLException {
		Connection conn = null;
		Statement Stmt = null;
		try {
			conn = entity.getConnection();
			Stmt = conn.createStatement();
			Stmt.executeUpdate("delete from " + entity.getEntityName() + " where " + columnName + "='" + stringColumnValue + "'");
		}
		finally {
			if (Stmt != null) {
				Stmt.close();
			}
			if (conn != null) {
				entity.freeConnection(conn);
			}
		}
	}
	public static void deleteMultiple(
		IDOLegacyEntity entity,
		String columnName1,
		String stringColumnValue1,
		String columnName2,
		String stringColumnValue2)
		throws SQLException {
		Connection conn = null;
		Statement Stmt = null;
		try {
			conn = entity.getConnection();
			Stmt = conn.createStatement();
			Stmt.executeUpdate(
				"delete from "
					+ entity.getEntityName()
					+ " where "
					+ columnName1
					+ "='"
					+ stringColumnValue1
					+ "' and "
					+ columnName2
					+ "='"
					+ stringColumnValue2
					+ "'");
		}
		finally {
			if (Stmt != null) {
				Stmt.close();
			}
			if (conn != null) {
				entity.freeConnection(conn);
			}
		}
	}
	/**
	*Deletes everything from the table of this entity - use with CAUTION :)
	**/
	public static void clear(IDOLegacyEntity entity) throws SQLException {
		Connection conn = null;
		Statement Stmt = null;
		try {
			conn = entity.getConnection();
			Stmt = conn.createStatement();
			Stmt.executeUpdate("delete from " + entity.getEntityName());
		}
		finally {
			if (Stmt != null) {
				Stmt.close();
			}
			if (conn != null) {
				entity.freeConnection(conn);
			}
		}
		entity.setEntityState(entity.STATE_DELETED);
	}
	public static EntityRelationship addManyToManyRelationShip(
		IDOLegacyEntity relatingEntity1,
		IDOLegacyEntity relatingEntity2,
		String relationShipTableName) {
		try {
			String idColumnName1 = relatingEntity1.getIDColumnName();
			String idColumnName2 = relatingEntity2.getIDColumnName();
			return addManyToManyRelationShip(relatingEntity1, relatingEntity2, relationShipTableName, idColumnName1, idColumnName2);
		}
		catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	protected static EntityRelationship addManyToManyRelationShip(String relatingEntityClassName1, String relatingEntityClassName2) {
		try {
			IDOLegacyEntity entity1 = GenericEntity.getStaticInstance(relatingEntityClassName1);
			IDOLegacyEntity entity2 = GenericEntity.getStaticInstance(relatingEntityClassName2);
			String generatedTableName = createMiddleTableString(entity1, entity2);
			return addManyToManyRelationShip(entity1, entity2, generatedTableName);
		}
		catch (Exception ex) {
			ex.printStackTrace();
			return null;
		}
	}
	public static EntityRelationship addManyToManyRelationShip(
		String relatingEntityClassName1,
		String relatingEntityClassName2,
		String relationShipTableName) {
		try {
			return addManyToManyRelationShip(
				com.idega.data.GenericEntity.getStaticInstance(relatingEntityClassName1),
				com.idega.data.GenericEntity.getStaticInstance(relatingEntityClassName2),
				relationShipTableName);
		}
		catch (Exception ex) {
			ex.printStackTrace();
			return null;
		}
	}
	public static String getTreeRelationShipTableName(IDOLegacyEntity entity) {
		EntityRelationship rel = getManyToManyRelationShip(entity, entity);
		if (rel != null) {
			return rel.getTableName();
		}
		else {
			return null;
		}
		/*
		String treeName = entity.getTableName() + "_tree";
		treeName = getCheckedRelatedTableName(treeName);
		return treeName;
		*/
	}
	private static String createTreeRelationShipTableName(IDOLegacyEntity entity) {
		String treeName = entity.getTableName() + "_tree";
		return treeName;
	}
	public static String getTreeRelationShipChildColumnName(IDOLegacyEntity entity) {
		return "child_" + entity.getIDColumnName();
	}
	public static void addTreeRelationShip(IDOLegacyEntity entity) {
		String relationShipTableName = createTreeRelationShipTableName(entity);
		String idColumnName1 = entity.getIDColumnName();
		String idColumnName2 = getTreeRelationShipChildColumnName(entity);
		addManyToManyRelationShip(entity, entity, relationShipTableName, idColumnName1, idColumnName2);
	}
	static String getCheckedRelatedTableName(String relatedTableName) {
		//this is necessary for Oracle and maybe others!
		if (limitTableNameToThirtyCharacters) {
			//relatedTableName = relatedTableName.substring(0, Math.min(relatedTableName.length(), 30));	
			relatedTableName = getTableNameShortenedToThirtyCharacters(relatedTableName);
		}
		return relatedTableName;
	}
	static EntityRelationship addManyToManyRelationShip(IDOLegacyEntity relatingEntity1, IDOLegacyEntity relatingEntity2) {
		String relationShipTableName = createMiddleTableString(relatingEntity1, relatingEntity2);
		return addManyToManyRelationShip(relatingEntity1, relatingEntity2, relationShipTableName);
	}
	private static EntityRelationship addManyToManyRelationShip(
		IDOLegacyEntity relatingEntity1,
		IDOLegacyEntity relatingEntity2,
		String relationShipTableName,
		String column1,
		String column2) {
		//if(relationshipTables==null){
		//  relationshipTables=new HashtableDoubleKeyed();
		//}
		String relatingEntityName1 = relatingEntity1.getEntityName();
		String relatingEntityName2 = relatingEntity2.getEntityName();
		EntityRelationship rel = new EntityRelationship();
		rel.setTableName(relationShipTableName);
		Class relatingEntityClass1 = relatingEntity1.getClass();
		Class relatingEntityClass2 = relatingEntity2.getClass();
		rel.addColumn(column1, relatingEntityClass1);
		rel.addColumn(column2, relatingEntityClass2);
		relationshipTables.put(relatingEntityName1, relatingEntityName2, rel);
		//relationshipTables.put(relatingEntityClassName1,relatingEntityClassName2,relationShipTableName);
		relationshipClasses.put(relatingEntityClass1, relatingEntityClass2);
		relationshipClasses.put(relatingEntityClass2, relatingEntityClass1);
		return rel;
	}
	/**
	 * Returns a list of Class Objects
	 */
	public static List getManyToManyRelationShipClasses(Class entityClass) {
		return getManyToManyRelationShipClasses(com.idega.data.GenericEntity.getStaticInstance(entityClass));
	}
	/**
	 * Returns a list of Class Objects
	 */
	protected static List getManyToManyRelationShipClasses(IDOLegacyEntity entity) {
		return relationshipClasses.getList(entity.getClass());
	}
	/**
	 * Returns a list of EntityRelationship Objects
	 */
	protected static List getManyToManyRelationShips(IDOLegacyEntity entity) {
		if (relationshipTables != null) {
			return relationshipTables.get(entity.getEntityName());
		}
		return null;
	}
	/**@todo : set back to protected
	 *
	 */
	public static String getManyToManyRelationShipTableName(Class entityClass1, Class entityClass2) {
		return getManyToManyRelationShipName(
			com.idega.data.GenericEntity.getStaticInstance(entityClass1),
			com.idega.data.GenericEntity.getStaticInstance(entityClass2));
	}
	protected static String getManyToManyRelationShipName(IDOLegacyEntity entity1, IDOLegacyEntity entity2) {
		return getManyToManyRelationShipName(entity1.getEntityName(), entity2.getEntityName());
	}
	protected static String getManyToManyRelationShipName(String relatingEntityName1, String relatingEntityName2) {
		EntityRelationship rel = getManyToManyRelationShip(relatingEntityName1, relatingEntityName2);
		if (rel != null) {
			return rel.getTableName();
		}
		else {
			return null;
		}
	}
	private static String createMiddleTableString(IDOLegacyEntity entity1, IDOLegacyEntity entity2) {
		String tableToSelectFrom = "";
		//		commented out by Eiki 14.nov.2002 this was not looking up the name correctly
		//		if (entity1.getEntityName().endsWith("_")) {
		//			tableToSelectFrom = entity1.getEntityName() + entity2.getEntityName();
		//		} else {
		//			tableToSelectFrom = entity1.getEntityName() + "_" + entity2.getEntityName();
		//		}
		//		if (tableToSelectFrom.endsWith("_")) {
		//			tableToSelectFrom = tableToSelectFrom.substring(0, tableToSelectFrom.length() - 1);
		//		}
		tableToSelectFrom = StringHandler.concatAlphabetically(entity1.getEntityName(), entity2.getEntityName(), "_");
		return tableToSelectFrom;
		//return getCheckedRelatedTableName(tableToSelectFrom);
	}
	protected static String getNameOfMiddleTable(IDOLegacyEntity entity1, IDOLegacyEntity entity2) {
		String tableName = getManyToManyRelationShipName(entity1, entity2);
		if (tableName == null) {
			String generatedTableName = createMiddleTableString(entity1, entity2);
			EntityRelationship rel = addManyToManyRelationShip(entity1, entity2, generatedTableName);
			return rel.getTableName();
		}
		else {
			return tableName;
		}
	}
	/**
	
	 * Returns the first int from the  first column of the result of the SQL Query
	
	 */
	public static int returnSingleSQLQuery(IDOLegacyEntity entity, String SQLString) {
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		int recordCount = -1;
		try {
			conn = entity.getConnection(entity.getDatasource());
			stmt = conn.createStatement();
			rs = stmt.executeQuery(SQLString);
			if (rs.next())
				recordCount = rs.getInt(1);
			if (rs != null)
				rs.close();
			//System.out.println(SQLString+"\n");
		}
		catch (SQLException e) {
			System.err.println("There was an error in EntityControl.returnSingleSQLQuery " + e.getMessage());
			e.printStackTrace(System.err);
		}
		catch (Exception e) {
			System.err.println("There was an error in EntityControl.returnSingleSQLQuery " + e.getMessage());
			e.printStackTrace(System.err);
		}
		finally {
			if (stmt != null) {
				try {
					stmt.close();
				}
				catch (SQLException ex) {
					System.err.println("There was an error in EntityControl.returnSingleSQLQuery " + ex.getMessage());
					ex.printStackTrace(System.err);
				}
			}
			if (conn != null) {
				entity.freeConnection(entity.getDatasource(), conn);
			}
		}
		return recordCount;
	}
	/**
	 * Returns an int[] array out of an the first columns row of the result of the SQL Query
	 */
	public static int[] returnIntArraySQLQuery(IDOLegacyEntity entity, String SQLString) {
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
			int recordAt = -1;
			while (rs.next()) {
				try {
					recordAt = rs.getInt(1);
					recordCount[counter] = recordAt;
					counter++;
				}
				catch (ArrayIndexOutOfBoundsException ex) {
					size += 10;
					int[] newArr = new int[size];
					for (int i = 0; i < recordCount.length; i++) {
						newArr[i] = recordCount[i];
					}
					newArr[counter] = recordAt;
					recordCount = newArr;
				}
			}
			rs.close();
			if (size > counter) {
				int[] newArr = new int[counter];
				for (int i = 0; i < recordCount.length; i++) {
					newArr[i] = recordCount[i];
				}
				recordCount = newArr;
			}
			//System.out.println(SQLString+"\n");
		}
		catch (SQLException e) {
			System.err.println("There was an error in EntityControl.returnSingleSQLQuery " + e.getMessage());
			e.printStackTrace(System.err);
		}
		catch (Exception e) {
			System.err.println("There was an error in EntityControl.returnSingleSQLQuery " + e.getMessage());
			e.printStackTrace(System.err);
		}
		finally {
			if (stmt != null) {
				try {
					stmt.close();
				}
				catch (SQLException ex) {
					System.err.println("There was an error in EntityControl.returnSingleSQLQuery " + ex.getMessage());
					ex.printStackTrace(System.err);
				}
			}
			if (conn != null) {
				entity.freeConnection(entity.getDatasource(), conn);
			}
		}
		return recordCount;
	}
	public static void setAutoCreationOfEntities(boolean ifAutoCreate) {
		autoCreate = ifAutoCreate;
	}
	public static boolean getIfEntityAutoCreate() {
		return autoCreate;
	}
	/**
	 * Returns a list of IDOLegacyEntity Class objects that have N:1 relationship with entityClass
	 */
	public static List getNToOneRelatedClasses(Class entityClass) {
		return getNToOneRelatedClasses(com.idega.data.GenericEntity.getEntityInstance(entityClass));
	}
	/**
	 * Returns a list of IDOLegacyEntity Class objects that have N:1 relationship with entity
	 */
	public static List getNToOneRelatedClasses(IDOLegacyEntity entity) {
		List theReturn = new java.util.Vector();
		IDOEntityBean bean = (IDOEntityBean)entity;
		Collection attributes = bean.getAttributes();
		java.util.Iterator iter = attributes.iterator();
		while (iter.hasNext()) {
			EntityAttribute item = (EntityAttribute) iter.next();
			if (item.isOneToNRelationship()) {
				theReturn.add(item.getRelationShipClass());
			}
		}
		return theReturn;
	}
	protected static String getTableNameShortenedToThirtyCharacters(String originalTableName) {
		try {
			String theReturn = originalTableName.substring(0, Math.min(30, originalTableName.length()));
			return theReturn;
		}
		catch (NullPointerException ne) {
			throw new RuntimeException("EntityControl.getTableNameShortenedToThirtyCharacters(). originalTableName is null");
		}
	}
	public static EntityRelationship getManyToManyRelationShip(IDOLegacyEntity relatingEntity1, IDOLegacyEntity relatingEntity2) {
		String relatingEntityName1 = relatingEntity1.getEntityName();
		String relatingEntityName2 = relatingEntity2.getEntityName();
		return getManyToManyRelationShip(relatingEntityName1, relatingEntityName2);
	}
	protected static EntityRelationship getManyToManyRelationShip(String relatingEntityName1, String relatingEntityName2) {
		if (relationshipTables == null) {
			relationshipTables = new HashtableDoubleKeyed();
			return null;
		}
		EntityRelationship rel = (EntityRelationship) relationshipTables.get(relatingEntityName1, relatingEntityName2);
		if (rel == null) {
			return null;
		}
		else {
			return rel;
		}
		//return (String)relationshipTables.get(relatingEntityClassName1,relatingEntityClassName2);
	}
}
