package com.idega.event;

import javax.ejb.*;

public interface IWStateMachine extends com.idega.business.IBOSession
{
 public com.idega.event.IWPresentationState getStateFor(com.idega.core.data.ICObjectInstance p0) throws java.rmi.RemoteException;
 public com.idega.event.IWPresentationState getStateFor(com.idega.idegaweb.IWLocation p0) throws java.rmi.RemoteException;
 public com.idega.event.IWPresentationState getStateFor(com.idega.idegaweb.IWLocation p0, java.lang.Class p1) throws java.rmi.RemoteException;
 public com.idega.event.IWPresentationState getStateFor(String compoundId, Class stateClassType) throws java.rmi.RemoteException;
}
