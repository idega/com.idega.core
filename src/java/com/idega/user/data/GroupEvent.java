package com.idega.user.data;

import javax.ejb.*;

public interface GroupEvent extends com.idega.data.IDOEntity
{
 public com.idega.user.data.Group getGroup() throws java.rmi.RemoteException;
 public void setGroup(com.idega.user.data.Group p0) throws java.rmi.RemoteException;
}
