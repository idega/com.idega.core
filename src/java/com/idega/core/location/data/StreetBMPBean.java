/*
 * $Id: StreetBMPBean.java,v 1.2 2007/05/10 22:36:31 thomas Exp $
 * Created on Mar 26, 2007
 *
 * Copyright (C) 2007 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.core.location.data;

import javax.ejb.FinderException;
import com.idega.data.GenericEntity;
import com.idega.data.IDOQuery;


/**
 * 
 *  Last modified: $Date: 2007/05/10 22:36:31 $ by $Author: thomas $
 * 
 * @author <a href="mailto:thomas@idega.com">thomas</a>
 * @version $Revision: 1.2 $
 */
public class StreetBMPBean extends GenericEntity implements Street {
	
	
	// long input fields 
	private static final int LONG_INPUT_FIELD = 255;
	
	private static final String COLUMN_NAME = "STREET_NAME";
	
	private static final String COLUMN_NAME_DATIV = "NAME_DATIV";
	
	private static final String COLUMN_POSTAL_CODE_ID = "POSTAL_CODE_ID";

	/* (non-Javadoc)
	 * @see com.idega.data.GenericEntity#getEntityName()
	 */
	public String getEntityName() {
		return "ic_street";
	}
	

	/* (non-Javadoc)
	 * @see com.idega.data.GenericEntity#initializeAttributes()
	 */
	public void initializeAttributes() {
		addAttribute(getIDColumnName());
		addAttribute(COLUMN_NAME, "name", String.class, LONG_INPUT_FIELD);
		addAttribute(COLUMN_NAME_DATIV, "name dativ", String.class, LONG_INPUT_FIELD);
		// pointers to other entities
		addManyToOneRelationship(COLUMN_POSTAL_CODE_ID, PostalCode.class);
	}
	
	public void setName(String name) {
		setColumn(COLUMN_NAME, name);
	}
	
	public String getName() {
		return (String) getColumnValue(COLUMN_NAME);
	}
	
	public void setNameDativ(String name) {
		setColumn(COLUMN_NAME_DATIV, name);
	}
	
	public String getNameDativ() {
		return (String) getColumnValue(COLUMN_NAME_DATIV);
	}
	
	public void setPostalCode(PostalCode postalCode) {
		setColumn(COLUMN_POSTAL_CODE_ID, postalCode);
	}
	
	public PostalCode getPostalCode() {
		return (PostalCode) getColumnValue(COLUMN_POSTAL_CODE_ID);
	}
	
	public Object ejbFindStreetByPostalCodeAndNameOrNameDativ(PostalCode postalCode, String name, String nameDativ) throws FinderException {
	    IDOQuery query = idoQueryGetSelect();
	    query.appendWhereEquals(COLUMN_POSTAL_CODE_ID, postalCode);
	    query.appendAnd();
	    query.appendLeftParenthesis();
	    query.appendEqualsQuoted(COLUMN_NAME, name);
	    query.appendOr();
	    query.appendEqualsQuoted(COLUMN_NAME_DATIV, nameDativ);
	    query.appendRightParenthesis();
	    return idoFindOnePKByQuery(query);
	 }
	
}
