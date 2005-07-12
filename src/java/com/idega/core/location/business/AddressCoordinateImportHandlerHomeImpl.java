/*
 * $Id: AddressCoordinateImportHandlerHomeImpl.java,v 1.3 2005/07/12 14:22:30 palli Exp $
 * Created on 3.2.2005
 *
 * Copyright (C) 2005 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.core.location.business;

import com.idega.business.IBOHomeImpl;


/**
 * 
 *  Last modified: $Date: 2005/07/12 14:22:30 $ by $Author: palli $
 * 
 * @author <a href="mailto:gimmi@idega.com">gimmi</a>
 * @version $Revision: 1.3 $
 */
public class AddressCoordinateImportHandlerHomeImpl extends IBOHomeImpl implements AddressCoordinateImportHandlerHome {

	protected Class getBeanInterfaceClass() {
		return AddressCoordinateImportHandler.class;
	}

	public AddressCoordinateImportHandler create() throws javax.ejb.CreateException {
		return (AddressCoordinateImportHandler) super.createIBO();
	}
}
