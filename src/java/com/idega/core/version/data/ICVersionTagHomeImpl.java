package com.idega.core.version.data;


public class ICVersionTagHomeImpl extends com.idega.data.IDOFactory implements ICVersionTagHome
{
 protected Class getEntityInterfaceClass(){
  return ICVersionTag.class;
 }


 public ICVersionTag create() throws javax.ejb.CreateException{
  return (ICVersionTag) super.createIDO();
 }


 public ICVersionTag findByPrimaryKey(Object pk) throws javax.ejb.FinderException{
  return (ICVersionTag) super.findByPrimaryKeyIDO(pk);
 }



}