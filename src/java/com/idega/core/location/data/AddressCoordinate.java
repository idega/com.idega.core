/*
 * $Id: AddressCoordinate.java,v 1.2 2005/02/04 00:08:31 gimmi Exp $
 * Created on 3.2.2005
 *
 * Copyright (C) 2005 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.core.location.data;

import com.idega.data.IDOEntity;


/**
 * 
 *  Last modified: $Date: 2005/02/04 00:08:31 $ by $Author: gimmi $
 * 
 * @author <a href="mailto:gimmi@idega.com">gimmi</a>
 * @version $Revision: 1.2 $
 */
public interface AddressCoordinate extends IDOEntity {

	/**
	 * @see com.idega.core.location.data.AddressCoordinateBMPBean#setCoordinate
	 */
	public void setCoordinate(String coordinate);

	/**
	 * @see com.idega.core.location.data.AddressCoordinateBMPBean#getCoordinate
	 */
	public String getCoordinate();

	/**
	 * @see com.idega.core.location.data.AddressCoordinateBMPBean#setCommune
	 */
	public void setCommune(Commune commune);

	/**
	 * @see com.idega.core.location.data.AddressCoordinateBMPBean#getCommune
	 */
	public Commune getCommune();

	/**
	 * @see com.idega.core.location.data.AddressCoordinateBMPBean#setCoordinateCode
	 */
	public void setCoordinateCode(String code);

	/**
	 * @see com.idega.core.location.data.AddressCoordinateBMPBean#getCoordinateCode
	 */
	public String getCoordinateCode();
}
