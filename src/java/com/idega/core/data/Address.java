package com.idega.core.data;

import javax.ejb.*;

public interface Address extends com.idega.data.IDOLegacyEntity
{
 public com.idega.core.data.AddressType getAddressType();
 public int getAddressTypeID();
 public java.lang.String getCity();
 public com.idega.core.data.Country getCountry();
 public int getCountryId();
 public java.lang.String getName();
 public java.lang.String getPOBox();
 public com.idega.core.data.PostalCode getPostalCode()throws java.sql.SQLException;
 public int getPostalCodeID();
 public java.lang.String getProvidence();
 public java.lang.String getStreetName();
 public java.lang.String getStreetNumber();
 public void setAddressTypeID(int p0);
 public void setCity(java.lang.String p0);
 public void setCountryId(int p0);
 public void setDefaulValues();
 public void setPOBox(java.lang.String p0);
 public void setPostalCodeID(int p0);
 public void setProvidence(java.lang.String p0);
 public void setStreetName(java.lang.String p0);
 public void setStreetNumber(java.lang.String p0);
 public void setStreetNumber(int p0);
}
