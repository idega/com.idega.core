/*
 * Created on 24.8.2004
 *
 * Copyright (C) 2004 Idega hf. All Rights Reserved.
 *
 *  This software is the proprietary information of Idega hf.
 *  Use is subject to license terms.
 */
package com.idega.user.business;



import com.idega.business.IBOHome;

/**
 * @author aron
 *
 * UserStatusBusinessHome TODO Describe this type
 */
public interface UserStatusBusinessHome extends IBOHome {
    public UserStatusBusiness create() throws javax.ejb.CreateException,
            java.rmi.RemoteException;

}
