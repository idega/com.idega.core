/*
 * $Id: AddressCoordinateBMPBean.java,v 1.2 2005/02/04 00:08:31 gimmi Exp $
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
 *  Last modified: $Date: 2005/02/04 00:08:31 $ by $Author: gimmi $
 * 
 * @author <a href="mailto:gimmi@idega.com">gimmi</a>
 * @version $Revision: 1.2 $
 */
public class AddressCoordinateBMPBean extends GenericEntity  implements AddressCoordinate{

	private static final String TABLE_NAME = "IC_ADDRESS_COORDINATE";
	private static final String COLUMN_COORDINATE = "COORDINATE";
	private static final String COLUMN_COMMUNE_ID = "IC_COMMUNE_ID";
	private static final String COLUMN_CODE = "COORDINATE_CODE";
	
	public String getEntityName() {
		return TABLE_NAME;
	}

	public void initializeAttributes() {
		addAttribute(getIDColumnName());
		addManyToOneRelationship(COLUMN_COMMUNE_ID, Commune.class);
		addAttribute(COLUMN_COORDINATE, "coordinate", true, true, String.class, 50);
		addAttribute(COLUMN_CODE, "Coordinate_code", true, true, String.class, 10);
	}
	
	public void setCoordinate(String coordinate) {
		setColumn(COLUMN_COORDINATE, coordinate);
	}
	
	public String getCoordinate() {
		return getStringColumnValue(COLUMN_COORDINATE);
	}
	
	public void setCommune(Commune commune) {
		this.setColumn(COLUMN_COMMUNE_ID, commune);
	}
	
	public Commune getCommune() {
		return (Commune) getColumnValue(COLUMN_COMMUNE_ID);
	}
	
	public void setCoordinateCode(String code) {
		setColumn(COLUMN_CODE, code);
	}
	
	public String getCoordinateCode() {
		return getStringColumnValue(COLUMN_CODE);
	}
	
	public Object ejbFindByCoordinate(String coordinate) throws FinderException {
		return this.idoFindOnePKByColumnBySQL(COLUMN_COORDINATE, coordinate);
	}
	
	
}
