package com.idega.data;

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
}