/*
 * $Id: AddressCoordinateHome.java,v 1.2 2005/02/04 00:08:31 gimmi Exp $
 * Created on 3.2.2005
 *
 * Copyright (C) 2005 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.core.location.data;

import javax.ejb.FinderException;
import com.idega.data.IDOHome;


/**
 * 
 *  Last modified: $Date: 2005/02/04 00:08:31 $ by $Author: gimmi $
 * 
 * @author <a href="mailto:gimmi@idega.com">gimmi</a>
 * @version $Revision: 1.2 $
 */
public interface AddressCoordinateHome extends IDOHome {

	public AddressCoordinate create() throws javax.ejb.CreateException;

	public AddressCoordinate findByPrimaryKey(Object pk) throws javax.ejb.FinderException;

	/**
	 * @see com.idega.core.location.data.AddressCoordinateBMPBean#ejbFindByCoordinate
	 */
	public AddressCoordinate findByCoordinate(String coordinate) throws FinderException;
}
