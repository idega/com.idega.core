package com.idega.core.accesscontrol.data;


public interface LoginInfoHome extends com.idega.data.IDOHome
{
 public LoginInfo create() throws javax.ejb.CreateException;
 public LoginInfo findByPrimaryKey(Object pk) throws javax.ejb.FinderException;

}