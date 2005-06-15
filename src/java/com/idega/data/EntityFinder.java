// idega 2000 - Tryggvi Larusson
/*
 * Copyright 2000 idega.is All Rights Reserved.
 */

package com.idega.data;

import java.sql.SQLException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.ResultSetMetaData;
import java.util.List;
import java.util.Vector;
import java.util.HashMap;
import java.util.Collection;
import java.util.Map;
import com.idega.repository.data.Instantiator;
import com.idega.repository.data.RefactorClassRegistry;
import com.idega.repository.data.Singleton;
import com.idega.repository.data.SingletonRepository;
import com.idega.util.ListUtil;
import javax.ejb.FinderException;

/**
 * This class is deprecated in the IDO Framework. <br>All finders should be
 * inside ejbFind... methods in appropriate BMPBean class.
 * 
 * @author <a href="mailto:tryggvi@idega.is">Tryggvi Larusson</a>
 * @version 1.0
 */
public class EntityFinder implements Singleton {

	private static Instantiator instantiator = new Instantiator() { 
		
		public Object getInstance() { 
			return new EntityFinder();
		}
		
		public void unload() {
			debug = DEBUG_DEFAULT_VALUE;
		}
	};

	//The constructor should only be accessible to this class
	private EntityFinder() {
	}

	public static EntityFinder getInstance() {
		return (EntityFinder) SingletonRepository.getRepository().getInstance(EntityFinder.class, instantiator);
	}
	
	private static final boolean DEBUG_DEFAULT_VALUE = false;
	public static boolean debug = DEBUG_DEFAULT_VALUE;

	/**
	 * Returns null if there was no match
	 */
	public static List findAll(IDOLegacyEntity entity) throws SQLException {
		return findAll(entity, "select * from " + entity.getTableName());
	}

	/**
	 * Returns an empty List if there was no match
	 */
	public List findAll(Class entityClass) throws IDOFinderException {
		try {
			List theReturn = findAll(com.idega.data.GenericEntity.getStaticInstance(entityClass));
			if (theReturn == null) {
				return com.idega.util.ListUtil.getEmptyList();
			}
			return theReturn;
		}
		catch (SQLException sql) {
			throw new IDOFinderException(sql);
		}
	}

	/**
	 * Returns null if there was no match
	 */
	public static List findAll(IDOLegacyEntity entity, String SQLString) throws SQLException {
		return findAll(entity, SQLString, -1);
	}

	/**
	 * Returns an empty List if there was no match
	 */
	public List findAll(Class entityClass, String SQLString) throws IDOFinderException {
		try {
			List theReturn = findAll(com.idega.data.GenericEntity.getStaticInstance(entityClass), SQLString);
			if (theReturn == null) {
				return com.idega.util.ListUtil.getEmptyList();
			}
			return theReturn;
		}
		catch (SQLException sql) {
			throw new IDOFinderException(sql);
		}
	}
	public static List findAll(IDOLegacyEntity entity, String SQLString, int returningNumberOfRecords) throws SQLException {
		if (debug) {
			System.err.println("EntityFinder sql query for class :" + entity.getClass().getName() + ":");
			System.err.println(SQLString);
		}
		Class interfaceClass = IDOLookup.getInterfaceClassFor(entity.getClass());
		if (IDOContainer.getInstance().queryCachingActive(interfaceClass)) {
			try {
				Collection c = findAllUsingIDO(interfaceClass, SQLString, returningNumberOfRecords, entity.getDatasource());
				int size = c.size();
				//System.out.println("CollectionSize="+size);
				if (size > 0) {
					return ListUtil.convertCollectionToList(c);
				}
				else {
					return null;
				}
			}
			catch (FinderException fe) {
				fe.printStackTrace();
				throw new SQLException("EntityFinder.findAll(): " + fe.getMessage());
			}
		}
		else {
			return findAllLegacy((GenericEntity) entity, SQLString, returningNumberOfRecords);
		}
	}

	static Collection findAllUsingIDO(Class entityInterfaceClass, String SQLString, int returningNumberOfRecords, String datasource) throws FinderException {
		//return null;
		try {
			IDOFactory factory = (IDOFactory) IDOLookup.getHome(entityInterfaceClass);
			if (datasource != null) {
				factory = (IDOFactory) IDOLookup.getHome(entityInterfaceClass, datasource);
			}
			GenericEntity entityInstance = (GenericEntity) factory.idoCheckOutPooledEntity();
			Collection pks = entityInstance.idoFindIDsBySQL(SQLString, returningNumberOfRecords);
			factory.idoCheckInPooledEntity(entityInstance);
			return factory.getEntityCollectionForPrimaryKeys(pks);
		}
		catch (java.rmi.RemoteException rme) {
			throw new IDOFinderException(rme);
		}
	}

	/**
	 * Returns null if there was no match
	 */
	public static List findAllLegacy(GenericEntity entity, String SQLString, int returningNumberOfRecords) throws SQLException {
		Connection conn = null;
		Statement Stmt = null;
		ResultSetMetaData metaData;
		//Vector vector = new Vector();
		Vector vector = null;
		boolean check = true;
		//Vector theIDs = new Vector();
		try {
			conn = entity.getConnection();
			Stmt = conn.createStatement();
			ResultSet RS = Stmt.executeQuery(SQLString);
			metaData = RS.getMetaData();
			int count = 1;
			while (RS.next() && check) {
				count++;
				if (returningNumberOfRecords != -1) {
					if (count > returningNumberOfRecords) {
						check = false;
					}
				}

				IDOLegacyEntity tempobj = null;
				try {
					tempobj = (IDOLegacyEntity) entity.getClass().newInstance();
				}
				catch (Exception ex) {
					System.err.println("There was an error in com.idega.data.GenericEntity.findAll " + ex.getMessage());
					ex.printStackTrace(System.err);
				}
				if (tempobj != null) {
					String columnName = null;
					for (int i = 1; i <= metaData.getColumnCount(); i++) {

						//debug getting an object every time? that sucks
						// tryggvi ;)
						//columnName = metaData.getColumnName(i);

						//if ( RS.getObject(columnName) != null ){
							columnName = metaData.getColumnName(i);
							//this must be done if using AS in an sql query
							if ("".equals(columnName)) {
								columnName = metaData.getColumnLabel(i);
							}
							try{
								tempobj.fillColumn(columnName, RS);
							}
							catch(NullPointerException e){
								e.printStackTrace();
							}
						//}
					}
				}
				if (vector == null) {
					vector = new Vector();
				}
				((IDOLegacyEntity) tempobj).setDatasource(entity.getDatasource());
				((IDOLegacyEntity) tempobj).setEntityState(IDOLegacyEntity.STATE_IN_SYNCH_WITH_DATASTORE);

				vector.addElement(tempobj);

			}
			RS.close();
		}
		finally {
			if (Stmt != null) {
				Stmt.close();
			}
			if (conn != null) {
				entity.freeConnection(conn);
			}
		}
		/*
		 * for (Enumeration enum = theIDs.elements();enum.hasMoreElements();){
		 * Integer tempInt = (Integer) enum.nextElement();
		 * vector.addElement(new IDOLegacyEntity(tempInt.intValue()));
		 */
		if (vector != null) {
			vector.trimToSize();
			return vector;
			//return (IDOLegacyEntity[])
			// vector.toArray((Object[])java.lang.reflect.Array.newInstance(this.getClass(),0));
			//return vector.toArray(new IDOLegacyEntity[0]);
		}
		else {
			return null;
		}
	}

	/**
	 * Returns an empty List if there was no match
	 */
	public List findAll(Class entityClass, String SQLString, int returningNumberOfRecords) throws IDOFinderException {
		try {
			List theReturn = findAll(com.idega.data.GenericEntity.getStaticInstance(entityClass), SQLString, returningNumberOfRecords);
			if (theReturn == null) {
				return com.idega.util.ListUtil.getEmptyList();
			}
			return theReturn;
		}
		catch (SQLException sql) {
			throw new IDOFinderException(sql);
		}
	}

	/**
	 * Finds all instances of the fromEntity in the otherEntity, returns null
	 * if no match
	 */
	public static List findAssociated(IDOLegacyEntity fromEntity, IDOLegacyEntity otherEntity) throws SQLException {
		return findAll(
			otherEntity,
			"select * from "
				+ otherEntity.getTableName()
				+ " where "
				+ fromEntity.getIDColumnName()
				+ "= "
				+ GenericEntity.getKeyValueSQLString(fromEntity.getPrimaryKeyValue()));
	}

	/**
	 * Finds all instances of the fromEntity in the otherEntity, returns empty
	 * List if no match
	 */
	public List findAssociated(Class entityClass, Class otherEntityClass) throws IDOFinderException {
		try {
			List theReturn =
				findAssociated(
					com.idega.data.GenericEntity.getStaticInstance(entityClass),
					com.idega.data.GenericEntity.getStaticInstance(otherEntityClass));
			if (theReturn == null) {
				return com.idega.util.ListUtil.getEmptyList();
			}
			return theReturn;
		}
		catch (SQLException sql) {
			throw new IDOFinderException(sql);
		}
	}

	public static List findAssociatedOrdered(IDOLegacyEntity fromEntity, IDOLegacyEntity otherEntity, String column_name)
		throws SQLException {
		return findAll(
			otherEntity,
			"select * from "
				+ otherEntity.getTableName()
				+ " where "
				+ fromEntity.getIDColumnName()
				+ "= "
				+ GenericEntity.getKeyValueSQLString(fromEntity.getPrimaryKeyValue())
				+ "  order by "
				+ column_name
				+ "");
	}

	/**
	 * Finds all instances of the fromEntity in the otherEntity ordered by
	 * column "column_name", returns empty List if no match
	 */
	public List findAssociatedOrdered(Class entityClass, Class otherEntityClass, String column_name) throws IDOFinderException {
		try {
			List theReturn =
				findAssociatedOrdered(
					com.idega.data.GenericEntity.getStaticInstance(entityClass),
					com.idega.data.GenericEntity.getStaticInstance(otherEntityClass),
					column_name);
			if (theReturn == null) {
				return com.idega.util.ListUtil.getEmptyList();
			}
			return theReturn;
		}
		catch (SQLException sql) {
			throw new IDOFinderException(sql);
		}
	}

	public static List findAllOrdered(IDOLegacyEntity fromEntity, String orderByColumnName) throws SQLException {
		return findAll(fromEntity, "select * from " + fromEntity.getTableName() + " order by " + orderByColumnName);
	}

	/**
	 * Finds all instances of the entityClass ordered by column
	 * "orderByColumnName", returns empty List if no match
	 */
	public List findAllOrdered(Class entityClass, String orderByColumnName) throws IDOFinderException {
		try {
			List theReturn = findAllOrdered(com.idega.data.GenericEntity.getStaticInstance(entityClass), orderByColumnName);
			if (theReturn == null) {
				return com.idega.util.ListUtil.getEmptyList();
			}
			return theReturn;
		}
		catch (SQLException sql) {
			throw new IDOFinderException(sql);
		}
	}

	public static List findAllByColumnOrdered(IDOLegacyEntity fromEntity, String columnName, String toFind, String orderByColumnName)
		throws SQLException {
		return findAll(
			fromEntity,
			"select * from " + fromEntity.getTableName() + " where " + columnName + " like '" + toFind + "' order by " + orderByColumnName);
	}

	/**
	 * Finds all instances of the entityClass ordered by column
	 * "orderByColumnName", returns empty List if no match
	 */
	public List findAllByColumnOrdered(Class entityClass, String columnName, String toFind, String orderByColumnName)
		throws IDOFinderException {
		try {
			List theReturn =
				findAllByColumnOrdered(com.idega.data.GenericEntity.getStaticInstance(entityClass), columnName, toFind, orderByColumnName);
			if (theReturn == null) {
				return com.idega.util.ListUtil.getEmptyList();
			}
			return theReturn;
		}
		catch (SQLException sql) {
			throw new IDOFinderException(sql);
		}
	}

	public static List findAllByColumnOrdered(IDOLegacyEntity fromEntity, String columnName, int toFind, String orderByColumnName)
		throws SQLException {
		return findAll(
			fromEntity,
			"select * from " + fromEntity.getTableName() + " where " + columnName + " = " + toFind + " order by " + orderByColumnName);
	}

	/**
	 * Finds all instances of the entityClass ordered by column
	 * "orderByColumnName", returns empty List if no match
	 */
	public List findAllByColumnOrdered(Class entityClass, String columnName, int toFind, String orderByColumnName)
		throws IDOFinderException {
		try {
			List theReturn =
				findAllByColumnOrdered(com.idega.data.GenericEntity.getStaticInstance(entityClass), columnName, toFind, orderByColumnName);
			if (theReturn == null) {
				return com.idega.util.ListUtil.getEmptyList();
			}
			return theReturn;
		}
		catch (SQLException sql) {
			throw new IDOFinderException(sql);
		}
	}

	public static List findAllByColumnOrdered(
		IDOLegacyEntity fromEntity,
		String columnName1,
		String toFind1,
		String columnName2,
		String toFind2,
		String orderByColumnName)
		throws SQLException {
		return findAll(
			fromEntity,
			"select * from "
				+ fromEntity.getTableName()
				+ " where "
				+ columnName1
				+ " like '"
				+ toFind1
				+ "' and "
				+ columnName2
				+ " like '"
				+ toFind2
				+ "' order by "
				+ orderByColumnName);
	}

	/**
	 * Finds all instances of the entityClass ordered by column
	 * "orderByColumnName", returns empty List if no match
	 */
	public List findAllByColumnOrdered(
		Class entityClass,
		String columnName1,
		String toFind1,
		String columnName2,
		String toFind2,
		String orderByColumnName)
		throws IDOFinderException {
		try {
			List theReturn =
				findAllByColumnOrdered(
					com.idega.data.GenericEntity.getStaticInstance(entityClass),
					columnName1,
					toFind1,
					columnName2,
					toFind2,
					orderByColumnName);
			if (theReturn == null) {
				return com.idega.util.ListUtil.getEmptyList();
			}
			return theReturn;
		}
		catch (SQLException sql) {
			throw new IDOFinderException(sql);
		}
	}

	public static List findAllByColumnOrdered(
		IDOLegacyEntity fromEntity,
		String columnName1,
		String toFind1,
		String columnName2,
		String toFind2,
		String orderByColumnName,
		String condition1,
		String condition2,
		String criteria,
		String returnColumn)
		throws SQLException {
		return findAll(
			fromEntity,
			"select "
				+ criteria
				+ " "
				+ returnColumn
				+ " from "
				+ fromEntity.getTableName()
				+ " where "
				+ columnName1
				+ " "
				+ condition1
				+ " '"
				+ toFind1
				+ "' and "
				+ columnName2
				+ " "
				+ condition2
				+ " '"
				+ toFind2
				+ "' order by "
				+ orderByColumnName);
	}

	/**
	 * Finds all instances of the entityClass ordered by column
	 * "orderByColumnName", returns empty List if no match
	 */
	public List findAllByColumnOrdered(
		Class entityClass,
		String columnName1,
		String toFind1,
		String columnName2,
		String toFind2,
		String orderByColumnName,
		String condition1,
		String condition2,
		String criteria,
		String returnColumn)
		throws IDOFinderException {
		try {
			List theReturn =
				findAllByColumnOrdered(
					com.idega.data.GenericEntity.getStaticInstance(entityClass),
					columnName1,
					toFind1,
					columnName2,
					toFind2,
					orderByColumnName,
					condition1,
					condition2,
					criteria,
					returnColumn);
			if (theReturn == null) {
				return com.idega.util.ListUtil.getEmptyList();
			}
			return theReturn;
		}
		catch (SQLException sql) {
			throw new IDOFinderException(sql);
		}
	}

	public static List findAllByColumnDescendingOrdered(
		IDOLegacyEntity fromEntity,
		String columnName,
		String toFind,
		String orderByColumnName)
		throws SQLException {
		return findAll(
			fromEntity,
			"select * from "
				+ fromEntity.getTableName()
				+ " where "
				+ columnName
				+ " like '"
				+ toFind
				+ "' order by "
				+ orderByColumnName
				+ " desc");
	}

	/**
	 * Finds all instances of the entityClass descending ordered by column
	 * "columnName", returns empty List if no match
	 */
	public List findAllByColumnDescendingOrdered(Class entityClass, String columnName, String toFind, String orderByColumnName)
		throws IDOFinderException {
		try {
			List theReturn =
				findAllByColumnDescendingOrdered(
					com.idega.data.GenericEntity.getStaticInstance(entityClass),
					columnName,
					toFind,
					orderByColumnName);
			if (theReturn == null) {
				return com.idega.util.ListUtil.getEmptyList();
			}
			return theReturn;
		}
		catch (SQLException sql) {
			throw new IDOFinderException(sql);
		}
	}

	public static List findAllByColumnDescendingOrdered(
		IDOLegacyEntity fromEntity,
		String columnName1,
		String toFind1,
		String columnName2,
		String toFind2,
		String orderByColumnName)
		throws SQLException {
		return findAll(
			fromEntity,
			"select * from "
				+ fromEntity.getTableName()
				+ " where "
				+ columnName1
				+ " like '"
				+ toFind1
				+ "' and "
				+ columnName2
				+ " like '"
				+ toFind2
				+ "' order by "
				+ orderByColumnName
				+ " desc");
	}

	/**
	 * Finds all instances of the entityClass descending ordered by column
	 * "orderByColumnName", returns empty List if no match
	 */
	public List findAllByColumnDescendingOrdered(
		Class entityClass,
		String columnName1,
		String toFind1,
		String columnName2,
		String toFind2,
		String orderByColumnName)
		throws IDOFinderException {
		try {
			List theReturn =
				findAllByColumnDescendingOrdered(
					com.idega.data.GenericEntity.getStaticInstance(entityClass),
					columnName1,
					toFind1,
					columnName2,
					toFind2,
					orderByColumnName);
			if (theReturn == null) {
				return com.idega.util.ListUtil.getEmptyList();
			}
			return theReturn;
		}
		catch (SQLException sql) {
			throw new IDOFinderException(sql);
		}
	}

	public static List findAllDescendingOrdered(IDOLegacyEntity fromEntity, String orderByColumnName) throws SQLException {
		return findAll(fromEntity, "select * from " + fromEntity.getTableName() + " order by " + orderByColumnName + " desc");
	}

	/**
	 * Finds all instances of the entityClass descending ordered by column
	 * "orderByColumnName", returns empty List if no match
	 */
	public List findAllDescendingOrdered(Class entityClass, String orderByColumnName) throws IDOFinderException {
		try {
			List theReturn = findAllDescendingOrdered(com.idega.data.GenericEntity.getStaticInstance(entityClass), orderByColumnName);
			if (theReturn == null) {
				return com.idega.util.ListUtil.getEmptyList();
			}
			return theReturn;
		}
		catch (SQLException sql) {
			throw new IDOFinderException(sql);
		}
	}

	public static List findAllByColumn(IDOLegacyEntity fromEntity, String columnName, String toFind) throws SQLException {
		return findAll(fromEntity, "select * from " + fromEntity.getTableName() + " where " + columnName + " like '" + toFind + "'");
	}

	public static List findAllByColumnEquals(IDOLegacyEntity fromEntity, String columnName, String toFind) throws SQLException {
		return findAll(fromEntity, "select * from " + fromEntity.getTableName() + " where " + columnName + " = '" + toFind + "'");
	}

	/**
	 * Finds all instances of the entityClass where columnName==toFind, returns
	 * empty List if no match
	 */
	public List findAllByColumn(Class entityClass, String columnName, String toFind) throws IDOFinderException {
		try {
			List theReturn = findAllByColumn(com.idega.data.GenericEntity.getStaticInstance(entityClass), columnName, toFind);
			if (theReturn == null) {
				return com.idega.util.ListUtil.getEmptyList();
			}
			return theReturn;
		}
		catch (SQLException sql) {
			throw new IDOFinderException(sql);
		}
	}

	public static List findAllByColumn(IDOLegacyEntity fromEntity, String columnName, int toFind) throws SQLException {
		return findAll(fromEntity, "select * from " + fromEntity.getTableName() + " where " + columnName + "=" + toFind + "");
	}

	/**
	 * Finds all instances of the entityClass where columnName==toFind, returns
	 * empty List if no match
	 */
	public List findAllByColumn(Class entityClass, String columnName, int toFind) throws IDOFinderException {
		try {
			List theReturn = findAllByColumn(com.idega.data.GenericEntity.getStaticInstance(entityClass), columnName, toFind);
			if (theReturn == null) {
				return com.idega.util.ListUtil.getEmptyList();
			}
			return theReturn;
		}
		catch (SQLException sql) {
			throw new IDOFinderException(sql);
		}
	}

	public static List findAllByColumn(IDOLegacyEntity fromEntity, String columnName1, String toFind1, String columnName2, String toFind2)
		throws SQLException {
		//System.out.println("select * from "+fromEntity.getTableName()+"
		// where "+columnName1+" like '"+toFind1+"' and "+columnName2+" like
		// '"+toFind2+"'");
		return findAll(
			fromEntity,
			"select * from "
				+ fromEntity.getTableName()
				+ " where "
				+ columnName1
				+ " like '"
				+ toFind1
				+ "' and "
				+ columnName2
				+ " like '"
				+ toFind2
				+ "'");
	}
	public static List findAllByColumnEquals(IDOLegacyEntity fromEntity, String columnName1, String toFind1, String columnName2, String toFind2)
		throws SQLException {
		//System.out.println("select * from "+fromEntity.getTableName()+"
		// where "+columnName1+" like '"+toFind1+"' and "+columnName2+" like
		// '"+toFind2+"'");
		return findAll(
			fromEntity,
			"select * from "
				+ fromEntity.getTableName()
				+ " where "
				+ columnName1
				+ " = '"
				+ toFind1
				+ "' and "
				+ columnName2
				+ " = '"
				+ toFind2
				+ "'");
	}

	public static List findAllByColumn(IDOLegacyEntity fromEntity, String columnName1, String toFind1, String columnName2, int toFind2)
		throws SQLException {
		//System.out.println("select * from "+fromEntity.getTableName()+"
		// where "+columnName1+" like '"+toFind1+"' and "+columnName2+" like
		// '"+toFind2+"'");
		return findAll(
			fromEntity,
			"select * from "
				+ fromEntity.getTableName()
				+ " where "
				+ columnName1
				+ " like '"
				+ toFind1
				+ "' and "
				+ columnName2
				+ " = "
				+ toFind2);
	}

	public static List findAllByColumn(IDOLegacyEntity fromEntity, String columnName1, int toFind1, String columnName2, int toFind2)
		throws SQLException {
		//System.out.println("select * from "+fromEntity.getTableName()+"
		// where "+columnName1+" like '"+toFind1+"' and "+columnName2+" like
		// '"+toFind2+"'");
		return findAll(
			fromEntity,
			"select * from "
				+ fromEntity.getTableName()
				+ " where "
				+ columnName1
				+ " = "
				+ toFind1
				+ " and "
				+ columnName2
				+ " = "
				+ toFind2);
	}

	public static List findAllByColumn(IDOLegacyEntity fromEntity, String columnName1, int toFind1, String columnName2, String toFind2)
	throws SQLException {
	//System.out.println("select * from "+fromEntity.getTableName()+"
	// where "+columnName1+" like '"+toFind1+"' and "+columnName2+" like
	// '"+toFind2+"'");
	return findAll(
		fromEntity,
		"select * from "
			+ fromEntity.getTableName()
			+ " where "
			+ columnName1
			+ " = "
			+ toFind1
			+ " and "
			+ columnName2
			+ " = '"
			+ toFind2
			+ "'");
	}

	/**
	 * Finds all instances of the entityClass where columnName1==toFind1 and
	 * columnName2==toFind2, returns empty List if no match
	 */
	public List findAllByColumn(Class entityClass, String columnName1, String toFind1, String columnName2, String toFind2)
		throws IDOFinderException {
		try {
			List theReturn =
				findAllByColumn(com.idega.data.GenericEntity.getStaticInstance(entityClass), columnName1, toFind1, columnName2, toFind2);
			if (theReturn == null) {
				return com.idega.util.ListUtil.getEmptyList();
			}
			return theReturn;
		}
		catch (SQLException sql) {
			throw new IDOFinderException(sql);
		}
	}

	public static List findAllByColumn(
		IDOLegacyEntity fromEntity,
		String columnName1,
		String toFind1,
		String columnName2,
		String toFind2,
		String columnName3,
		String toFind3)
		throws SQLException {
		return findAll(
			fromEntity,
			"select * from "
				+ fromEntity.getTableName()
				+ " where "
				+ columnName1
				+ " like '"
				+ toFind1
				+ "' and "
				+ columnName2
				+ " like '"
				+ toFind2
				+ "' and "
				+ columnName3
				+ " like '"
				+ toFind3
				+ "'");
	}

	public static List findAllByColumnEquals(
			IDOLegacyEntity fromEntity,
			String columnName1,
			String toFind1,
			String columnName2,
			String toFind2,
			String columnName3,
			String toFind3)
			throws SQLException {
			return findAll(
				fromEntity,
				"select * from "
					+ fromEntity.getTableName()
					+ " where "
					+ columnName1
					+ " = '"
					+ toFind1
					+ "' and "
					+ columnName2
					+ " = '"
					+ toFind2
					+ "' and "
					+ columnName3
					+ " = '"
					+ toFind3
					+ "'");
		}

	/**
	 * Finds all instances of the entityClass where columnName1==toFind1 and
	 * columnName2==toFind2, and columnName3==toFind3 returns empty List if no
	 * match
	 */
	public List findAllByColumn(
		Class entityClass,
		String columnName1,
		String toFind1,
		String columnName2,
		String toFind2,
		String columnName3,
		String toFind3)
		throws IDOFinderException {
		try {
			List theReturn =
				findAllByColumn(
					com.idega.data.GenericEntity.getStaticInstance(entityClass),
					columnName1,
					toFind1,
					columnName2,
					toFind2,
					columnName3,
					toFind3);
			if (theReturn == null) {
				return com.idega.util.ListUtil.getEmptyList();
			}
			return theReturn;
		}
		catch (SQLException sql) {
			throw new IDOFinderException(sql);
		}
	}

	public static List findReverseRelated(IDOLegacyEntity fromEntity, IDOLegacyEntity returningEntity) throws SQLException {
		String tableToSelectFrom = EntityControl.getNameOfMiddleTable(fromEntity, returningEntity);
		String SQLString =
			"select * from "
				+ tableToSelectFrom
				+ " where "
				+ fromEntity.getIDColumnName()
				+ "="
				+ GenericEntity.getKeyValueSQLString(fromEntity.getPrimaryKeyValue());
		return findRelated(fromEntity, returningEntity, SQLString);
	}

	/**
	 * Returns null if nothing found
	 */
	protected static List findRelated(IDOLegacyEntity fromEntity, IDOLegacyEntity returningEntity, String SQLString) throws SQLException {
		if (debug) {
			System.err.println("EntityFinder : findRelated :");
			System.err.println(SQLString);
		}

		Connection conn = null;
		Statement Stmt = null;
		//Vector vector = new Vector();
		Vector vector = null;
		/*
		 * String tableToSelectFrom = ""; if
		 * (returningEntity.getTableName().endsWith("_")){ tableToSelectFrom =
		 * returningEntity.getTableName()+fromEntity.getTableName(); } else{
		 * tableToSelectFrom =
		 * returningEntity.getTableName()+"_"+fromEntity.getTableName();
		 */

		try {
			conn = fromEntity.getConnection();
			Stmt = conn.createStatement();
			ResultSet RS = Stmt.executeQuery(SQLString);
			while (RS != null && RS.next()) {

				IDOLegacyEntity tempobj = null;
				try {
					tempobj = (IDOLegacyEntity) returningEntity.getClass().newInstance();
					if (debug) {
						System.err.println("Classname " + returningEntity.getClass().getName());
						System.err.println(
							"Entity " + returningEntity.getEntityName() + " IdColumnname " + returningEntity.getIDColumnName());
					}
					int id = RS.getInt(returningEntity.getIDColumnName());
					tempobj.setDatasource(fromEntity.getDatasource());
					tempobj.findByPrimaryKey(id);
				}
				catch (Exception ex) {

					System.err.println(
						"There was an error in com.idega.data.GenericEntity.findRelated(IDOLegacyEntity returningEntity,String SQLString): "
							+ ex.getMessage());
					ex.printStackTrace();

				}
				if (vector == null) {
					vector = new Vector();
				}
				vector.addElement(tempobj);

			}
			RS.close();

		}
		finally {
			if (Stmt != null) {
				Stmt.close();
			}
			if (conn != null) {
				fromEntity.freeConnection(conn);
			}
		}

		if (vector != null) {
			vector.trimToSize();
			//return (IDOLegacyEntity[])
			// vector.toArray((Object[])java.lang.reflect.Array.newInstance(returningEntity.getClass(),0));
			//return vector.toArray(new IDOLegacyEntity[0]);
			return vector;
		}
		else {
			return null;
		}
	}
	
	/**
	 * Returns an sql query like: 'select * from PO_POLL_IC_OBJECT_INSTANCE where IC_OBJECT_INSTANCE_ID=3565'
	 * @param fromEntity
	 * @param returningEntityClass
	 * @return
	 */
	public String getFindRelatedSQLQuery(IDOEntity fromEntity,Class returningEntityClass){
	    IDOEntity returningEntityInstance = GenericEntity.getStaticInstanceIDO(returningEntityClass);
		String tableToSelectFrom = EntityControl.getNameOfMiddleTable(returningEntityInstance, fromEntity);
		StringBuffer buffer = new StringBuffer();
		buffer.append("select * from ");
		buffer.append(tableToSelectFrom);
		buffer.append(" where ");
		try {
            buffer.append(fromEntity.getEntityDefinition().getPrimaryKeyDefinition().getField().getSQLFieldName());
        } catch (IDOCompositePrimaryKeyException e) {
           System.err.println("Primary Key is composite for :"+fromEntity.getClass().toString());
           e.printStackTrace();
        }
		buffer.append("=");
		buffer.append(GenericEntity.getKeyValueSQLString(fromEntity.getPrimaryKey()));
		//buffer.append(" order by ");
		//buffer.append(fromEntity.getIDColumnName());
		String SQLString = buffer.toString();
		return SQLString;
	}

	public static List findRelated(IDOLegacyEntity fromEntity, IDOLegacyEntity returningEntity) throws SQLException {
		/*String tableToSelectFrom = EntityControl.getNameOfMiddleTable(returningEntity, fromEntity);
		StringBuffer buffer = new StringBuffer();
		buffer.append("select * from ");
		buffer.append(tableToSelectFrom);
		buffer.append(" where ");
		buffer.append(fromEntity.getIDColumnName());
		buffer.append("=");
		buffer.append(GenericEntity.getKeyValueSQLString(fromEntity.getPrimaryKeyValue()));
		//buffer.append(" order by ");
		//buffer.append(fromEntity.getIDColumnName());
		String SQLString = buffer.toString();
		//String SQLString="select * from "+tableToSelectFrom+" where
		// "+fromEntity.getIDColumnName()+"="+GenericEntity.getKeyValueSQLString(fromEntity.getPrimaryKeyValue());
		//System.out.println("FindRelated SQLString="+SQLString+"crap");*/
	    String SQLString = getInstance().getFindRelatedSQLQuery(fromEntity,returningEntity.getClass());
		return findRelated(fromEntity, returningEntity, SQLString);
	}

	/**
	 * Finds all instances of the returningEntityClass where it is associated
	 * with the fromEntity and the column criteria matches the returningEntity,
	 * returns empty List if no match
	 */
	public List findRelated(
		IDOLegacyEntity fromEntity,
		Class returningEntityClass,
		String returningEntityColumnName,
		String returningEntityColumnValue)
		throws IDOFinderException {
		IDOLegacyEntity returningEntity = com.idega.data.GenericEntity.getStaticInstance(returningEntityClass);
		String tableToSelectFrom = EntityControl.getNameOfMiddleTable(returningEntity, fromEntity);
		String returnTableName = returningEntity.getEntityName();
		StringBuffer buffer = new StringBuffer();
		buffer.append("select * from ");
		buffer.append(tableToSelectFrom).append(",").append(returnTableName);
		buffer.append(" where ");
		buffer.append(tableToSelectFrom).append(".").append(fromEntity.getIDColumnName());
		buffer.append("=");
		buffer.append(GenericEntity.getKeyValueSQLString(fromEntity.getPrimaryKeyValue()));
		buffer.append(" and ");
		buffer.append(tableToSelectFrom).append(".").append(returningEntity.getIDColumnName());
		buffer.append("=");
		buffer.append(returnTableName).append(".").append(returningEntity.getIDColumnName());
		buffer.append(" and ");
		buffer.append(returnTableName).append(".").append(returningEntityColumnName);
		buffer.append("=");
		buffer.append(returningEntityColumnValue);
		String SQLString = buffer.toString();
		//String SQLString="select * from "+tableToSelectFrom+" where
		// "+fromEntity.getIDColumnName()+"="+GenericEntity.getKeyValueSQLString(fromEntity.getPrimaryKeyValue());
		//System.out.println("FindRelated SQLString="+SQLString+"crap");
		try {
			return findRelated(fromEntity, returningEntity, SQLString);
		}
		catch (SQLException sql) {
			throw new IDOFinderException(sql);
		}
	}
	/**
	 * Finds all instances of the returningEntityClass where it is associated
	 * with the fromEntity, returns empty List if no match
	 */
	public List findRelated(IDOLegacyEntity fromEntity, Class returningEntityClass) throws IDOFinderException {
		try {
			List theReturn = findRelated(fromEntity, com.idega.data.GenericEntity.getStaticInstance(returningEntityClass));
			if (theReturn == null) {
				return com.idega.util.ListUtil.getEmptyList();
			}
			return theReturn;
		}
		catch (SQLException sql) {
			throw new IDOFinderException(sql);
		}
	}

	public static List findNonRelated(IDOLegacyEntity fromEntity, IDOLegacyEntity returningEntity) {
		try {
			String tableToSelectFrom = EntityControl.getNameOfMiddleTable(returningEntity, fromEntity);

			StringBuffer buffer = new StringBuffer();
			buffer.append("select " + returningEntity.getTableName() + ".* from ");
			buffer.append(returningEntity.getTableName());
			buffer.append(" where ");
			buffer.append(returningEntity.getIDColumnName());
			buffer.append(" not in (select " + returningEntity.getIDColumnName() + " from ");
			buffer.append(tableToSelectFrom);
			buffer.append(")");
			String SQLString = buffer.toString();
			return findRelated(fromEntity, returningEntity, SQLString);
		}
		catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Finds all instances of the returningEntityClass where it is has a
	 * relationship (n-to-n, where n=[1..x]) with the fromEntity, returns empty
	 * List if no match
	 */
	public List findNonRelated(IDOLegacyEntity fromEntity, Class returningEntityClass) throws IDOFinderException {
		try {
			List theReturn = findRelated(fromEntity, com.idega.data.GenericEntity.getStaticInstance(returningEntityClass));
			if (theReturn == null) {
				return com.idega.util.ListUtil.getEmptyList();
			}
			return theReturn;
		}
		catch (SQLException sql) {
			throw new IDOFinderException(sql);
		}
	}

	/**
	 * If ascending==true ordering is descending, else it is ascending
	 */
	public static List findRelatedOrdered(
		IDOLegacyEntity fromEntity,
		IDOLegacyEntity returningEntity,
		String returningEntityColumnToOrderBy,
		boolean ascending)
		throws SQLException {
		String fromTable = fromEntity.getTableName();
		String middleTable = EntityControl.getNameOfMiddleTable(returningEntity, fromEntity);
		String returningTable = returningEntity.getTableName();
		String comma = ",";
		String dot = ".";
		StringBuffer buffer = new StringBuffer();
		buffer.append("select ");
		buffer.append(returningTable);
		buffer.append(dot);
		buffer.append("* from ");
		buffer.append(middleTable);
		buffer.append(comma);
		buffer.append(returningTable);
		buffer.append(comma);
		buffer.append(fromTable);
		buffer.append(" where ");
		buffer.append(middleTable);
		buffer.append(dot);
		buffer.append(fromEntity.getIDColumnName());
		buffer.append("=");
		buffer.append(GenericEntity.getKeyValueSQLString(fromEntity.getPrimaryKeyValue()));
		buffer.append(" and ");
		buffer.append(fromTable);
		buffer.append(dot);
		buffer.append(fromEntity.getIDColumnName());
		buffer.append("=");
		buffer.append(middleTable);
		buffer.append(dot);
		buffer.append(fromEntity.getIDColumnName());
		buffer.append(" and ");
		buffer.append(middleTable);
		buffer.append(dot);
		buffer.append(returningEntity.getIDColumnName());
		buffer.append("=");
		buffer.append(returningTable);
		buffer.append(dot);
		buffer.append(returningEntity.getIDColumnName());
		buffer.append(" order by ");
		buffer.append(returningTable);
		buffer.append(".");
		buffer.append(returningEntityColumnToOrderBy);
		if (ascending) {
			buffer.append(" asc");
		}
		else {
			buffer.append(" desc");
		}
		String SQLString = buffer.toString();
		return findAll(returningEntity, SQLString);
	}

	/**
	 * Finds all instances of the returningEntityClass where it is has a
	 * relationship (n-to-n, where n=[1..x]) with the fromEntity ordered by
	 * column "returningEntityColumnToOrderBy" , if ascending==true ordering is
	 * descending, else it is ascending, returns empty List if no match
	 */
	public List findRelatedOrdered(
		IDOLegacyEntity fromEntity,
		Class returningEntityClass,
		String returningEntityColumnToOrderBy,
		boolean ascending)
		throws IDOFinderException {
		try {
			List theReturn =
				findRelatedOrdered(
					fromEntity,
					com.idega.data.GenericEntity.getStaticInstance(returningEntityClass),
					returningEntityColumnToOrderBy,
					ascending);
			if (theReturn == null) {
				return com.idega.util.ListUtil.getEmptyList();
			}
			return theReturn;
		}
		catch (SQLException sql) {
			throw new IDOFinderException(sql);
		}
	}

	public static IDOLegacyEntity findByPrimaryKey(String entityClassName, int primaryKeyID) throws Exception {
		IDOLegacyEntity entity = (IDOLegacyEntity) RefactorClassRegistry.forName(entityClassName).newInstance();
		return findByPrimaryKey(entity, primaryKeyID);

	}

	public static IDOLegacyEntity findByPrimaryKey(IDOLegacyEntity entity, int primaryKeyID) throws Exception {

		Connection conn = null;
		Statement Stmt = null;
		try {
			conn = entity.getConnection();
			Stmt = conn.createStatement();
			StringBuffer buffer = new StringBuffer();
			buffer.append("select ");
			//buffer.append("* from ");
			buffer.append(
				DatastoreInterface.getInstance((GenericEntity) entity).getCommaDelimitedColumnNamesForSelect((GenericEntity) entity));
			buffer.append(" from ");
			buffer.append(entity.getTableName());
			buffer.append(" where ");
			buffer.append(entity.getIDColumnName());
			buffer.append("=");
			buffer.append(primaryKeyID);

			ResultSet RS = Stmt.executeQuery(buffer.toString());

			//ResultSet RS = Stmt.executeQuery("select * from
			// "+getTableName()+" where "+getIDColumnName()+"="+id);

			RS.next();
			String[] columnNames = entity.getColumnNames();
			for (int i = 0; i < columnNames.length; i++) {
				try {
					if (RS.getString(columnNames[i]) != null) {
						entity.fillColumn(columnNames[i], RS);
					}
				}
				catch (SQLException ex) {
					//NOCATH
					try {
						if (RS.getString(columnNames[i].toUpperCase()) != null) {
							entity.fillColumn(columnNames[i], RS);
						}
					}
					catch (SQLException exe) {
						try {
							if (RS.getString(columnNames[i].toLowerCase()) != null) {
								entity.fillColumn(columnNames[i], RS);
							}
						}
						catch (SQLException exep) {
							System.err.println(
								"Exception in IDOLegacyEntity findByPrimaryKey, RS.getString() not found: " + exep.getMessage());
							exep.printStackTrace(System.err);
						}
					}

				}

			}
			RS.close();

		}
		finally {
			if (Stmt != null) {
				Stmt.close();
			}
			if (conn != null) {
				entity.freeConnection(conn);
			}
		}
		if (GenericEntity.isColumnValueNotEmpty(GenericEntity.getKeyValueSQLString(entity.getPrimaryKeyValue()))) {
			return entity;
		}
		return null;
	}

	/**
	 * Returns a Map of Entity values keyed by string presentation of specified
	 * columnname values,returns null if collection is null or empty
	 *  
	 */
	public Map getMapOfEntity(Collection c, String keyColumnName) {
		if (c != null) {
			java.util.Iterator iter = c.iterator();
			HashMap map = new HashMap(c.size());
			while (iter.hasNext()) {
				IDOLegacyEntity entity = (IDOLegacyEntity) iter.next();
				map.put(entity.getColumnValue(keyColumnName).toString(), entity);
			}
			return map;
		}
		return null;
	}

}
