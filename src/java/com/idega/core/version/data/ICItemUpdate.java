package com.idega.core.version.data;


public interface ICItemUpdate extends com.idega.data.IDOEntity
{
 public java.lang.String getDescription();
 public com.idega.user.data.User getUpdatedByUser();
 public int getUpdatedByUserID();
 public java.sql.Timestamp getUpdatedTimestamp();
 public com.idega.core.version.data.ICVersion getVersion();
 public int getVersionID();
 public void initializeAttributes();
 public void setCreatedTimestamp(java.sql.Timestamp p0);
 public void setDescription(java.lang.String p0);
 public void setUpdatedByUser(com.idega.user.data.User p0);
 public void setUpdatedByUser(int p0);
 public void setVersion(com.idega.core.version.data.ICVersion p0);
 public void setVersionID(int p0);
}
