package com.idega.core.data;

import javax.ejb.*;

public interface ICInformationCategory extends com.idega.data.CacheableEntity,com.idega.core.business.InformationCategory
{
 public java.sql.Timestamp getCreated();
 public boolean getDeleted();
 public int getDeletedBy();
 public java.sql.Timestamp getDeletedWhen();
 public java.lang.String getDescription();
 public int getICObjectId();
 public java.lang.String getName();
 public int getOwnerFolderId();
 public java.lang.String getType();
 public boolean getValid();
 public void setCreated(java.sql.Timestamp p0);
 public void setDeleted(boolean p0);
 public void setDescription(java.lang.String p0);
 public void setFolderSpecific(int p0);
 public void setGlobal()throws java.sql.SQLException;
 public void setICObjectId(int p0);
 public void setName(java.lang.String p0);
 public void setType(java.lang.String p0);
 public void setValid(boolean p0);
}
