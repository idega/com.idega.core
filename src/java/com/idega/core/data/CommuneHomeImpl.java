package com.idega.core.data;


public class CommuneHomeImpl extends com.idega.data.IDOFactory implements CommuneHome
{
 protected Class getEntityInterfaceClass(){
  return Commune.class;
 }


 public Commune create() throws javax.ejb.CreateException{
  return (Commune) super.createIDO();
 }


public java.util.Collection findAllCommunes()throws javax.ejb.FinderException{
	com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
	java.util.Collection ids = ((CommuneBMPBean)entity).ejbFindAllCommunes();
	this.idoCheckInPooledEntity(entity);
	return this.getEntityCollectionForPrimaryKeys(ids);
}

public Commune findByCommuneNameAndProvince(java.lang.String p0,java.lang.Object p1)throws javax.ejb.FinderException{
	com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
	Object pk = ((CommuneBMPBean)entity).ejbFindByCommuneNameAndProvince(p0,p1);
	this.idoCheckInPooledEntity(entity);
	return this.findByPrimaryKey(pk);
}

 public Commune findByPrimaryKey(Object pk) throws javax.ejb.FinderException{
  return (Commune) super.findByPrimaryKeyIDO(pk);
 }



}