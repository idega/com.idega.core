/*
 * $Id: AddressCoordinateHomeImpl.java,v 1.2 2005/02/04 00:08:31 gimmi Exp $
 * Created on 3.2.2005
 *
 * Copyright (C) 2005 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.core.location.data;

import javax.ejb.FinderException;
import com.idega.data.IDOFactory;


/**
 * 
 *  Last modified: $Date: 2005/02/04 00:08:31 $ by $Author: gimmi $
 * 
 * @author <a href="mailto:gimmi@idega.com">gimmi</a>
 * @version $Revision: 1.2 $
 */
public class AddressCoordinateHomeImpl extends IDOFactory implements AddressCoordinateHome {

	protected Class getEntityInterfaceClass() {
		return AddressCoordinate.class;
	}

	public AddressCoordinate create() throws javax.ejb.CreateException {
		return (AddressCoordinate) super.createIDO();
	}

	public AddressCoordinate findByPrimaryKey(Object pk) throws javax.ejb.FinderException {
		return (AddressCoordinate) super.findByPrimaryKeyIDO(pk);
	}

	public AddressCoordinate findByCoordinate(String coordinate) throws FinderException {
		com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
		Object pk = ((AddressCoordinateBMPBean) entity).ejbFindByCoordinate(coordinate);
		this.idoCheckInPooledEntity(entity);
		return this.findByPrimaryKey(pk);
	}
}
