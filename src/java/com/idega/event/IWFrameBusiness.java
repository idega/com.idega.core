package com.idega.event;



public interface IWFrameBusiness extends com.idega.business.IBOSession
{
 public com.idega.presentation.FrameTable getFrameSet(java.lang.String p0) throws java.rmi.RemoteException;
 public com.idega.presentation.Page getFrame(java.lang.String p0,java.lang.String p1) throws java.rmi.RemoteException;
 public java.lang.String retainFrameSet(com.idega.presentation.FrameTable p0) throws java.rmi.RemoteException;
 public java.lang.String getFrameSetIdentifier(com.idega.presentation.FrameTable p0) throws java.rmi.RemoteException;
 public java.lang.String getFrameSetIdentifier(com.idega.idegaweb.IWLocation p0) throws java.rmi.RemoteException;
 public com.idega.presentation.Page getFrame(com.idega.idegaweb.IWLocation p0) throws java.rmi.RemoteException;
}
