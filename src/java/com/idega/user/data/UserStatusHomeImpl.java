package com.idega.user.data;


public class UserStatusHomeImpl extends com.idega.data.IDOFactory implements UserStatusHome
{
 protected Class getEntityInterfaceClass(){
  return UserStatus.class;
 }


 public UserStatus create() throws javax.ejb.CreateException{
  return (UserStatus) super.createIDO();
 }


 public UserStatus findByPrimaryKey(Object pk) throws javax.ejb.FinderException{
  return (UserStatus) super.findByPrimaryKeyIDO(pk);
 }



}