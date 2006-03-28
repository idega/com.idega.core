package com.idega.core.component.data;


public interface ICObjectInstance extends com.idega.data.IDOLegacyEntity
{
 public int getIBPageID();
 public java.lang.String getName();
 public com.idega.presentation.PresentationObject getNewInstance()throws java.lang.ClassNotFoundException,java.lang.IllegalAccessException,java.lang.InstantiationException;
 public com.idega.core.component.data.ICObject getObject();
 public int getParentInstanceID();
 public void setDefaultValues();
 public void setIBPageByKey(java.lang.String p0);
 public void setIBPageID(int p0);
 public void setICObject(com.idega.core.component.data.ICObject p0);
 public void setICObjectID(int p0);
 public void setParentInstanceID(int p0);
public String getUniqueId();
public void setUniqueId(String uniqueId);
public int getID();
}
