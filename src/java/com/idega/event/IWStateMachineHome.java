package com.idega.event;


public interface IWStateMachineHome extends com.idega.business.IBOHome
{
 public IWStateMachine create() throws javax.ejb.CreateException, java.rmi.RemoteException;

}