/*
 * $Id: AddressCoordinateBMPBean.java,v 1.1 2005/01/20 09:42:13 gimmi Exp $
 * Created on 20.1.2005
 *
 * Copyright (C) 2005 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.core.location.data;

import javax.ejb.FinderException;
import com.idega.data.GenericEntity;


/**
 * 
 *  Last modified: $Date: 2005/01/20 09:42:13 $ by $Author: gimmi $
 * 
 * @author <a href="mailto:gimmi@idega.com">gimmi</a>
 * @version $Revision: 1.1 $
 */
public class AddressCoordinateBMPBean extends GenericEntity  implements AddressCoordinate{

	private static final String TABLE_NAME = "IC_ADDRESS_COORDINATE";
	private static final String COLUMN_ADDRESS_ID = "IC_ADDRESS_ID";
	private static final String COLUMN_COORDINATE = "COORDINATE";
	
	public String getEntityName() {
		return TABLE_NAME;
	}

	public void initializeAttributes() {
		addAttribute(getIDColumnName());
		addAttribute(COLUMN_COORDINATE, "coordinate", true, true, String.class, 50);
	}
	
	public void setCoordinate(String coordinate) {
		setColumn(COLUMN_COORDINATE, coordinate);
	}
	
	public String getCoordinate() {
		return getStringColumnValue(COLUMN_COORDINATE);
	}
	
	public Object ejbFindByCoordinate(String coordinate) throws FinderException {
		return this.idoFindOnePKByColumnBySQL(COLUMN_COORDINATE, coordinate);
	}
	
	
}
