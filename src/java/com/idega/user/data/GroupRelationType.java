package com.idega.user.data;

import javax.ejb.*;

public interface GroupRelationType extends com.idega.data.IDOEntity
{
 public java.lang.String getType() throws java.rmi.RemoteException;
 public void setType(java.lang.String p0) throws java.rmi.RemoteException;
}
