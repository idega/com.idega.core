package com.idega.core.location.business;


public class AddressBusinessHomeImpl extends com.idega.business.IBOHomeImpl implements AddressBusinessHome
{
 protected Class getBeanInterfaceClass(){
  return AddressBusiness.class;
 }


 public AddressBusiness create() throws javax.ejb.CreateException{
  return (AddressBusiness) super.createIBO();
 }



}