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
	
}