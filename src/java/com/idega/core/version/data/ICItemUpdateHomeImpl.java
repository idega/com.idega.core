package com.idega.core.version.data;


public class ICItemUpdateHomeImpl extends com.idega.data.IDOFactory implements ICItemUpdateHome
{
 protected Class getEntityInterfaceClass(){
  return ICItemUpdate.class;
 }


 public ICItemUpdate create() throws javax.ejb.CreateException{
  return (ICItemUpdate) super.createIDO();
 }


 public ICItemUpdate findByPrimaryKey(Object pk) throws javax.ejb.FinderException{
  return (ICItemUpdate) super.findByPrimaryKeyIDO(pk);
 }



}