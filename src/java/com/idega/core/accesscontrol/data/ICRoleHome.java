package com.idega.core.accesscontrol.data;


public interface ICRoleHome extends com.idega.data.IDOHome
{
 public ICRole create() throws javax.ejb.CreateException;
 public ICRole findByPrimaryKey(Object pk) throws javax.ejb.FinderException;
 public java.util.Collection<ICRole> findAllRoles()throws javax.ejb.FinderException;

}