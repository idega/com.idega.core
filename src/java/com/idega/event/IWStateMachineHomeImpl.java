package com.idega.event;


public class IWStateMachineHomeImpl extends com.idega.business.IBOHomeImpl implements IWStateMachineHome
{
 protected Class getBeanInterfaceClass(){
  return IWStateMachine.class;
 }


 public IWStateMachine create() throws javax.ejb.CreateException{
  return (IWStateMachine) super.createIBO();
 }



}