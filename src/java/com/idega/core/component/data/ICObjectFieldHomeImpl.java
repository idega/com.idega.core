package com.idega.core.component.data;


public class ICObjectFieldHomeImpl extends com.idega.data.IDOFactory implements ICObjectFieldHome
{
 protected Class getEntityInterfaceClass(){
  return ICObjectField.class;
 }


 public ICObjectField create() throws javax.ejb.CreateException{
  return (ICObjectField) super.createIDO();
 }


 public ICObjectField findByPrimaryKey(Object pk) throws javax.ejb.FinderException{
  return (ICObjectField) super.findByPrimaryKeyIDO(pk);
 }



}