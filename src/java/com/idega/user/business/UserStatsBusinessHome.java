/*
 * Created on Jan 20, 2005
 */
package com.idega.user.business;

import com.idega.business.IBOHome;

/**
 * @author Sigtryggur
 */
public interface UserStatsBusinessHome extends IBOHome {
    public UserStatsBusiness create() throws javax.ejb.CreateException,
            java.rmi.RemoteException;

}
