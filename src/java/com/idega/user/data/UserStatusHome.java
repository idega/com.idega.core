package com.idega.user.data;


public interface UserStatusHome extends com.idega.data.IDOHome
{
 public UserStatus create() throws javax.ejb.CreateException;
 public UserStatus findByPrimaryKey(Object pk) throws javax.ejb.FinderException;

}