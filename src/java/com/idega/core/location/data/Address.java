package com.idega.core.location.data;



public interface Address extends com.idega.data.IDOLegacyEntity
{
 String FIELD_STREET_NAME = "StreetName";
	public void setPOBox(java.lang.String p0);
 public java.lang.String getPOBox();
 public int getAddressTypeID();
 public void setPostalCodeID(int p0);
 public void setDefaulValues();
 public void setStreetNumber(java.lang.String p0);
 public com.idega.core.location.data.AddressType getAddressType();
 public void setPostalCode(com.idega.core.location.data.PostalCode p0);
 public java.lang.String getCity();
 public void setCountry(com.idega.core.location.data.Country p0);
 public void setAddressTypeID(int p0);
 public void setCountryId(int p0);
 public void setStreetNumber(int p0);
 public java.lang.String getName();
 public java.lang.String getStreetNumber();
 public com.idega.core.location.data.AddressTypeHome getAddressTypeHome()throws java.rmi.RemoteException;
 public java.lang.String getStreetName();
 public com.idega.core.location.data.PostalCode getPostalCode();
 public void setAddressType(com.idega.core.location.data.AddressType p0);
 public void setStreetName(java.lang.String p0);
 public void setProvince(java.lang.String p0);
 public com.idega.core.location.data.Country getCountry();
 public int getCountryId();
 public int getPostalCodeID();
 public void setCity(java.lang.String p0);
 public java.lang.String getProvince();
 public int getCommuneID();
 public void setCommuneID(int communeId);
 public boolean isEqualTo(Address address);
 
 public AddressCoordinate getCoordinate();
 public void setCoordinate(AddressCoordinate coordinate);
 
     /**
     * Gets the street name together with the number
     */
 public String getStreetAddress();
     
     /**
     * Gets the postal code together with its name
     */
 public String getPostalAddress();

 /**
  * Sets the Commune (Municipality) for this address
  * @param commune
  */
 public void setCommune(Commune commune);
}
