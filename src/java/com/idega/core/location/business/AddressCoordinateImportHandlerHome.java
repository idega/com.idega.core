/*
 * $Id: AddressCoordinateImportHandlerHome.java,v 1.1 2005/02/04 00:09:24 gimmi Exp $
 * Created on 3.2.2005
 *
 * Copyright (C) 2005 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.core.location.business;

import com.idega.business.IBOHome;


/**
 * 
 *  Last modified: $Date: 2005/02/04 00:09:24 $ by $Author: gimmi $
 * 
 * @author <a href="mailto:gimmi@idega.com">gimmi</a>
 * @version $Revision: 1.1 $
 */
public interface AddressCoordinateImportHandlerHome extends IBOHome {

	public AddressCoordinateImportHandler create() throws javax.ejb.CreateException, java.rmi.RemoteException;
}
