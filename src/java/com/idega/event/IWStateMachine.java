package com.idega.event;

import javax.ejb.*;

public interface IWStateMachine extends com.idega.business.IBOSession
{
 public com.idega.event.IWPresentationState getStateFor(com.idega.core.data.ICObjectInstance p0) throws java.rmi.RemoteException;
}
