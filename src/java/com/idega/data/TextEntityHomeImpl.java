package com.idega.data;


public class TextEntityHomeImpl extends com.idega.data.IDOFactory implements TextEntityHome
{
 protected Class getEntityInterfaceClass(){
  return TextEntity.class;
 }


 public TextEntity create() throws javax.ejb.CreateException{
  return (TextEntity) super.createIDO();
 }


 public TextEntity findByPrimaryKey(Object pk) throws javax.ejb.FinderException{
  return (TextEntity) super.findByPrimaryKeyIDO(pk);
 }



}