package com.idega.idegaweb.block.business;


public class FolderBlockBusinessHomeImpl extends com.idega.business.IBOHomeImpl implements FolderBlockBusinessHome
{
 protected Class getBeanInterfaceClass(){
  return FolderBlockBusiness.class;
 }


 public FolderBlockBusiness create() throws javax.ejb.CreateException{
  return (FolderBlockBusiness) super.createIBO();
 }



}