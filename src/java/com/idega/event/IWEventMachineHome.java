package com.idega.event;


public interface IWEventMachineHome extends com.idega.business.IBOHome
{
 public IWEventMachine create() throws javax.ejb.CreateException, java.rmi.RemoteException;

}