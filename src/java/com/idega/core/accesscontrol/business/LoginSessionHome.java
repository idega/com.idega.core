/*
 * $Id: LoginSessionHome.java,v 1.1 2004/09/07 13:52:18 aron Exp $
 * Created on 3.9.2004
 *
 * Copyright (C) 2004 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.core.accesscontrol.business;


import com.idega.business.IBOHome;

/**
 * 
 *  Last modified: $Date: 2004/09/07 13:52:18 $ by $Author: aron $
 * 
 * @author <a href="mailto:aron@idega.com">aron</a>
 * @version $Revision: 1.1 $
 */
public interface LoginSessionHome extends IBOHome {
    public LoginSession create() throws javax.ejb.CreateException,
            java.rmi.RemoteException;

}
