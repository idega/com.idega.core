package com.idega.core.version.data;


public interface ICItem extends com.idega.data.IDOEntity
{
 public com.idega.user.data.User getCreatedByUser();
 public int getCreatedByUserID();
 public java.sql.Timestamp getCreatedTimestamp();
 public com.idega.core.version.data.ICVersion getCurrentOpenVersion();
 public int getCurrentOpenVersionID();
 public java.lang.String getDescription();
 public java.lang.String getName();
 public void initializeAttributes();
 public void setCreatedByUser(com.idega.user.data.User p0);
 public void setCreatedByUser(int p0);
 public void setCreatedTimestamp(java.sql.Timestamp p0);
 public void setCurrentOpenVersion(com.idega.core.version.data.ICVersion p0);
 public void setCurrentOpenVersionID(int p0);
 public void setDescription(java.lang.String p0);
 public void setName(java.lang.String p0);
}
