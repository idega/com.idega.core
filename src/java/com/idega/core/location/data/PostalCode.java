package com.idega.core.location.data;


import java.util.Collection;
import com.idega.data.IDOStoreException;
import com.idega.data.IDOEntity;

public interface PostalCode extends IDOEntity {

	/**
	 * @see com.idega.core.location.data.PostalCodeBMPBean#setPostalCode
	 */
	public void setPostalCode(String code);

	/**
	 * @see com.idega.core.location.data.PostalCodeBMPBean#getPostalCode
	 */
	public String getPostalCode();

	/**
	 * @see com.idega.core.location.data.PostalCodeBMPBean#setCommuneID
	 */
	public void setCommuneID(String commune);

	/**
	 * @see com.idega.core.location.data.PostalCodeBMPBean#getCommuneID
	 */
	public String getCommuneID();

	/**
	 * @see com.idega.core.location.data.PostalCodeBMPBean#setCommune
	 */
	public void setCommune(Commune commune);

	/**
	 * @see com.idega.core.location.data.PostalCodeBMPBean#getCommune
	 */
	public Commune getCommune();

	/**
	 * @see com.idega.core.location.data.PostalCodeBMPBean#setName
	 */
	public void setName(String name);

	/**
	 * @see com.idega.core.location.data.PostalCodeBMPBean#getName
	 */
	public String getName();

	/**
	 * @see com.idega.core.location.data.PostalCodeBMPBean#setCountry
	 */
	public void setCountry(Country country);

	/**
	 * @see com.idega.core.location.data.PostalCodeBMPBean#getCountry
	 */
	public Country getCountry();

	/**
	 * @see com.idega.core.location.data.PostalCodeBMPBean#setCountryID
	 */
	public void setCountryID(int country_id);

	/**
	 * @see com.idega.core.location.data.PostalCodeBMPBean#getCountryID
	 */
	public int getCountryID();

	/**
	 * @see com.idega.core.location.data.PostalCodeBMPBean#getPostalAddress
	 */
	public String getPostalAddress();

	/**
	 * @see com.idega.core.location.data.PostalCodeBMPBean#getAddresses
	 */
	public Collection getAddresses();

	/**
	 * @see com.idega.core.location.data.PostalCodeBMPBean#isEqualTo
	 */
	public boolean isEqualTo(PostalCode postal);

	/**
	 * @see com.idega.core.location.data.PostalCodeBMPBean#store
	 */
	public void store() throws IDOStoreException;
}