package com.idega.core.location.data;


public class ProvinceHomeImpl extends com.idega.data.IDOFactory implements ProvinceHome
{
 protected Class getEntityInterfaceClass(){
  return Province.class;
 }


 public Province create() throws javax.ejb.CreateException{
  return (Province) super.createIDO();
 }


public Province findByProvinceNameAndCountryId(java.lang.String p0,int p1)throws java.rmi.RemoteException,javax.ejb.FinderException{
	com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
	Object pk = ((ProvinceBMPBean)entity).ejbFindByProvinceNameAndCountryId(p0,p1);
	this.idoCheckInPooledEntity(entity);
	return this.findByPrimaryKey(pk);
}

 public Province findByPrimaryKey(Object pk) throws javax.ejb.FinderException{
  return (Province) super.findByPrimaryKeyIDO(pk);
 }



}