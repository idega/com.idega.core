/*
 * $Id: RealEstateBMPBean.java,v 1.4 2008/04/16 18:33:26 valdas Exp $ Created on Mar 26, 2007
 * 
 * Copyright (C) 2007 Idega Software hf. All Rights Reserved.
 * 
 * This software is the proprietary information of Idega hf. Use is subject to license terms.
 */
package com.idega.core.location.data;

import javax.ejb.FinderException;

import com.idega.data.IDOQuery;

public class RealEstateBMPBean extends com.idega.data.GenericEntity implements RealEstate {

	// length of description field
	private static final int DESCRIPTION = 255;
	
	// length of fields that are identifiers (equal to size of primary keys)
	private static final int IDENTIFIER = 22;

	// long input fields
	private static final int LONG_INPUT_FIELD = 255;

	private static final String COLUMN_REAL_ESTATE_NUMBER = "REAl_ESTATE_NUMBER";
	
	private static final String COLUMN_REAL_ESTATE_UNIT = "REAL_ESTATE_UNIT";

	private static final String COLUMN_REAL_ESTATE_CODE = "REAL_ESTATE_CODE";
	
	private static final String COLUMN_LAND_REGISTER_MAP_NUMBER = "LAND_REGISTER";

	private static final String COLUMN_NAME = "NAME";

	private static final String COLUMN_USE = "REAL_ESTATE_USE";

	private static final String COLUMN_COMMENT = "REAL_ESTATE_COMMENT";

	private static final String COLUMN_STREET_NUMBER = "STREET_NUMBER";

	private static final String COLUMN_STREET_ID = "STREET_ID";

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.idega.data.GenericEntity#getEntityName()
	 */
	public String getEntityName() {
		return "ic_real_estate";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.idega.data.GenericEntity#initializeAttributes()
	 */
	public void initializeAttributes() {
		addAttribute(getIDColumnName());
		addAttribute(COLUMN_REAL_ESTATE_NUMBER, "real estate number", String.class, LONG_INPUT_FIELD);
		addAttribute(COLUMN_REAL_ESTATE_UNIT, "real estate unit", String.class, LONG_INPUT_FIELD);
		addAttribute(COLUMN_REAL_ESTATE_CODE, "real estate code", String.class, LONG_INPUT_FIELD);
		addAttribute(COLUMN_NAME, "name", String.class, LONG_INPUT_FIELD);
		addAttribute(COLUMN_USE, "use of the real estate", String.class, DESCRIPTION);
		addAttribute(COLUMN_COMMENT, "some explanation", String.class, DESCRIPTION);
		addAttribute(COLUMN_STREET_NUMBER, "street number", String.class, LONG_INPUT_FIELD);
		// pseudo pointer to land register map number 
		addAttribute(COLUMN_LAND_REGISTER_MAP_NUMBER, "land register map number", String.class, IDENTIFIER);
		// pointers to other entities
		addManyToOneRelationship(COLUMN_STREET_ID, Street.class);
	}

	public void setRealEstateNumber(String realEstateNumber) {
		setColumn(COLUMN_REAL_ESTATE_NUMBER, realEstateNumber);
	}

	public String getRealEstateNumber() {
		return (String) getColumnValue(COLUMN_REAL_ESTATE_NUMBER);
	}
	
	public void setRealEstateUnit(String unit) {
		setColumn(COLUMN_REAL_ESTATE_UNIT, unit);
	}
	
	public String getRealEstateUnit() {
		return (String) getColumnValue(COLUMN_REAL_ESTATE_UNIT);
	}

	public void setRealEstateCode(String realEstateCode) {
		setColumn(COLUMN_REAL_ESTATE_CODE, realEstateCode);
	}

	public String getRealEstateCode() {
		return (String) getColumnValue(COLUMN_REAL_ESTATE_CODE);
	}

	public void setName(String name) {
		setColumn(COLUMN_NAME, name);
	}

	public String getName() {
		return (String) getColumnValue(COLUMN_NAME);
	}

	public void setUse(String use) {
		setColumn(COLUMN_USE, use);
	}

	public String getUse() {
		return (String) getColumnValue(COLUMN_USE);
	}

	public void setComment(String comment) {
		setColumn(COLUMN_COMMENT, comment);
	}

	public String getComment() {
		return (String) getColumnValue(COLUMN_COMMENT);
	}

	public void setLandRegisterMapNumber(String landRegisterMapNumber) {
		setColumn(COLUMN_LAND_REGISTER_MAP_NUMBER, landRegisterMapNumber);
	}

	public String getLandRegisterMapNumber() {
		return (String) getColumnValue(COLUMN_LAND_REGISTER_MAP_NUMBER);
	}

	public void setStreetNumber(String streetNumber) {
		setColumn(COLUMN_STREET_NUMBER, streetNumber);
	}

	public String getStreetNumber() {
		return (String) getColumnValue(COLUMN_STREET_NUMBER);
	}
	
	public void setStreet(Street street) {
		setColumn(COLUMN_STREET_ID, street);
	}

	public Street getStreet() {
		return (Street) getColumnValue(COLUMN_STREET_ID);
	}

	public void setStreetID(Integer streetID) {
		setColumn(COLUMN_STREET_ID, streetID);
	}

	public Integer getStreetID() {
		return getIntegerColumnValue(COLUMN_STREET_ID);
	}
	
	public boolean isDummy() {
		return 
			getLandRegisterMapNumber() == null &&
			getRealEstateNumber() == null &&
			getRealEstateUnit() == null &&
			getRealEstateCode() == null;
	}

	public Object ejbFindRealEstateByRealEstateIdentifier(String landRegisterMapNumber, String number, String unit, String code) throws FinderException {
		IDOQuery query = idoQueryGetSelect();
		query.appendWhere();
		// land register number
		if (landRegisterMapNumber == null) {
			query.append(COLUMN_LAND_REGISTER_MAP_NUMBER).appendIsNull();
		}
		else {
			query.appendEqualsQuoted(COLUMN_LAND_REGISTER_MAP_NUMBER, landRegisterMapNumber);
		}
		// real estate number
		if (number == null) {
			query.appendAndIsNull(COLUMN_REAL_ESTATE_NUMBER);
		}
		else {
			query.appendAnd().appendEqualsQuoted(COLUMN_REAL_ESTATE_NUMBER, number);
		}
		// real estate unit
		if (unit == null) {
			query.appendAndIsNull(COLUMN_REAL_ESTATE_UNIT);
		}
		else {
			query.appendAnd().appendEqualsQuoted(COLUMN_REAL_ESTATE_UNIT, unit);
		}
		// real estate code
		if (code == null) {
			query.appendAndIsNull(COLUMN_REAL_ESTATE_CODE);
		}
		else {
			query.appendAnd().appendEqualsQuoted(COLUMN_REAL_ESTATE_CODE, code);
		}
		return idoFindOnePKByQuery(query);
	}

}
