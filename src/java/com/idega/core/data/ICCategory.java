package com.idega.core.data;

import javax.ejb.*;

public interface ICCategory extends com.idega.data.TreeableEntity,com.idega.core.business.Category
{
 public int getBusinessId();
 public java.lang.String getCategoryType();
 public java.sql.Timestamp getCreated();
 public java.lang.String getDescription();
 public int getLocaleId();
 public java.lang.String getName();
 public int getParentId();
 public java.lang.String getType();
 public boolean getValid();
 public void setBusinessId(int p0);
 public void setCreated(java.sql.Timestamp p0);
 public void setDefaultValues();
 public void setDescription(java.lang.String p0);
 public void setLocaleId(int p0);
 public void setName(java.lang.String p0);
 public void setParentId(int p0);
 public void setType(java.lang.String p0);
 public void setValid(boolean p0);
}
