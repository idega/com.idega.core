package com.idega.user.business;


public class GroupBusinessHomeImpl extends com.idega.business.IBOHomeImpl implements GroupBusinessHome
{
 protected Class getBeanInterfaceClass(){
  return GroupBusiness.class;
 }


 public GroupBusiness create() throws javax.ejb.CreateException{
  return (GroupBusiness) super.createIBO();
 }



}