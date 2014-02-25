package com.idega.core.data;

import com.idega.data.TreeableEntity;


public interface ICBusiness extends TreeableEntity<ICBusiness>
{
 public java.sql.Timestamp getCreated();
 public java.lang.String getDescription();
 public java.lang.String getName();
 public java.lang.String getType();
 public boolean getValid();
 public void setCreated(java.sql.Timestamp p0);
 public void setDescription(java.lang.String p0);
 public void setName(java.lang.String p0);
 public void setType(java.lang.String p0);
 public void setValid(boolean p0);
}
