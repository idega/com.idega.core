/*
 * $Id: UUIDBusinessHome.java,v 1.3 2004/09/16 17:49:08 eiki Exp $
 * Created on Sep 13, 2004
 *
 * Copyright (C) 2004 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.core.idgenerator.business;

import com.idega.business.IBOHome;


/**
 * 
 *  Last modified: $Date: 2004/09/16 17:49:08 $ by $Author: eiki $
 * 
 * @author <a href="mailto:eiki@idega.com">eiki</a>
 * @version $Revision: 1.3 $
 */
public interface UUIDBusinessHome extends IBOHome {

	public UUIDBusiness create() throws javax.ejb.CreateException, java.rmi.RemoteException;
}
