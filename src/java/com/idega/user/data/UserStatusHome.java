package com.idega.user.data;


public interface UserStatusHome extends com.idega.data.IDOHome
{
 public UserStatus create() throws javax.ejb.CreateException;
 public UserStatus findByPrimaryKey(Object pk) throws javax.ejb.FinderException;
 public java.util.Collection findAll()throws javax.ejb.FinderException;
 public java.util.Collection findAllByGroupId(int p0)throws javax.ejb.FinderException;
 public java.util.Collection findAllByUserId(int p0)throws javax.ejb.FinderException;
 public java.util.Collection findAllByUserIdAndGroupId(int p0,int p1)throws javax.ejb.FinderException;

}