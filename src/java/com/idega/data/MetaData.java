package com.idega.data;


public interface MetaData extends com.idega.data.IDOLegacyEntity
{
 public java.lang.String getMetaDataName();
 public java.lang.String getMetaDataValue();
 public java.lang.String getMetaDataType();
 public java.lang.String getName();
 public java.lang.String getValue();
 public java.lang.String getType();
 public void setMetaDataName(java.lang.String p0);
 public void setMetaDataNameAndValue(java.lang.String p0,java.lang.String p1);
 public void setMetaDataValue(java.lang.String p0);
 public void setMetaDataType(java.lang.String p0);
 public void setName(java.lang.String p0);
 public void setValue(java.lang.String p0);
 public void setType(java.lang.String p0);
}
