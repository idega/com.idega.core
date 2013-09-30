package com.idega.data;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

/**
 *
 * Title:        IDOLegacyEntity the old interface for IDO data objects
 * Description:  IDOLegacyEntity is to be deprecated soon and all new code should use IDOEntity instead.
 * Copyright:    Copyright (c) 2001-2002
 * Company:      idega
 * @author <a href="tryggvi@idega.is">Tryggvi Larusson</a>
 * @version 1.0
 */

public interface IDOLegacyEntity extends IDOEntity,MetaDataCapable {

	/**
	 * @deprecated this is a legacy field an should not be used in new code
	 */
	@Deprecated
	static int STATE_NEW = 0;
	/**
	* @deprecated this is a legacy field an should not be used in new code
	*/
	@Deprecated
	static int STATE_IN_SYNCH_WITH_DATASTORE = 1;
	/**
	* @deprecated this is a legacy field an should not be used in new code
	*/
	@Deprecated
	static int STATE_NOT_IN_SYNCH_WITH_DATASTORE = 2;
	/**
	* @deprecated this is a legacy field an should not be used in new code
	*/
	@Deprecated
	static int STATE_NEW_AND_NOT_IN_SYNCH_WITH_DATASTORE = 3;
	/**
	* @deprecated this is a legacy field an should not be used in new code
	*/
	@Deprecated
	static int STATE_DELETED = 4;

	/**
	* @deprecated this is a legacy method, you should rather use something that is also available via IDOEntity
	*/
	@Deprecated
	public void insert() throws java.sql.SQLException;
	/**
	* @deprecated this is a legacy method, you should rather use something that is also available via IDOEntity
	*/
	@Deprecated
	public char getCharColumnValue(java.lang.String p0);
	/**
	* @deprecated this is a legacy method, you should rather use something that is also available via IDOEntity
	*/
	@Deprecated
	public void setRelationShipClassName(java.lang.String p0, java.lang.String p1);
	/**
	* @deprecated this is a legacy method, you should rather use something that is also available via IDOEntity
	*/
	@Deprecated
	public void addTreeRelationShip();
	/**
	* @deprecated this is a legacy method, you should rather use something that is also available via IDOEntity
	*/
	@Deprecated
	public java.lang.Class getRelationShipClass(java.lang.String p0);
	/**
	* @deprecated this is a legacy method, you should rather use something that is also available via IDOEntity
	*/
	@Deprecated
	public void addTo(com.idega.data.IDOLegacyEntity p0, java.lang.String p1, java.lang.String p2, java.lang.String p3, java.lang.String p4) throws java.sql.SQLException;
	/**
	* @deprecated this is a legacy method, you should rather use something that is also available via IDOEntity
	*/
	@Deprecated
	public void addTo(com.idega.data.IDOLegacyEntity p0, java.lang.String p1, java.lang.String p2) throws java.sql.SQLException;
	//public void addColumnName(java.lang.String p0,java.lang.String p1,boolean p2,boolean p3,java.lang.String p4);
	/**
	* @deprecated this is a legacy method, you should rather use something that is also available via IDOEntity
	*/
	//public boolean equals(com.idega.data.IDOLegacyEntity p0);
	/**
	* @deprecated this is a legacy method, you should rather use something that is also available via IDOEntity
	*/
	@Deprecated
	public void addTo(com.idega.data.IDOLegacyEntity p0) throws java.sql.SQLException;
	/**
	* @deprecated this is a legacy method, you should rather use something that is also available via IDOEntity
	*/
	@Deprecated
	@Override
	public void updateMetaData() throws java.sql.SQLException;
	/**
	* @deprecated this is a legacy method, you should rather use something that is also available via IDOEntity
	*/
	@Deprecated
	public void addMetaData(java.lang.String p0, java.lang.String p1);
	/**
	* @deprecated this is a legacy method, you should rather use something that is also available via IDOEntity
	*/
	@Deprecated
	public void addMetaData(java.lang.String p0, java.lang.String p1, java.lang.String p2);
	/**
	* @deprecated this is a legacy method, you should rather use something that is also available via IDOEntity
	*/
	@Deprecated
	@Override
	public java.lang.String toString();
	/**
	* @deprecated this is a legacy method, you should rather use something that is also available via IDOEntity
	*/
	@Deprecated
	public java.lang.String getNameOfMiddleTable(com.idega.data.IDOEntity p0, com.idega.data.IDOEntity p1);
	/**
	* @deprecated this is a legacy method, you should rather use something that is also available via IDOEntity
	*/
	@Deprecated
	public com.idega.data.IDOLegacyEntity[] findAllOrdered(java.lang.String p0) throws java.sql.SQLException;
	/**
	* @deprecated this is a legacy method, you should rather use something that is also available via IDOEntity
	*/
	@Deprecated
	public boolean isPrimaryKey(java.lang.String p0);
	/**
	* @deprecated this is a legacy method, you should rather use something that is also available via IDOEntity
	*/
	@Deprecated
	public void setColumn(java.lang.String p0, java.lang.Object p1);
	/**
	* @deprecated this is a legacy method, you should rather use something that is also available via IDOEntity
	*/
	@Deprecated
	public void setColumn(java.lang.String p0, java.lang.Boolean p1);
	/**
	* @deprecated this is a legacy method, you should rather use something that is also available via IDOEntity
	*/
	@Deprecated
	public boolean getIfNullable(java.lang.String p0);
	/**
	* @deprecated this is a legacy method, you should rather use something that is also available via IDOEntity
	*/
	@Deprecated
	public void setName(java.lang.String p0);
	/**
	* @deprecated this is a legacy method, you should rather use something that is also available via IDOEntity
	*/
	@Deprecated
	public void deleteMultiple(java.lang.String p0, java.lang.String p1) throws java.sql.SQLException;
	/**
	* @deprecated this is a legacy method, you should rather use something that is also available via IDOEntity
	*/
	@Deprecated
	public void deleteMultiple(String columnName1, String stringColumnValue1, String columnName2, String stringColumnValue2) throws SQLException;
	/**
	* @deprecated this is a legacy method, you should rather use something that is also available via IDOEntity
	*/
	@Deprecated
	public void update() throws java.sql.SQLException;
	/**
	* @deprecated this is a legacy method, you should rather use something that is also available via IDOEntity
	*/
	@Deprecated
	public boolean getIfVisible(java.lang.String p0);
	/**
	* @deprecated this is a legacy method, you should rather use something that is also available via IDOEntity
	*/
	@Deprecated
	public java.lang.String getStringColumnValue(java.lang.String p0);
	/**
	* @deprecated this is a legacy method, you should rather use something that is also available via IDOEntity
	*/
	@Deprecated
	public com.idega.data.IDOLegacyEntity[] findAssociatedOrdered(com.idega.data.IDOLegacyEntity p0, java.lang.String p1) throws java.sql.SQLException;
	/**
	* @deprecated this is a legacy method, you should rather use something that is also available via IDOEntity
	*/
	@Deprecated
	public java.lang.String[] getEditableColumnNames();
	/**
	* @deprecated this is a legacy method, you should rather use something that is also available via IDOEntity
	*/
	@Deprecated
	public void metaDataHasChanged(boolean p0);
	/**
	* @deprecated this is a legacy method, you should rather use something that is also available via IDOEntity
	*/
	@Deprecated
	public com.idega.data.BlobWrapper getBlobColumnValue(java.lang.String p0);
	/**
	* @deprecated this is a legacy method, you should rather use something that is also available via IDOEntity
	*/
	@Deprecated
	public java.util.Hashtable getMetaDataIds();
	/**
	* @deprecated this is a legacy method, you should rather use something that is also available via IDOEntity
	*/
	@Deprecated
	public java.io.InputStream getInputStreamColumnValue(java.lang.String p0) throws java.lang.Exception;
	/**
	* @deprecated this is a legacy method, you should rather use something that is also available via IDOEntity
	*/
	@Deprecated
	public void setStringColumn(java.lang.String p0, java.lang.String p1);
	/**
	* @deprecated this is a legacy method, you should rather use something that is also available via IDOEntity
	*/
	@Deprecated
	public void removeFrom(com.idega.data.IDOLegacyEntity p0) throws java.sql.SQLException;
	/**
	* @deprecated this is a legacy method, you should rather use something that is also available via IDOEntity
	*/
	@Deprecated
	public com.idega.data.IDOLegacyEntity[] findAll(java.lang.String p0, int p1) throws java.sql.SQLException;
	/**
	* @deprecated this is a legacy method, you should rather use something that is also available via IDOEntity
	*/
	@Deprecated
	public void setID(java.lang.Integer p0);
	/**
	* @deprecated this is a legacy method, you should rather use something that is also available via IDOEntity
	*/
	@Deprecated
	public void addTo(com.idega.data.IDOLegacyEntity p0, java.lang.String p1, java.lang.String p2, java.lang.String p3, java.lang.String p4, java.lang.String p5, java.lang.String p6) throws java.sql.SQLException;
	/**
	* @deprecated this is a legacy method, you should rather use something that is also available via IDOEntity
	*/
	@Deprecated
	public void insertMetaData() throws java.sql.SQLException;
	/**
	* @deprecated this is a legacy method, you should rather use something that is also available via IDOEntity
	*/
	@Deprecated
	public List<String> getMetaDataUpdate();
	/**
	* @deprecated this is a legacy method, you should rather use something that is also available via IDOEntity
	*/
	@Deprecated
	@Override
	public boolean removeMetaData(java.lang.String p0);
	/**
	* @deprecated this is a legacy method, you should rather use something that is also available via IDOEntity
	*/
	@Deprecated
	public List<String> getMetaDataInsert();
	/**
	* @deprecated this is a legacy method, you should rather use something that is also available via IDOEntity
	*/
	@Deprecated
	public java.sql.Connection getConnection() throws java.sql.SQLException;
	/**
	* @deprecated this is a legacy method, you should rather use something that is also available via IDOEntity
	*/
	@Deprecated
	public int getNumberOfRecordsReverseRelated(com.idega.data.IDOLegacyEntity p0) throws java.sql.SQLException;
	/**
	* @deprecated this is a legacy method, you should rather use something that is also available via IDOEntity
	*/
	@Deprecated
	public com.idega.data.IDOLegacyEntity[] findAllByColumnOrdered(java.lang.String p0, java.lang.String p1, java.lang.String p2) throws java.sql.SQLException;
	/**
	* @deprecated this is a legacy method, you should rather use something that is also available via IDOEntity
	*/
	@Deprecated
	public com.idega.data.IDOLegacyEntity[] findAllByColumnEqualsOrdered(java.lang.String p0, java.lang.String p1, java.lang.String p2) throws java.sql.SQLException;

	/**
	* @deprecated this is a legacy method, you should rather use something that is also available via IDOEntity
	*/
	@Deprecated
	public int getStorageClassType(java.lang.String p0);
	/**
	* @deprecated this is a legacy method, you should rather use something that is also available via IDOEntity
	*/
	@Deprecated
	public void setColumn(java.lang.String p0, java.io.InputStream p1);
	/**
	* @deprecated this is a legacy method, you should rather use something that is also available via IDOEntity
	*/
	@Deprecated
	public void setMaxLength(java.lang.String p0, int p1);
	/**
	* @deprecated this is a legacy method, you should rather use something that is also available via IDOEntity
	*/
	@Deprecated
	public com.idega.data.IDOLegacyEntity[] findAllByColumn(java.lang.String p0, java.lang.String p1, java.lang.String p2, java.lang.String p3) throws java.sql.SQLException;
	/**
	* @deprecated this is a legacy method, you should rather use something that is also available via IDOEntity
	*/
	@Deprecated
	public com.idega.data.IDOLegacyEntity[] findAllByColumnEquals(java.lang.String p0, java.lang.String p1, java.lang.String p2, java.lang.String p3) throws java.sql.SQLException;
	/**
	* @deprecated this is a legacy method, you should rather use something that is also available via IDOEntity
	*/
	@Deprecated
	public com.idega.data.IDOLegacyEntity[] findAllByColumn(java.lang.String p0, java.lang.String p1, java.lang.String p2, java.lang.String p3, java.lang.String p4, java.lang.String p5) throws java.sql.SQLException;
	/**
	* @deprecated this is a legacy method, you should rather use something that is also available via IDOEntity
	*/
	@Deprecated
	public com.idega.data.IDOLegacyEntity[] findAllByColumnEquals(java.lang.String p0, java.lang.String p1, java.lang.String p2, java.lang.String p3, java.lang.String p4, java.lang.String p5) throws java.sql.SQLException;
	/**
	* @deprecated this is a legacy method, you should rather use something that is also available via IDOEntity
	*/
	@Deprecated
	public void reverseRemoveFrom(com.idega.data.IDOLegacyEntity p0) throws java.sql.SQLException;
	/**
	* @deprecated this is a legacy method, you should rather use something that is also available via IDOEntity
	*/
	@Deprecated
	public void freeConnection(java.sql.Connection p0);
	/**
	* @deprecated this is a legacy method, you should rather use something that is also available via IDOEntity
	*/
	@Deprecated
	public java.lang.Integer getIDInteger();
	/**
	* @deprecated this is a legacy method, you should rather use something that is also available via IDOEntity
	*/
	@Deprecated
	public void empty();
	/**
	* @deprecated this is a legacy method, you should rather use something that is also available via IDOEntity
	*/
	@Deprecated
	public com.idega.data.IDOLegacyEntity[] findAllByColumnOrdered(java.lang.String p0, java.lang.String p1, java.lang.String p2, java.lang.String p3, java.lang.String p4) throws java.sql.SQLException;
	/**
	* @deprecated this is a legacy method, you should rather use something that is also available via IDOEntity
	*/
	@Deprecated
	public com.idega.data.IDOLegacyEntity[] findAllByColumnEqualsOrdered(java.lang.String p0, java.lang.String p1, java.lang.String p2, java.lang.String p3, java.lang.String p4) throws java.sql.SQLException;
	/**
	* @deprecated this is a legacy method, you should rather use something that is also available via IDOEntity
	*/
	@Deprecated
	public com.idega.data.IDOLegacyEntity[] findAllByColumnEqualsOrdered(java.lang.String p0, int p1, java.lang.String p2, int p3, java.lang.String p4) throws java.sql.SQLException;
	/**
	* @deprecated this is a legacy method, you should rather use something that is also available via IDOEntity
	*/
	@Deprecated
	@Override
	public void setDatasource(java.lang.String p0);
	/**
	* @deprecated this is a legacy method, you should rather use something that is also available via IDOEntity
	*/
	@Deprecated
	public int getNumberOfRecords(java.lang.String p0, java.lang.String p1) throws java.sql.SQLException;
	/**
	* @deprecated this is a legacy method, you should rather use something that is also available via IDOEntity
	*/
	@Deprecated
	public int getMaxColumnValue(java.lang.String p0) throws java.sql.SQLException;
	/**
	* @deprecated this is a legacy method, you should rather use something that is also available via IDOEntity
	*/
	@Deprecated
	public float getFloatColumnValue(java.lang.String p0);
	/**
	* @deprecated this is a legacy method, you should rather use something that is also available via IDOEntity
	*/
	@Deprecated
	public java.lang.String getName();
	/**
	* @deprecated this is a legacy method, you should rather use something that is also available via IDOEntity
	*/
	@Deprecated
	public void setStorageClassType(java.lang.String p0, int p1);
	/**
	* @deprecated this is a legacy method, you should rather use something that is also available via IDOEntity
	*/
	@Deprecated
	public com.idega.data.IDOLegacyEntity[] findReverseRelated(com.idega.data.IDOLegacyEntity p0) throws java.sql.SQLException;
	/**
	* @deprecated this is a legacy method, you should rather use something that is also available via IDOEntity
	*/
	@Deprecated
	public com.idega.data.IDOLegacyEntity[] findAll(java.lang.String p0) throws java.sql.SQLException;
	/**
	* @deprecated this is a legacy method, you should rather use something that is also available via IDOEntity
	*/
	@Deprecated
	public void setDefaultValues();
	/**
	* @deprecated this is a legacy method, you should rather use something that is also available via IDOEntity
	*/
	@Deprecated
	public java.lang.String getStorageClassName(java.lang.String p0);
	/**
	* @deprecated this is a legacy method, you should rather use something that is also available via IDOEntity
	*/
	@Deprecated
	public com.idega.data.IDOLegacyEntity[] findRelated(com.idega.data.IDOLegacyEntity p0) throws java.sql.SQLException;
	/**
	* @deprecated this is a legacy method, you should rather use something that is also available via IDOEntity
	*/
	@Deprecated
	public void setColumn(java.lang.String p0, java.lang.Float p1);
	/**
	* @deprecated this is a legacy method, you should rather use something that is also available via IDOEntity
	*/
	@Deprecated
	public void setColumn(java.lang.String p0, java.lang.Integer p1);
	/**
	* @deprecated this is a legacy method, you should rather use something that is also available via IDOEntity
	*/
	@Deprecated
	public java.lang.Object getColumnValue(java.lang.String p0);
	/**
	* @deprecated this is a legacy method, you should rather use something that is also available via IDOEntity
	*/
	@Deprecated
	public java.lang.String[] getColumnNames();
	/**
	* @deprecated this is a legacy method, you should rather use something that is also available via IDOEntity
	*/
	@Deprecated
	public void setUnique(java.lang.String p0, boolean p1);
	/**
	* @deprecated this is a legacy method, you should rather use something that is also available via IDOEntity
	*/
	@Deprecated
	public int getIntColumnValue(java.lang.String p0);
	/**
	* @deprecated this is a legacy method, you should rather use something that is also available via IDOEntity
	*/
	@Deprecated
	public void delete() throws java.sql.SQLException;
	/**
	* @deprecated this is a legacy method, you should rather use something that is also available via IDOEntity
	*/
	@Deprecated
	public com.idega.data.IDOLegacyEntity[] constructArray(java.lang.String[] p0);
	/**
	* @deprecated this is a legacy method, you should rather use something that is also available via IDOEntity
	*/
	@Deprecated
	public boolean isInSynchWithDatastore();
	/**
	* @deprecated this is a legacy method, you should rather use something that is also available via IDOEntity
	*/
	@Deprecated
	public int getMaxLength(java.lang.String p0);
	/**
	* @deprecated this is a legacy method, you should rather use something that is also available via IDOEntity
	*/
	@Deprecated
	public void setStorageClassName(java.lang.String p0, java.lang.String p1);
	/**
	* @deprecated this is a legacy method, you should rather use something that is also available via IDOEntity
	*/
	@Deprecated
	public com.idega.data.IDOLegacyEntity[] findAssociated(com.idega.data.IDOLegacyEntity p0) throws java.sql.SQLException;
	/**
	* @deprecated this is a legacy method, you should rather use something that is also available via IDOEntity
	*/
	@Deprecated
	public int getNumberOfRecords(java.lang.String p0, java.lang.String p1, java.lang.String p2) throws java.sql.SQLException;
	/**
	* @deprecated this is a legacy method, you should rather use something that is also available via IDOEntity
	*/
	@Deprecated
	public int getNumberOfRecords() throws java.sql.SQLException;
	/**
	* @deprecated this is a legacy method, you should rather use something that is also available via IDOEntity
	*/
	@Deprecated
	public com.idega.data.IDOLegacyEntity[] findAll() throws java.sql.SQLException;
	/**
	* @deprecated this is a legacy method, you should rather use something that is also available via IDOEntity
	*/
	@Deprecated
	public int getID();
	/**
	* @deprecated this is a legacy method, you should rather use something that is also available via IDOEntity
	*/
	@Deprecated
	public void setColumn(java.lang.String p0, int p1);
	/**
	* @deprecated this is a legacy method, you should rather use something that is also available via IDOEntity
	*/
	@Deprecated
	public boolean hasMetaDataRelationship();
	/**
	* @deprecated this is a legacy method, you should rather use something that is also available via IDOEntity
	*/
	@Deprecated
	public com.idega.data.IDOLegacyEntity[] findAllDescendingOrdered(java.lang.String p0) throws java.sql.SQLException;
	/**
	* @deprecated this is a legacy method, you should rather use something that is also available via IDOEntity
	*/
	@Deprecated
	public double getDoubleColumnValue(java.lang.String p0);
	/**
	* @deprecated this is a legacy method, you should rather use something that is also available via IDOEntity
	*/
	@Deprecated
	public void setColumn(java.lang.String p0, float p1);
	/**
	* @deprecated this is a legacy method, you should rather use something that is also available via IDOEntity
	*/
	@Deprecated
	public java.lang.String[] getVisibleColumnNames();
	/**
	* @deprecated this is a legacy method, you should rather use something that is also available via IDOEntity
	*/
	@Deprecated
	public java.lang.String getIDColumnName();
	/**
	* @deprecated this is a legacy method, you should rather use something that is also available via IDOEntity
	*/
	@Deprecated
	public void addTo(java.lang.Class p0, int p1) throws java.sql.SQLException;
	/**
	* @deprecated this is a legacy method, you should rather use something that is also available via IDOEntity
	*/
	@Deprecated
	public void removeFromColumn(java.lang.String p0);
	/**
	* @deprecated this is a legacy method, you should rather use something that is also available via IDOEntity
	*/
	@Deprecated
	public java.lang.String getRelationShipType(java.lang.String p0);
	/**
	* @deprecated this is a legacy method, you should rather use something that is also available via IDOEntity
	*/
	@Deprecated
	public void setVisible(java.lang.String p0, boolean p1);
	/**
	* deprecated: this is a legacy method, you should rather use something that is also available via IDOEntity
	*/
	public java.lang.String getEntityName();
	/**
	* @deprecated this is a legacy method, you should rather use something that is also available via IDOEntity
	*/
	@Deprecated
	public com.idega.data.BlobWrapper getEmptyBlob(java.lang.String p0);
	/**
	* @deprecated this is a legacy method, you should rather use something that is also available via IDOEntity
	*/
	@Deprecated
	public com.idega.data.IDOLegacyEntity[] findAllByColumn(java.lang.String p0, int p1) throws java.sql.SQLException;
	/**
	* @deprecated this is a legacy method, you should rather use something that is also available via IDOEntity
	*/
	@Deprecated
	public com.idega.data.IDOLegacyEntity[] findAllByColumnEquals(java.lang.String p0, int p1) throws java.sql.SQLException;
	/**
	* @deprecated this is a legacy method, you should rather use something that is also available via IDOEntity
	*/
	@Deprecated
	public com.idega.data.IDOLegacyEntity[] findAllByColumnDescendingOrdered(java.lang.String p0, java.lang.String p1, java.lang.String p2, java.lang.String p3, java.lang.String p4) throws java.sql.SQLException;
	/**
	* @deprecated this is a legacy method, you should rather use something that is also available via IDOEntity
	*/
	@Deprecated
	public boolean getIfEditable(java.lang.String p0);
	/**
	* @deprecated this is a legacy method, you should rather use something that is also available via IDOEntity
	*/
	@Deprecated
	public boolean getIfUnique(java.lang.String p0);
//	/**
//	* @deprecated this is a legacy method, you should rather use something that is also available via IDOEntity
//	*/
//	public com.idega.data.IDOLegacyEntity[] constructArray(int[] p0);
	/**
	* @deprecated this is a legacy method, you should rather use something that is also available via IDOEntity
	*/
	@Deprecated
	public List<String> getMetaDataDelete();
	/**
	* @deprecated this is a legacy method, you should rather use something that is also available via IDOEntity
	*/
	@Deprecated
	public int getNumberOfRecords(java.lang.String p0) throws java.sql.SQLException;
	/**
	* @deprecated this is a legacy method, you should rather use something that is also available via IDOEntity
	*/
	@Deprecated
	public java.lang.String getLongName(java.lang.String p0);
	/**
	* @deprecated this is a legacy method, you should rather use something that is also available via IDOEntity
	*/
	@Deprecated
	public void setID(int p0);
	/**
	* @deprecated this is a legacy method, you should rather use something that is also available via IDOEntity
	*/
	@Deprecated
	public void setLongName(java.lang.String p0, java.lang.String p1);
	/**
	* @deprecated this is a legacy method, you should rather use something that is also available via IDOEntity
	*/
	@Deprecated
	public java.io.OutputStream getColumnOutputStream(java.lang.String p0);
	/**
	* @deprecated this is a legacy method, you should rather use something that is also available via IDOEntity
	*/
	@Deprecated
	public java.sql.Connection getConnection(java.lang.String p0) throws java.sql.SQLException;
	/**
	* @deprecated this is a legacy method, you should rather use something that is also available via IDOEntity
	*/
	@Deprecated
	public java.lang.String getLobColumnName();
	/**
	* @deprecated this is a legacy method, you should rather use something that is also available via IDOEntity
	*/
	@Deprecated
	@Override
	public java.lang.String getDatasource();
	/**
	* @deprecated this is a legacy method, you should rather use something that is also available via IDOEntity
	*/
	@Deprecated
	public java.lang.Integer getIntegerColumnValue(java.lang.String p0);
	/**
	* @deprecated this is a legacy method, you should rather use something that is also available via IDOEntity
	*/
	//public boolean equals(java.lang.Object p0);
	/**
	* @deprecated this is a legacy method, you should rather use something that is also available via IDOEntity
	*/
	@Deprecated
	public void removeFrom(com.idega.data.IDOLegacyEntity[] p0) throws java.sql.SQLException;
	/**
	* @deprecated this is a legacy method, you should rather use something that is also available via IDOEntity
	*/
	@Deprecated
	public int getIntTableValue(java.lang.String p0) throws java.sql.SQLException;
	/**
	* @deprecated this is a legacy method, you should rather use something that is also available via IDOEntity
	*/
	@Deprecated
	public void freeConnection(java.lang.String p0, java.sql.Connection p1);
	/**
	* @deprecated this is a legacy method, you should rather use something that is also available via IDOEntity
	*/
	@Deprecated
	public void setRelationShipType(java.lang.String p0, java.lang.String p1);
	/**
	* @deprecated this is a legacy method, you should rather use something that is also available via IDOEntity
	*/
	@Deprecated
	public com.idega.data.IDOLegacyEntity[] findAllByColumn(java.lang.String p0, java.lang.String p1) throws java.sql.SQLException;
	/**
	* @deprecated this is a legacy method, you should rather use something that is also available via IDOEntity
	*/
	@Deprecated
	public com.idega.data.IDOLegacyEntity[] findAllByColumnEquals(java.lang.String p0, java.lang.String p1) throws java.sql.SQLException;

	/**
	* @deprecated this is a legacy method, you should rather use something that is also available via IDOEntity
	*/
	@Deprecated
	public void findByPrimaryKey(int p0) throws java.sql.SQLException;
	/**
	* @deprecated this is a legacy method, you should rather use something that is also available via IDOEntity
	*/
	@Deprecated
	public void setColumn(java.lang.String p0, boolean p1);
	/**
	* @deprecated this is a legacy method, you should rather use something that is also available via IDOEntity
	*/
	@Deprecated
	public void clear() throws java.sql.SQLException;
	/**
	* @deprecated this is a legacy method, you should rather use something that is also available via IDOEntity
	*/
	@Deprecated
	public boolean metaDataHasChanged();
	/**
	* @deprecated this is a legacy method, you should rather use something that is also available via IDOEntity
	*/
	@Deprecated
	public void addTo(com.idega.data.IDOLegacyEntity p0, java.lang.String p1) throws java.sql.SQLException;
	/**
	* @deprecated this is a legacy method, you should rather use something that is also available via IDOEntity
	*/
	@Deprecated
	public boolean isNull(java.lang.String p0);
	/**
	* @deprecated this is a legacy method, you should rather use something that is also available via IDOEntity
	*/
	@Deprecated
	public com.idega.data.IDOLegacyEntity[] findAllByColumnDescendingOrdered(java.lang.String p0, java.lang.String p1, java.lang.String p2) throws java.sql.SQLException;
	/**
	* @deprecated this is a legacy method, you should rather use something that is also available via IDOEntity
	*/
	@Deprecated
	public boolean getBooleanColumnValue(java.lang.String p0);
	/**
	* @deprecated this is a legacy method, you should rather use something that is also available via IDOEntity
	*/
	@Deprecated
	public int getMaxColumnValue(java.lang.String p0, java.lang.String p1, java.lang.String p2) throws java.sql.SQLException;
	/**
	* @deprecated this is a legacy method, you should rather use something that is also available via IDOEntity
	*/
	@Deprecated
	public void addTo(com.idega.data.IDOLegacyEntity p0, java.sql.Connection p1) throws java.sql.SQLException;
	/**
	* @deprecated this is a legacy method, you should rather use something that is also available via IDOEntity
	*/
	@Deprecated
	public int getNumberOfRecordsRelated(com.idega.data.IDOLegacyEntity p0) throws java.sql.SQLException;
	/**
	* @deprecated this is a legacy method, you should rather use something that is also available via IDOEntity
	*/
	@Deprecated
	public void setNullable(java.lang.String p0, boolean p1);
	/**
	* @deprecated this is a legacy method, you should rather use something that is also available via IDOEntity
	*/
	@Deprecated
	public void deleteMetaData() throws java.sql.SQLException;
	/**
	* @deprecated this is a legacy method, you should rather use something that is also available via IDOEntity
	*/
	@Deprecated
	public com.idega.data.EntityAttribute getColumn(java.lang.String p0);
	/**
	* @deprecated this is a legacy method, you should rather use something that is also available via IDOEntity
	*/
	@Deprecated
	public boolean columnsHaveChanged();
	/**
	* @deprecated this is a legacy method, you should rather use something that is also available via IDOEntity
	*/
	@Deprecated
	public void setToInsertStartData(boolean ifTrue);
	/**
	* @deprecated this is a legacy method, you should rather use something that is also available via IDOEntity
	*/
	@Deprecated
	public boolean getIfInsertStartData();
	/**
	* @deprecated this is a legacy method, you should rather use something that is also available via IDOEntity
	*/
	@Deprecated
	public String getTableName();
	/**
	* @deprecated this is a legacy method, you should rather use something that is also available via IDOEntity
	*/
	@Deprecated
	public void setEntityState(int state);
	/**
	* @deprecated this is a legacy method, you should rather use something that is also available via IDOEntity
	*/ //public java.util.Collection getAttributes();
//	/**
//	* @deprecated this is a legacy method, you should rather use something that is also available via IDOEntity
//	*/
//	public GenericEntity getIDOEntityStaticInstance();
	//public String getCachedColumnNamesList();
	/**
	* @deprecated this is a legacy method, you should rather use something that is also available via IDOEntity
	*/
	@Deprecated
	public boolean hasBeenSetNull(String columnName);
	/**
	* @deprecated this is a legacy method, you should rather use something that is also available via IDOEntity
	*/
	@Deprecated
	public Object getPrimaryKeyValue();
	/**
	* @deprecated this is a legacy method, you should rather use something that is also available via IDOEntity
	*/
	@Deprecated
	public boolean hasLobColumn() throws Exception;
	/**
	* @deprecated this is a legacy method, you should rather use something that is also available via IDOEntity
	*/
	@Deprecated
	public void fillColumn(String columnName, java.sql.ResultSet rs) throws java.sql.SQLException;
	/**
	* @deprecated this is a legacy method, you should rather use something that is also available via IDOEntity
	*/
	@Deprecated
	public void delete(Connection c) throws SQLException;
	/**
	* @deprecated this is a legacy method, you should rather use something that is also available via IDOEntity
	*/
	@Deprecated
	public void insert(Connection c) throws SQLException;
	/**
	* @deprecated this is a legacy method, you should rather use something that is also available via IDOEntity
	*/
	@Deprecated
	public void update(Connection c) throws SQLException;
	/**
	* @deprecated this is a legacy method, you should rather use something that is also available via IDOEntity
	*/
	@Deprecated
	public void removeFrom(IDOLegacyEntity entityToRemoveFrom, Connection conn) throws SQLException;
	/**
	* @deprecated this is a legacy method, you should rather use something that is also available via IDOEntity
	*/
	@Deprecated
	public void removeFrom(Class classToRemoveFrom) throws SQLException;
	/**
	* @deprecated this is a legacy method, you should rather use something that is also available via IDOEntity
	*/
	@Deprecated
	public int getEntityState();

	/**
	* @deprecated this is a legacy method, you should rather use something that is also available via IDOEntity
	*/
	@Deprecated
	public IDOLegacyEntity[] findAllByColumnOrdered(String columnName1, String toFind1, String columnName2, String toFind2, String orderByColumnName, String condition1, String condition2) throws SQLException;
	/**
	* @deprecated this is a legacy method, you should rather use something that is also available via IDOEntity
	*/
	@Deprecated
	public IDOLegacyEntity[] findAllByColumn(String columnName, String toFind, String condition) throws SQLException;
	/**
	* @deprecated this is a legacy method, you should rather use something that is also available via IDOEntity
	*/
	@Deprecated
	public IDOLegacyEntity[] findAllByColumn(String columnName1, String toFind1, char condition1, String columnName2, String toFind2, char condition2) throws SQLException;
	/**
	* @deprecated this is a legacy method, you should rather use something that is also available via IDOEntity
	*/
	@Deprecated
	public int[] findRelatedIDs(IDOLegacyEntity entity, String entityColumnName, String entityColumnValue) throws SQLException;
	/**
	* @deprecated this is a legacy method, you should rather use something that is also available via IDOEntity
	*/
	@Deprecated
	public int[] findRelatedIDs(IDOLegacyEntity entity) throws SQLException;
	/**
	* @deprecated this is a legacy method, you should rather use something that is also available via IDOEntity
	*/
	@Deprecated
	public void removeFrom(Class classToRemoveFrom, int idToRemoveFrom) throws SQLException;
	/**
	* @deprecated this is a legacy method, you should rather use something that is also available via IDOEntity
	*/
	@Deprecated
	public IDOLegacyEntity[] findAllByColumnOrdered(java.lang.String columnName1, java.lang.String toFind1, java.lang.String columnName2, java.lang.String toFind2) throws SQLException;
	/**
	* @deprecated this is a legacy method, you should rather use something that is also available via IDOEntity
	*/
	@Deprecated
	public IDOLegacyEntity[] findRelated(IDOLegacyEntity entity, String columnName, String columnValue) throws SQLException;
	/**
	* @deprecated this is a legacy method, you should rather use something that is also available via IDOEntity
	*/
	@Deprecated
	public int getNumberOfRecords(String columnName, int id) throws SQLException;
}
