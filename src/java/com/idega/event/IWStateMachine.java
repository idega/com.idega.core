package com.idega.event;

public interface IWStateMachine extends com.idega.business.IBOSession
{
 public com.idega.event.IWPresentationState getStateFor(com.idega.core.component.data.ICObjectInstance p0) throws java.rmi.RemoteException;
 public com.idega.event.IWPresentationState getStateFor(com.idega.idegaweb.IWLocation p0) throws java.rmi.RemoteException;
 public com.idega.event.IWPresentationState getStateFor(com.idega.idegaweb.IWLocation p0, java.lang.Class p1) throws java.rmi.RemoteException;
 public com.idega.event.IWPresentationState getStateFor(String compoundId, Class stateClassType) throws java.rmi.RemoteException;

	/**
	 * 
	 * @uml.property name="allControllers"
	 */
	public java.util.Collection getAllControllers()
		throws java.rmi.RemoteException;

	/**
	 * 
	 * @uml.property name="allChangeListeners"
	 */
	public java.util.Collection getAllChangeListeners()
		throws java.rmi.RemoteException;

	
	public void unload();
}
