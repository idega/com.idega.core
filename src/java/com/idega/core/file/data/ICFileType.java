package com.idega.core.file.data;


public interface ICFileType extends com.idega.data.CacheableEntity
{
 public java.lang.String getCacheKey();
 public java.lang.String getDescription();
 public java.lang.String getDisplayName();
 public int getFileTypeHandlerID();
 public java.lang.String getName();
 public java.lang.String getUniqueName();
 public void setDescription(java.lang.String p0);
 public void setDisplayName(java.lang.String p0);
 public void setFileTypeHandler(com.idega.core.file.data.ICFileTypeHandler p0);
 public void setFileTypeHandlerId(int p0);
 public void setName(java.lang.String p0);
 public void setType(java.lang.String p0);
 public void setUniqueName(java.lang.String p0);
}
