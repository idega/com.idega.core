package com.idega.core.ldap.business;


public class LDAPUserBusinessHomeImpl extends com.idega.business.IBOHomeImpl implements LDAPUserBusinessHome
{
 protected Class getBeanInterfaceClass(){
  return LDAPUserBusiness.class;
 }


 public LDAPUserBusiness create() throws javax.ejb.CreateException{
  return (LDAPUserBusiness) super.createIBO();
 }



}