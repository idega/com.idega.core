package com.idega.core.data;


public interface ICApplicationBinding extends com.idega.data.IDOEntity
{
 public java.lang.String getBindingType();
 public java.lang.String getKey();
 public java.lang.Class getPrimaryKeyClass();
 public java.lang.String getValue();
 public void setBindingType(java.lang.String p0);
 public void setKey(java.lang.String p0);
 public void setValue(java.lang.String p0);
}
