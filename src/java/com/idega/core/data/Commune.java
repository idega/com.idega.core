package com.idega.core.data;


public interface Commune extends com.idega.data.IDOEntity
{
 public java.lang.String getCommuneCode();
 public java.lang.String getCommuneName();
 public com.idega.core.data.Province getProvince();
 public int getProvinceID();
 public void initializeAttributes();
 public void setCommuneCode(java.lang.String p0);
 public void setCommuneName(java.lang.String p0);
 public void setProvince(com.idega.core.data.Province p0);
 public void setProvinceID(int p0);
}
