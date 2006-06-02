package com.idega.data;

import java.util.HashMap;

/**
 * Title:        idegaclasses
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      idega
 * @author <a href="tryggvi@idega.is">Tryggvi Larusson</a>
 * @version 1.0
 */

public interface IDOEntityDefinition {
    public String getUniqueEntityName();
    public String getSQLTableName();
    public IDOEntityDefinition[] getManyToManyRelatedEntities();
    public IDOEntityField[] getFields();
	public IDOPrimaryKeyDefinition getPrimaryKeyDefinition();
	public Class getInterfaceClass();
	public IDOEntityField findFieldByUniqueName(String name);
	public String getMiddleTableNameForRelation(String relatedTable);
	public IDOEntityField[] findFieldByRelation(Class interfaceClass);
	/**
	 * @return HashMap with the indexes for this Entity, where the KEY is indexName, 
	 * and VALUE is String[], containing columns
	 * @throws NoIndexException is no indexes are specified
	 */
	public HashMap getIndexes() throws NoIndexException;
	public boolean hasAutoIncrementColumn();
	public void setHasAutoIncrementColumn(boolean autoIncrementColumn);
	/**
	 * @return Boolean.TRUE if active by default, Boolean.FALSE if inactive by default, null if system-default
	 */
	public Boolean isBeanCachingActive();
	public void setBeanCachingActiveByDefault(boolean value);
	public void setBeanCachingActiveByDefault(boolean value,int maxCachedBeans);
	public void setBeanCachingActiveByDefault(boolean value,boolean allRecordsCached);
	/**
	 * Gets the default max-cached bean instances if beancaching is set active.
	 */
	public int getMaxCachedBeans();
	public boolean isAllRecordsCached();

	/**
	 * @param allRecordsCached the allRecordsCached to set
	 */
	public void setAllRecordsCached(boolean allRecordsCached);
	
	/**
	 * @return the finderCollectionPrefetchSize
	 */
	public int getFinderCollectionPrefetchSize();

	
	/**
	 * <p>
	 * Sets the prefetch size if the property useFinderCollectionPrefetch is set to true.
	 * Default value of this is 1000
	 * </p>
	 */
	public void setFinderCollectionPrefetchSize(int finderCollectionPrefetchSize);
	
	/**
	 * <p>
	 * Gets if the framework will "pre-fetch" data from resultsets for large collections for this
	 * entity.
	 * </p>
	 */
	public boolean isUseFinderCollectionPrefetch();
	
	/**
	 * <p>
	 * Sets if the framework will "pre-fetch" data from resultsets for large collections.<br/>
	 * This is a workaround for the "n+1" queries problem for BMP beans where n is the number
	 * of records of the resultset fetched.
	 * Setting this property to true will default use this mechanism (through IDOPrimaryKeyList)
	 * when calling the method idoFindPKsByQuery(SelectQuery query) of GenericEntity
	 * and default prefetch batches 1000 records in a row, and can boost database performance
	 * and reduce bottlenecks significantly.
	 * </p>
	 */
	public void setUseFinderCollectionPrefetch(boolean useFinderCollectionPrefetch);
}