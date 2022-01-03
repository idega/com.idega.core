package com.idega.core.location.data;


import java.rmi.RemoteException;

import com.idega.data.ExplicitlySynchronizedEntity;
import com.idega.data.IDOLegacyEntity;

public interface Address extends IDOLegacyEntity, ExplicitlySynchronizedEntity {
	String FIELD_STREET_NAME = "StreetName";
	String FIELD_STREET_NUMBER = "StreetNumber";
	/**
	 * @see com.idega.core.location.data.AddressBMPBean#setDefaulValues
	 */
	public void setDefaulValues();

	/**
	 * @see com.idega.core.location.data.AddressBMPBean#getName
	 */
	@Override
	public String getName();

	/**
	 * @see com.idega.core.location.data.AddressBMPBean#getStreetAddressNominative
	 */
	public String getStreetAddressNominative();

	/**
	 * @see com.idega.core.location.data.AddressBMPBean#setStreetAddressNominative
	 */
	public void setStreetAddressNominative(String address);

	/**
	 * @see com.idega.core.location.data.AddressBMPBean#getStreetNameOriginal
	 */
	public String getStreetNameOriginal();

	/**
	 * @see com.idega.core.location.data.AddressBMPBean#getStreetName
	 */
	public String getStreetName();

	/**
	 * @see com.idega.core.location.data.AddressBMPBean#setStreetName
	 */
	public void setStreetName(String street_name);

	/**
	 * @see com.idega.core.location.data.AddressBMPBean#getStreetNumber
	 */
	public String getStreetNumber();

	/**
	 * @see com.idega.core.location.data.AddressBMPBean#setStreetNumber
	 */
	public void setStreetNumber(String street_number);

	/**
	 * @see com.idega.core.location.data.AddressBMPBean#setStreetNumber
	 */
	public void setStreetNumber(int street_number);

	/**
	 * @see com.idega.core.location.data.AddressBMPBean#getCity
	 */
	public String getCity();

	/**
	 * @see com.idega.core.location.data.AddressBMPBean#setCity
	 */
	public void setCity(String city);

	/**
	 * @see com.idega.core.location.data.AddressBMPBean#getProvince
	 */
	public String getProvince();

	/**
	 * @see com.idega.core.location.data.AddressBMPBean#setProvince
	 */
	public void setProvince(String province);

	/**
	 * @see com.idega.core.location.data.AddressBMPBean#getPOBox
	 */
	public String getPOBox();

	/**
	 * @see com.idega.core.location.data.AddressBMPBean#setPOBox
	 */
	public void setPOBox(String p_o_box);

	/**
	 * @see com.idega.core.location.data.AddressBMPBean#getPostalCode
	 */
	public PostalCode getPostalCode();

	/**
	 * @see com.idega.core.location.data.AddressBMPBean#getPostalCodeID
	 */
	public int getPostalCodeID();

	/**
	 * @see com.idega.core.location.data.AddressBMPBean#setPostalCode
	 */
	public void setPostalCode(PostalCode postalCode);

	/**
	 * @see com.idega.core.location.data.AddressBMPBean#setPostalCodeID
	 */
	public void setPostalCodeID(int postal_code_id);

	/**
	 * @see com.idega.core.location.data.AddressBMPBean#getAddressType
	 */
	public AddressType getAddressType();

	/**
	 * @see com.idega.core.location.data.AddressBMPBean#setAddressTypeID
	 */
	public void setAddressTypeID(int address_type_id);

	/**
	 * @see com.idega.core.location.data.AddressBMPBean#setAddressType
	 */
	public void setAddressType(AddressType type);

	/**
	 * @see com.idega.core.location.data.AddressBMPBean#getAddressTypeID
	 */
	public int getAddressTypeID();

	/**
	 * @see com.idega.core.location.data.AddressBMPBean#getCoordinate
	 */
	public AddressCoordinate getCoordinate();

	/**
	 * @see com.idega.core.location.data.AddressBMPBean#setCoordinate
	 */
	public void setCoordinate(AddressCoordinate coordinate);

	/**
	 * @see com.idega.core.location.data.AddressBMPBean#getCountry
	 */
	public Country getCountry();

	/**
	 * @see com.idega.core.location.data.AddressBMPBean#getCountryId
	 */
	public int getCountryId();

	/**
	 * @see com.idega.core.location.data.AddressBMPBean#setCountry
	 */
	public void setCountry(Country country);

	/**
	 * @see com.idega.core.location.data.AddressBMPBean#setCountryId
	 */
	public void setCountryId(int country_id);

	/**
	 * @see com.idega.core.location.data.AddressBMPBean#getCommuneID
	 */
	public int getCommuneID();

	/**
	 * @see com.idega.core.location.data.AddressBMPBean#setCommuneID
	 */
	public void setCommuneID(int communeId);

	/**
	 * @see com.idega.core.location.data.AddressBMPBean#setCommune
	 */
	public void setCommune(Commune commune);

	/**
	 * @see com.idega.core.location.data.AddressBMPBean#getCommune
	 */
	public Commune getCommune();

	/**
	 * @see com.idega.core.location.data.AddressBMPBean#getAddressTypeHome
	 */
	public AddressTypeHome getAddressTypeHome() throws RemoteException;

	/**
	 * @see com.idega.core.location.data.AddressBMPBean#getStreetAddress
	 */
	public String getStreetAddress();

	/**
	 * @see com.idega.core.location.data.AddressBMPBean#getPostalAddress
	 */
	public String getPostalAddress();

	/**
	 * @see com.idega.core.location.data.AddressBMPBean#isEqualTo
	 */
	public boolean isEqualTo(Address address);

	public String getAddress();

	public String getAppartmentNumber();

	public void setAppartmentNumber(String appartmentNumber);

}