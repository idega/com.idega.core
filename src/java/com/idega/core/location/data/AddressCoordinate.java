/*
 * $Id: AddressCoordinate.java,v 1.1 2005/01/20 09:42:13 gimmi Exp $
 * Created on 20.1.2005
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
 *  Last modified: $Date: 2005/01/20 09:42:13 $ by $Author: gimmi $
 * 
 * @author <a href="mailto:gimmi@idega.com">gimmi</a>
 * @version $Revision: 1.1 $
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
}
