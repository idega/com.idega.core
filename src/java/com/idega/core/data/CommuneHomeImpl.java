package com.idega.core.data;


public class CommuneHomeImpl extends com.idega.data.IDOFactory implements CommuneHome
{
 protected Class getEntityInterfaceClass(){
  return Commune.class;
 }


 public Commune create() throws javax.ejb.CreateException{
  return (Commune) super.createIDO();
 }


public Commune findByCommuneNameAndProvinceId(java.lang.String p0,int p1)throws javax.ejb.FinderException,java.rmi.RemoteException{
	com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
	Object pk = ((CommuneBMPBean)entity).ejbFindByCommuneNameAndProvinceId(p0,p1);
	this.idoCheckInPooledEntity(entity);
	return this.findByPrimaryKey(pk);
}

 public Commune findByPrimaryKey(Object pk) throws javax.ejb.FinderException{
  return (Commune) super.findByPrimaryKeyIDO(pk);
 }



}