package com.idega.core.data;

import javax.ejb.*;

public interface Address extends com.idega.data.IDOLegacyEntity
{
 public void setPOBox(java.lang.String p0);
 public java.lang.String getPOBox();
 public int getAddressTypeID();
 public void setPostalCodeID(int p0);
 public void setDefaulValues();
 public void setStreetNumber(java.lang.String p0);
 public com.idega.core.data.AddressType getAddressType();
 public void setPostalCode(com.idega.core.data.PostalCode p0);
 public java.lang.String getCity();
 public void setCountry(com.idega.core.data.Country p0);
 public void setAddressTypeID(int p0);
 public void setCountryId(int p0);
 public void setStreetNumber(int p0);
 public java.lang.String getName();
 public java.lang.String getStreetNumber();
 public com.idega.core.data.AddressTypeHome getAddressTypeHome()throws java.rmi.RemoteException;
 public java.lang.String getStreetName();
 public com.idega.core.data.PostalCode getPostalCode()throws java.sql.SQLException;
 public void setAddressType(com.idega.core.data.AddressType p0);
 public void setStreetName(java.lang.String p0);
 public void setProvince(java.lang.String p0);
 public com.idega.core.data.Country getCountry();
 public int getCountryId();
 public int getPostalCodeID();
 public void setCity(java.lang.String p0);
 public java.lang.String getProvince();
 public String getStreetAddress();
 public String getPostalAddress();

}
