package com.idega.user.data;


public interface GroupDomainRelationTypeHome extends com.idega.data.IDOHome
{
 public GroupDomainRelationType create() throws javax.ejb.CreateException, java.rmi.RemoteException;
 public GroupDomainRelationType findByPrimaryKey(Object pk) throws javax.ejb.FinderException, java.rmi.RemoteException;
 public com.idega.user.data.GroupDomainRelationType getTopNodeRelationType()throws javax.ejb.FinderException,java.rmi.RemoteException, java.rmi.RemoteException;

}