package com.idega.core.location.data;


public interface Commune extends com.idega.data.IDOEntity
{
 public java.lang.String getCommuneCode();
 public java.lang.String getCommuneName();
 public boolean getIsDefault();
 public boolean getIsValid();
 public com.idega.core.location.data.Province getProvince();
 public int getProvinceID();
 public void initializeAttributes();
 public void setCommuneCode(java.lang.String p0);
 public void setCommuneName(java.lang.String p0);
 public void setIsDefault(boolean p0);
 public void setIsValid(boolean p0);
 public void setProvince(com.idega.core.location.data.Province p0);
 public void setProvinceID(int p0);
 public void setValid(boolean p0);
}
