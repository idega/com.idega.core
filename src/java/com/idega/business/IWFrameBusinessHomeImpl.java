package com.idega.business;


public class IWFrameBusinessHomeImpl extends com.idega.business.IBOHomeImpl implements IWFrameBusinessHome
{
 protected Class getBeanInterfaceClass(){
  return IWFrameBusiness.class;
 }


 public IWFrameBusiness create() throws javax.ejb.CreateException{
  return (IWFrameBusiness) super.createIBO();
 }



}