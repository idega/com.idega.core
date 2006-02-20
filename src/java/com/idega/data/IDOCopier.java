package com.idega.data;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
//import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import java.util.ArrayList;

/**
 * Title:        A class to copy the IDO data objects persisted in one datasource to another.
 * Description:
 * Copyright:    Copyright (c) 2001-2002 idega software
 *  Company:     idega software
 *@author <a href="mailto:tryggvi@idega.is">Tryggvi Larusson</a>
 *@version 1.0
 */
public class IDOCopier {
	private GenericEntity fromEntity;
	private String toDataSourceName;
	private GenericEntity toEntity;
	private List entityToCopyList;
	private List entityRelationshipInfos = new ArrayList();
	//private List copiedEntityClasses=new Vector();
	private List copiedEntites = new ArrayList();
	private boolean copySameTableOnlyOnce = false;
	
	
	protected IDOCopier() {
	}
	private IDOCopier(GenericEntity entity) {
		this.fromEntity = entity;
		addEntityToCopy(entity);
	}
	public static IDOCopier getCopierInstance() {
		return new IDOCopier();
	}
	public static IDOCopier getCopierInstance(GenericEntity entity) {
		return new IDOCopier(entity);
	}
	public void setToDatasource(String dataSourceName) {
		this.toDataSourceName = dataSourceName;
	}
	public void addEntityToCopy(GenericEntity entity) {
		if (fromEntity == null) {
			fromEntity = entity;
		}
		getEntityCopyList().add(entity);
	}
	private List getEntityCopyList() {
		if (entityToCopyList == null) {
			entityToCopyList = new Vector();
		}
		return entityToCopyList;
	}
	public String getToDatasource() {
		return toDataSourceName;
	}
	public void copyAllData() {
		try {
			//toEntity = (GenericEntity)fromEntity.getClass().newInstance();
			toEntity = this.createEntityInstance(fromEntity);
			toEntity.setDatasource(getToDatasource());
			toEntity.setToInsertStartData(false);
			IDODependencyList dep = IDODependencyList.createDependencyList();
			dep.addAllEntityClasses(this.getEntityCopyList());
			dep.compile();
			List depList = dep.getDependencyListAsClasses();
			//List depList = compileDependencyList(com.idega.core.data.ICFile.class);
			//List depList = compileDependencyList(com.idega.core.user.data.User.class);
			Iterator iter = com.idega.util.ListUtil.reverseList(depList).iterator();
			while (iter.hasNext()) {
				Class item = (Class) iter.next();
				//out.println(item.getName()+"\n<br>");
				try {
					GenericEntity toInstance = this.createEntityInstance(item);
					//GenericEntity toInstance = (GenericEntity)item.newInstance();
					toInstance.setDatasource(this.getToDatasource());
					toInstance.setToInsertStartData(false);
					//GenericEntity fromInstance = (GenericEntity)item.newInstance();
					GenericEntity fromInstance = this.createEntityInstance(item);
					fromInstance.setDatasource(this.fromEntity.getDatasource());
					DatastoreInterface.getInstance(toInstance).createEntityRecord(toInstance);
					copyAllData(fromInstance, toInstance, true);
					copyManyToManyData(fromInstance, toInstance);
				}
				catch (java.lang.IllegalAccessException illae) {
					//illae.printStackTrace();
				}
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	private void copyAllData(GenericEntity fromInstance, GenericEntity toInstance, boolean maintainIDs) {
		try {
			IDOEntityCopyInfo info = getIDOEntityCopyInfo(fromInstance.getClass(), fromInstance.getTableName());
			if (!isTableAlreadyCopied(info)) {
				System.out.println("[idoCopier] Copying data for " + fromInstance.getClass().getName());
				List l = null;
				if (fromInstance instanceof com.idega.core.builder.data.ICPage) {
					/**
					*  @todo change - Shitty mix change this as soon as possible!!!
					*
					*/
					List l1 = EntityFinder.findAll((IDOLegacyEntity)fromInstance, "select * from ib_page where template_id is null");
					l = new Vector();
					l.addAll(l1);
					List l2 =
						EntityFinder.findAll((IDOLegacyEntity)
							fromInstance,
							"select * from ib_page where template_id is not null order by template_id asc,ib_page_id asc");
					l.addAll(l2);
				}
				else {
					l = EntityFinder.findAll((IDOLegacyEntity)fromInstance);
				}
				if (l != null) {
					int highestID = 1;
					Iterator iter = l.iterator();
					while (iter.hasNext()) {
						GenericEntity tempEntity = (GenericEntity) iter.next();
						String originalDatasource = tempEntity.getDatasource();
						tempEntity.setDatasource(this.getToDatasource());
						if (!maintainIDs) {
							/**
							 * @todo: Implement
							 */
						}
						try {
							tempEntity.insert();
						}
						catch (SQLException e) {
							System.err.println(
								"Error in inserting "
									+ tempEntity.getClass().getName()
									+ " for id="
									+ tempEntity.getID()
									+ " Message: "
									+ e.getMessage());
						}
						try {
							if (tempEntity.getID() > highestID) {
								highestID = tempEntity.getID();
								//  DatastoreInterface.getInstance(tempEntity).setSequenceValue(tempEntity,highestID);
							}
						}
						catch (ClassCastException e) {
							//e.printStackTrace();
							System.err.println("ClassCastException: " + e.getMessage());
						}
						tempEntity.setDatasource(originalDatasource);
					}
					updateNumberGeneratorValue(toInstance, highestID);	
				}
				this.addToCopiedEntityList(fromInstance);
			}
			else {
				System.out.println(
					"[idoCopier] Skipping copying data for " + fromInstance.getClass().getName() + " as table has already been copied");
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	private void copyManyToManyData(GenericEntity fromInstance, GenericEntity toInstance) {
		//addToCopiedEntityList(fromInstance);
		List infoList = getManyToManyRelatedAndCopied(fromInstance);
		Iterator iter = infoList.iterator();
		while (iter.hasNext()) {
			IDOEntityRelationshipCopyInfo info = (IDOEntityRelationshipCopyInfo) iter.next();
			if (!info.copied) {

				Connection toConn = null;
				Connection fromConn = null;
				try {
					toConn = this.toEntity.getConnection();
					fromConn = fromEntity.getConnection();
					String fromTableName = getCrossTableName(info.relation,fromConn);
					String toTableName = getCrossTableName(info.relation,toConn);
					this.copyManyToManyData(fromTableName,toTableName, fromConn, toConn);
					info.copied = true;
				}
				catch (SQLException e) {
					e.printStackTrace();
				}
				finally {
					if (toConn != null) {
						toEntity.freeConnection(toConn);
					}
					if (fromConn != null) {
						fromEntity.freeConnection(fromConn);
					}
				}
				
			}
		}
	}
	/**
	 * Method getCrosTableName.
	 * @param entityRelationship
	 * @param fromConn
	 * @return String
	 */
	private String getCrossTableName(EntityRelationship entityRelationship, Connection fromConn) {
		/**
		 * Workaround implementation
		 * @todo Implement this in a more elegant way
		 */
		if(IDOCopier.isDatasourceOracle(fromConn)){
			/**
			 * Returns the possibly truncated name of the table
			 */
			return EntityControl.getCheckedRelatedTableName(entityRelationship.getTableName());
		}
		else{
			/**
			 * Returns the originally set "longer" set name of the table
			 */
			return entityRelationship.getSetTableName();			
		}
	}
	private IDOEntityCopyInfo addToCopiedEntityList(GenericEntity entity) {
		IDOEntityCopyInfo cInfo = getIDOEntityCopyInfo(entity.getClass(), entity.getTableName());
		copiedEntites.add(cInfo);
		//copiedEntityClasses.add(entity.getClass());
		List relations = EntityControl.getManyToManyRelationShips(entity);
		if (relations != null) {
			Iterator iter = relations.iterator();
			while (iter.hasNext()) {
				boolean areAllReferencedEntitiesCopied = true;
				EntityRelationship item = (EntityRelationship) iter.next();
				Collection referencedClasses = item.getColumnsAndReferencingClasses().values();
				Iterator classIter = referencedClasses.iterator();
				while (classIter.hasNext()) {
					Class classToCheck = (Class) classIter.next();
					IDOEntityCopyInfo cInfoToCheck = getIDOEntityCopyInfo(classToCheck);
					if (copiedEntites.contains(cInfoToCheck) && areAllReferencedEntitiesCopied) {
						//if(copiedEntityClasses.contains(classToCheck)&&areAllReferencedEntitiesCopied){
						//areAllReferencedEntitiesCopied still kept true
						areAllReferencedEntitiesCopied = true;
					}
					else {
						areAllReferencedEntitiesCopied = false;
					}
				}
				if (areAllReferencedEntitiesCopied) {
					IDOEntityRelationshipCopyInfo info = new IDOEntityRelationshipCopyInfo();
					info.relation = item;
					if (!entityRelationshipInfos.contains(info)) {
						entityRelationshipInfos.add(info);
					}
				}
			}
		}
		return cInfo;
	}
	/**
	 *   Returns a List of IDOEntityRelationshipCopyInfo Objects
	 */
	private List getManyToManyRelatedAndCopied(GenericEntity entity) {
		return entityRelationshipInfos;
	}
	private void copyManyToManyData(String fromTableName,String toTableName, Connection fromConnection, Connection toConnection) throws SQLException{
		System.out.println("[idoCopier] Copying data from cross-table: " + fromTableName+" to cross-table: " + toTableName);
		Statement stmt = null;
		PreparedStatement ps = null;
		ResultSet RS = null;
		try {
			stmt = fromConnection.createStatement();
			ps = toConnection.prepareStatement("insert into " + toTableName + " values(?,?)");
			RS = stmt.executeQuery("select * from " + fromTableName);
			//ResultSetMetaData rsm = RS.getMetaData();
			//int columnCount = rsm.getColumnCount();
			while (RS.next()) {
				//try {
					int i1 = RS.getInt(1);
					int i2 = RS.getInt(2);
					ps.setInt(1, i1);
					ps.setInt(2, i2);
					ps.executeUpdate();
				//}
				//catch (SQLException e) {
				//	//e.printStackTrace();
				//	System.err.println(e.getMessage());
				//	throw new SQLException();
				//}
			}
		}
		//catch (SQLException e) {
		//	e.printStackTrace();
		//}
		finally {
			if (RS != null) {
				try {
					RS.close();
				}
				catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (stmt != null) {
				try {
					stmt.close();
				}
				catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (ps != null) {
				try {
					ps.close();
				}
				catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	protected IDOEntityRelationshipCopyInfo getIDOEntityRelationshipCopyInfoInstance(){
		return new IDOEntityRelationshipCopyInfo();	
	}

	protected IDOEntityCopyInfo getIDOEntityCopyInfo(Class entityClass){
		GenericEntity entity = this.createEntityInstance(entityClass);
		String tableName = entity.getTableName();
		return getIDOEntityCopyInfo(entityClass,tableName);
	}

	protected IDOEntityCopyInfo getIDOEntityCopyInfo(Class entityClass, String tableName){
		return new IDOEntityCopyInfo(entityClass,tableName);	
	}
	
	protected class IDOEntityRelationshipCopyInfo {
		EntityRelationship relation;
		boolean copied = false;
		/*String fromTableName;
		String toTableName;
		
		IDOEntityRelationshipCopyInfo(EntityRelationship aRelation,String fromTableName,String toTableName){
			relation=aRelation;
			this.fromTableName=fromTableName;
			this.toTableName=toTableName;
		}*/
		public boolean equals(Object o) {
			if (o != null) {
				if (o instanceof IDOEntityRelationshipCopyInfo) {
					return ((IDOEntityRelationshipCopyInfo) o).relation.equals(this.relation);
				}
			}
			return false;
		}
	}
	protected class IDOEntityCopyInfo {
		protected IDOEntityCopyInfo(Class entityClass, String tableName) {
			this.entityClass = entityClass;
			this.tableName = tableName;
		}
		public Class entityClass;
		public String tableName;
		public boolean equals(Object o) {
			if (o != null) {
				if (o instanceof IDOEntityCopyInfo) {
					return ((IDOEntityCopyInfo) o).tableName.equals(this.tableName);
				}
				else if (o instanceof Class) {
					return ((Class) o).equals(this.entityClass);
				}
				else if (o instanceof String) {
					return ((String) o).equals(this.tableName);
				}
			}
			return false;
		}
	}
	private void updateNumberGeneratorValue(GenericEntity entity, int highestValue) {
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		int valueToSet = highestValue;
		try {
			conn = entity.getConnection();
			stmt = conn.createStatement();
			rs = stmt.executeQuery("select max(" + entity.getIDColumnName() + ") from " + entity.getTableName());
			rs.next();
			int i = rs.getInt(1);
			if (i > valueToSet) {
				valueToSet = i;
			}
			rs.close();
			stmt.close();
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
		finally {
			if (conn != null) {
				entity.freeConnection(conn);
			}
		}
		DatastoreInterface.getInstance(entity).setNumberGeneratorValue(entity, valueToSet);
	}
	protected GenericEntity createEntityInstance(GenericEntity entity) {
		return createEntityInstance(entity.getClass());
	}
	protected GenericEntity createEntityInstance(Class entityInterfaceOrBeanClass) {
		try {
			Class interfaceClass = IDOLookup.getInterfaceClassFor(entityInterfaceOrBeanClass);
			return (GenericEntity) IDOLookup.getHome(interfaceClass).createIDO();
		}
		catch (Exception e) {
			throw new RuntimeException(
				"[idoCopier] : Error creating entity " + entityInterfaceOrBeanClass.getName() + " Message: " + e.getMessage());
		}
	}
	/**
	 * Gets the name of the table checked with constraints such as maximum length of name.
	 **/
	protected static String getNameOfTableChecked(Connection connForDatasource, EntityRelationship relation) {
		String origTableName = relation.getTableName();
		if (doesDatasourceLimitTableNamesToThirtyCharacters(connForDatasource)) {
			return EntityControl.getTableNameShortenedToThirtyCharacters(origTableName);
		}
		else {
			return origTableName;
		}
	}
	/**
	 * Checks if the datasource with the given connection connForDatasource is connected to a database that limits character length to 30.
	 **/
	protected static boolean doesDatasourceLimitTableNamesToThirtyCharacters(Connection connForDatasource) {
		return isDatasourceOracle(connForDatasource);
	}
	/**
	 * Checks if the datasource with the given connection connForDatasource is connected to an Oracle database
	 **/
	protected static boolean isDatasourceOracle(Connection connForDatasource) {
		DatastoreInterface di = DatastoreInterface.getInstance(connForDatasource);
		if (di instanceof OracleDatastoreInterface) {
			return true;
		}
		return false;
	}
	protected boolean isTableAlreadyCopied(GenericEntity entity) {
		IDOEntityCopyInfo info = getIDOEntityCopyInfo(entity.getClass(), entity.getTableName());
		return isTableAlreadyCopied(info);
	}
	protected boolean isTableAlreadyCopied(IDOEntityCopyInfo cInfo) {
		if(copySameTableOnlyOnce){
			for (Iterator iterator = copiedEntites.iterator(); iterator.hasNext();) {
				IDOEntityCopyInfo element = (IDOEntityCopyInfo) iterator.next();
				if(element.equals(cInfo)){
					return true;	
				}
			}
		}
		return false;
	}
	
	public void setToTryCopyingSameTableOnlyOnce(boolean setValue){
		copySameTableOnlyOnce=setValue;
	}
}
