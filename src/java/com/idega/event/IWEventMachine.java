package com.idega.event;


public interface IWEventMachine extends com.idega.business.IBOSession
{
 public void processEvent(com.idega.presentation.Page p0,com.idega.presentation.IWContext p1) throws java.rmi.RemoteException;
 public javax.swing.event.EventListenerList getListenersFor(com.idega.core.component.data.ICObjectInstance p0) throws java.rmi.RemoteException;
 public javax.swing.event.EventListenerList getListenersFor(java.lang.String p0) throws java.rmi.RemoteException;
 public javax.swing.event.EventListenerList getListenersFor(com.idega.idegaweb.IWLocation p0) throws java.rmi.RemoteException;
 public javax.swing.event.EventListenerList getListenersForCompoundId(String compoundId) throws java.rmi.RemoteException;
}
