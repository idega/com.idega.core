/*
 * $Id: RealEstateBMPBean.java,v 1.2 2007/05/10 22:36:31 thomas Exp $ Created on Mar 26, 2007
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

	// long input fields
	private static final int LONG_INPUT_FIELD = 255;

	private static final String COLUMN_REAL_ESTATE_NUMBER = "REAl_ESTATE_NUMBER";

	private static final String COLUMN_REAL_ESTATE_CODE = "REAL_ESTATE_CODE";

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
		addAttribute(COLUMN_REAL_ESTATE_CODE, "real estate code", String.class, LONG_INPUT_FIELD);
		addAttribute(COLUMN_NAME, "name", String.class, LONG_INPUT_FIELD);
		addAttribute(COLUMN_USE, "use of the real estate", String.class, DESCRIPTION);
		addAttribute(COLUMN_COMMENT, "some explanation", String.class, DESCRIPTION);
		addAttribute(COLUMN_STREET_NUMBER, "street number", String.class, LONG_INPUT_FIELD);
		// pointers to other entities
		addManyToOneRelationship(COLUMN_STREET_ID, Street.class);
	}

	public void setRealEstateNumber(String realEstateNumber) {
		setColumn(COLUMN_REAL_ESTATE_NUMBER, realEstateNumber);
	}

	public String getRealEstateNumber() {
		return (String) getColumnValue(COLUMN_REAL_ESTATE_NUMBER);
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

	public Object ejbFindRealEstateByNumber(String number) throws FinderException {
		IDOQuery query = idoQueryGetSelect();
		query.appendWhere();
		query.appendEqualsQuoted(COLUMN_REAL_ESTATE_NUMBER, number);
		return idoFindOnePKByQuery(query);
	}

}
