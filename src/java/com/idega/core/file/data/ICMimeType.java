package com.idega.core.file.data;


public interface ICMimeType extends com.idega.data.CacheableEntity
{
 public void delete()throws java.sql.SQLException;
 public java.lang.String getDescription();
 public int getFileTypeID();
 public java.lang.String getIDColumnName();
 public java.lang.String getMimeType();
 public void setDescription(java.lang.String p0);
 public void setFileTypeId(int p0);
 public void setMimeType(java.lang.String p0);
 public void setMimeTypeAndDescription(java.lang.String p0,java.lang.String p1);
}
