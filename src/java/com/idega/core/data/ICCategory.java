package com.idega.core.data;

import javax.ejb.*;

public interface ICCategory extends com.idega.data.TreeableEntity,com.idega.core.business.Category
{
 public void setValid(boolean p0);
 public boolean getValid();
 public void setLocaleId(int p0);
 public int getBusinessId();
 public java.lang.String getCategoryType();
 public void setName(java.lang.String p0);
 public void initializeAttributes();
 public void setDescription(java.lang.String p0);
 public java.lang.String getDescription();
 public void setType(java.lang.String p0);
 public java.sql.Timestamp getCreated();
 public java.lang.String getName();
 public void setBusinessId(int p0);
 public int getParentId();
 public java.lang.String getType();
 public int getLocaleId();
 public void setParentId(int p0);
 public void setCreated(java.sql.Timestamp p0);
}
