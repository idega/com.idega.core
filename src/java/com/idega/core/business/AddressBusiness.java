package com.idega.core.business;

import javax.ejb.*;

public interface AddressBusiness extends com.idega.business.IBOService
{
 public com.idega.core.data.AddressHome getAddressHome() throws java.rmi.RemoteException;
}
