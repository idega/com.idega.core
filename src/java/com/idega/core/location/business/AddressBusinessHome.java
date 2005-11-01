/*
 * $Id: AddressBusinessHome.java,v 1.4 2005/11/01 16:10:59 eiki Exp $
 * Created on Nov 1, 2005
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
 *  Last modified: $Date: 2005/11/01 16:10:59 $ by $Author: eiki $
 * 
 * @author <a href="mailto:eiki@idega.com">eiki</a>
 * @version $Revision: 1.4 $
 */
public interface AddressBusinessHome extends IBOHome {

	public AddressBusiness create() throws javax.ejb.CreateException, java.rmi.RemoteException;
}
