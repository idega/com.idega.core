package com.idega.user.business;


public class UserStatusBusinessHomeImpl extends com.idega.business.IBOHomeImpl implements UserStatusBusinessHome
{
 protected Class getBeanInterfaceClass(){
  return UserStatusBusiness.class;
 }


 public UserStatusBusiness create() throws javax.ejb.CreateException{
  return (UserStatusBusiness) super.createIBO();
 }



}