package com.idega.core.component.data;

import com.idega.exception.IWBundleDoesNotExist;


public interface ICObject extends com.idega.data.IDOLegacyEntity
{
 public com.idega.idegaweb.IWBundle getBundle(com.idega.idegaweb.IWMainApplication p0)throws IWBundleDoesNotExist;
 public java.lang.String getBundleIdentifier();
 public java.lang.String getClassName();
 public java.lang.String getName();
 public com.idega.presentation.PresentationObject getNewInstance()throws java.lang.ClassNotFoundException,java.lang.IllegalAccessException,java.lang.InstantiationException;
 public java.lang.Class getObjectClass()throws java.lang.ClassNotFoundException;
 public java.lang.String getObjectType();
 public void initializeAttributes();
 public void setBundle(com.idega.idegaweb.IWBundle p0);
 public void setBundleIdentifier(java.lang.String p0);
 public void setClassName(java.lang.String p0);
 public void setName(java.lang.String p0);
 public void setObjectClass(java.lang.Class p0);
 public void setObjectType(java.lang.String p0);
}
