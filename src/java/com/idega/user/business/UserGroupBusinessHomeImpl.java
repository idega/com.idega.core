package com.idega.user.business;


public class UserGroupBusinessHomeImpl extends com.idega.business.IBOHomeImpl implements UserGroupBusinessHome
{
 protected Class getBeanInterfaceClass(){
  return UserGroupBusiness.class;
 }


 public UserGroupBusiness create() throws javax.ejb.CreateException{
  return (UserGroupBusiness) super.createIBO();
 }



}