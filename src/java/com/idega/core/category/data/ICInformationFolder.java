package com.idega.core.category.data;


public interface ICInformationFolder extends com.idega.data.CacheableEntity,com.idega.core.category.data.InformationFolder
{
 public java.sql.Timestamp getCreated();
 public boolean getDeleted();
 public int getDeletedBy();
 public java.sql.Timestamp getDeletedWhen();
 public java.lang.String getDescription();
 public int getICObjectId();
 public int getLocaleId();
 public java.lang.String getName();
 public int getOwnerGroupID();
 public int getParentId();
 public java.lang.String getType();
 public boolean getValid();
 public void setCreated(java.sql.Timestamp p0);
 public void setDeleted(boolean p0);
 public void setDescription(java.lang.String p0);
 public void setICObjectId(int p0);
 public void setLocaleId(int p0);
 public void setName(java.lang.String p0);
 public void setOwnerGroupID(int p0);
 public void setParentId(int p0);
 public ICInformationFolder getParent();
 public void setType(java.lang.String p0);
 public void setValid(boolean p0);
}
