package com.idega.user.data;

import javax.ejb.*;

public interface GroupType extends com.idega.data.IDOLegacyEntity
{
 public java.lang.Class getHandlerClass()throws java.lang.ClassNotFoundException;
 public void setType(java.lang.String p0);
 public java.lang.Class getPrimaryKeyClass();
 public void setHandlerClass(com.idega.core.data.ICObject p0);
 public void setDescription(java.lang.String p0);
 public java.lang.String getType();
 public java.lang.String getDescription();
 public java.lang.String getIDColumnName();
}
