package com.idega.core.data;


public class PostalCodeHomeImpl extends com.idega.data.IDOFactory implements PostalCodeHome
{
 protected Class getEntityInterfaceClass(){
  return PostalCode.class;
 }


 public PostalCode create() throws javax.ejb.CreateException{
  return (PostalCode) super.createIDO();
 }


public PostalCode findByPostalCodeAndCountryId(java.lang.String p0,int p1)throws java.rmi.RemoteException,javax.ejb.FinderException{
	com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
	Object pk = ((PostalCodeBMPBean)entity).ejbFindByPostalCodeAndCountryId(p0,p1);
	this.idoCheckInPooledEntity(entity);
	return this.findByPrimaryKey(pk);
}

 public PostalCode findByPrimaryKey(Object pk) throws javax.ejb.FinderException{
  return (PostalCode) super.findByPrimaryKeyIDO(pk);
 }



}