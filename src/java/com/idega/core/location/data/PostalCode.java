package com.idega.core.location.data;

import java.util.Collection;


public interface PostalCode extends com.idega.data.IDOEntity
{
 public com.idega.core.location.data.Country getCountry();
 public int getCountryID();
 public java.lang.String getName();
 public java.lang.String getPostalAddress();
 public java.lang.String getPostalCode();
 public void initializeAttributes();
 public void setCountry(com.idega.core.location.data.Country p0);
 public void setCountryID(int p0);
 public void setName(java.lang.String p0);
 public void setPostalCode(java.lang.String p0);
 public boolean isEqualTo(PostalCode postal);
 public Collection getAddresses();
}
