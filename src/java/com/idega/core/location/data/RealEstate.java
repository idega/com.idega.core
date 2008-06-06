package com.idega.core.location.data;


import com.idega.data.IDOEntity;

public interface RealEstate extends IDOEntity {

	/**
	 * @see com.idega.core.location.data.RealEstateBMPBean#setRealEstateNumber
	 */
	public void setRealEstateNumber(String realEstateNumber);

	/**
	 * @see com.idega.core.location.data.RealEstateBMPBean#getRealEstateNumber
	 */
	public String getRealEstateNumber();

	/**
	 * @see com.idega.core.location.data.RealEstateBMPBean#setRealEstateUnit
	 */
	public void setRealEstateUnit(String unit);

	/**
	 * @see com.idega.core.location.data.RealEstateBMPBean#getRealEstateUnit
	 */
	public String getRealEstateUnit();

	/**
	 * @see com.idega.core.location.data.RealEstateBMPBean#setRealEstateCode
	 */
	public void setRealEstateCode(String realEstateCode);

	/**
	 * @see com.idega.core.location.data.RealEstateBMPBean#getRealEstateCode
	 */
	public String getRealEstateCode();

	/**
	 * @see com.idega.core.location.data.RealEstateBMPBean#setName
	 */
	public void setName(String name);

	/**
	 * @see com.idega.core.location.data.RealEstateBMPBean#getName
	 */
	public String getName();

	/**
	 * @see com.idega.core.location.data.RealEstateBMPBean#setUse
	 */
	public void setUse(String use);

	/**
	 * @see com.idega.core.location.data.RealEstateBMPBean#getUse
	 */
	public String getUse();

	/**
	 * @see com.idega.core.location.data.RealEstateBMPBean#setComment
	 */
	public void setComment(String comment);

	/**
	 * @see com.idega.core.location.data.RealEstateBMPBean#getComment
	 */
	public String getComment();

	/**
	 * @see com.idega.core.location.data.RealEstateBMPBean#setLandRegisterMapNumber
	 */
	public void setLandRegisterMapNumber(String landRegisterMapNumber);

	/**
	 * @see com.idega.core.location.data.RealEstateBMPBean#getLandRegisterMapNumber
	 */
	public String getLandRegisterMapNumber();

	/**
	 * @see com.idega.core.location.data.RealEstateBMPBean#setStreetNumber
	 */
	public void setStreetNumber(String streetNumber);

	/**
	 * @see com.idega.core.location.data.RealEstateBMPBean#getStreetNumber
	 */
	public String getStreetNumber();

	/**
	 * @see com.idega.core.location.data.RealEstateBMPBean#setStreet
	 */
	public void setStreet(Street street);

	/**
	 * @see com.idega.core.location.data.RealEstateBMPBean#getStreet
	 */
	public Street getStreet();

	/**
	 * @see com.idega.core.location.data.RealEstateBMPBean#setStreetID
	 */
	public void setStreetID(Integer streetID);

	/**
	 * @see com.idega.core.location.data.RealEstateBMPBean#getStreetID
	 */
	public Integer getStreetID();
	
	/**
	 * @see com.idega.core.location.data.RealEstateBMPBean#isDummy
	 */
	public boolean isDummy();
}