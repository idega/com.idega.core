package com.idega.core.file.data;


public interface ICFileTypeHandler extends com.idega.data.CacheableEntity
{
 public java.lang.String getCacheKey();
 public java.lang.String getHandlerClass();
 public java.lang.String getHandlerName();
 public java.lang.String getName();
 public void setHandlerClass(java.lang.String p0);
 public void setHandlerClass(java.lang.Class p0);
 public void setHandlerName(java.lang.String p0);
 public void setName(java.lang.String p0);
 public void setNameAndHandlerClass(java.lang.String p0,java.lang.String p1);
 public void setNameAndHandlerClass(java.lang.String p0,java.lang.Class p1);
}
