package com.idega.user.business;


public class UserBusinessHomeImpl extends com.idega.business.IBOHomeImpl implements UserBusinessHome
{
 protected Class getBeanInterfaceClass(){
  return UserBusiness.class;
 }


 public UserBusiness create() throws javax.ejb.CreateException{
  return (UserBusiness) super.createIBO();
 }



}