package com.idega.core.ldap.business;


public class LDAPGroupBusinessHomeImpl extends com.idega.business.IBOHomeImpl implements LDAPGroupBusinessHome
{
 protected Class getBeanInterfaceClass(){
  return LDAPGroupBusiness.class;
 }


 public LDAPGroupBusiness create() throws javax.ejb.CreateException{
  return (LDAPGroupBusiness) super.createIBO();
 }



}