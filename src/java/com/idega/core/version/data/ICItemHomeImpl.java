package com.idega.core.version.data;


public class ICItemHomeImpl extends com.idega.data.IDOFactory implements ICItemHome
{
 protected Class getEntityInterfaceClass(){
  return ICItem.class;
 }


 public ICItem create() throws javax.ejb.CreateException{
  return (ICItem) super.createIDO();
 }


 public ICItem findByPrimaryKey(Object pk) throws javax.ejb.FinderException{
  return (ICItem) super.findByPrimaryKeyIDO(pk);
 }



}