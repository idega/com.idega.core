package com.idega.event;


public class IWEventMachineHomeImpl extends com.idega.business.IBOHomeImpl implements IWEventMachineHome
{
 protected Class getBeanInterfaceClass(){
  return IWEventMachine.class;
 }


 public IWEventMachine create() throws javax.ejb.CreateException{
  return (IWEventMachine) super.createIBO();
 }



}