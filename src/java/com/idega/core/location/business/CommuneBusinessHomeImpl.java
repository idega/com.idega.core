package com.idega.core.location.business;


public class CommuneBusinessHomeImpl extends com.idega.business.IBOHomeImpl implements CommuneBusinessHome
{
 protected Class getBeanInterfaceClass(){
  return CommuneBusiness.class;
 }


 public CommuneBusiness create() throws javax.ejb.CreateException{
  return (CommuneBusiness) super.createIBO();
 }



}