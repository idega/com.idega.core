package com.idega.versioncontrol.business;


public class UpdateServiceHomeImpl extends com.idega.business.IBOHomeImpl implements UpdateServiceHome
{
 protected Class getBeanInterfaceClass(){
  return UpdateService.class;
 }


 public UpdateService create() throws javax.ejb.CreateException{
  return (UpdateService) super.createIBO();
 }



}