package com.idega.core.data;


public interface PostalCode extends com.idega.data.IDOEntity
{
 public com.idega.core.data.Country getCountry();
 public int getCountryID();
 public java.lang.String getName();
 public java.lang.String getPostalAddress();
 public java.lang.String getPostalCode();
 public void initializeAttributes();
 public void setCountry(com.idega.core.data.Country p0);
 public void setCountryID(int p0);
 public void setName(java.lang.String p0);
 public void setPostalCode(java.lang.String p0);
}
