package com.idega.event;

import com.idega.business.IBOHome;


public interface IWFrameBusinessHome extends com.idega.business.IBOHome
{
 public IWFrameBusiness create() throws javax.ejb.CreateException, java.rmi.RemoteException;

}