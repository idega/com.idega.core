package com.idega.core.data;

import javax.ejb.*;

public interface PostalCode extends com.idega.data.IDOLegacyEntity
{
 public com.idega.core.data.Country getCountry();
 public int getCountryID();
 public java.lang.String getName();
 public java.lang.String getPostalCode();
 public void setCountry(com.idega.core.data.Country p0);
 public void setCountryID(int p0);
 public void setName(java.lang.String p0);
 public void setPostalCode(java.lang.String p0);
}
