package com.idega.data;

/**
 * Title:        idegaclasses
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      idega
 * @author <a href="tryggvi@idega.is">Tryggvi Larusson</a>
 * @version 1.0
 */

public interface IDOEntityField{
  public IDOEntityDefinition getDeclaredEntity();
  public String getUniqueFieldName();
  public String getSQLFieldName();
  public Class getDataTypeClass();
  public boolean isNullAllowed();
  public boolean isPartOfPrimaryKey();
  public int getMaxLength();
  public boolean isPartOfManyToOneRelationship();
  public IDOEntityDefinition getManyToOneRelated()throws IDORelationshipException;
}