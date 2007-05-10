package com.idega.core.location.data;


import com.idega.data.IDOEntity;

public interface Street extends IDOEntity {

	/**
	 * @see com.idega.core.location.data.StreetBMPBean#setName
	 */
	public void setName(String name);

	/**
	 * @see com.idega.core.location.data.StreetBMPBean#getName
	 */
	public String getName();

	/**
	 * @see com.idega.core.location.data.StreetBMPBean#setNameDativ
	 */
	public void setNameDativ(String name);

	/**
	 * @see com.idega.core.location.data.StreetBMPBean#getNameDativ
	 */
	public String getNameDativ();

	/**
	 * @see com.idega.core.location.data.StreetBMPBean#setPostalCode
	 */
	public void setPostalCode(PostalCode postalCode);

	/**
	 * @see com.idega.core.location.data.StreetBMPBean#getPostalCode
	 */
	public PostalCode getPostalCode();
}