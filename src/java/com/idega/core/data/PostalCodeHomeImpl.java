package com.idega.core.data;


public class PostalCodeHomeImpl extends com.idega.data.IDOFactory implements PostalCodeHome
{
 protected Class getEntityInterfaceClass(){
  return PostalCode.class;
 }


 public PostalCode create() throws javax.ejb.CreateException{
  return (PostalCode) super.createIDO();
 }


public java.util.Collection findAll()throws javax.ejb.FinderException,java.rmi.RemoteException{
	com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
	java.util.Collection ids = ((PostalCodeBMPBean)entity).ejbFindAll();
	this.idoCheckInPooledEntity(entity);
	return this.getEntityCollectionForPrimaryKeys(ids);
}

public java.util.Collection findAllByCountryIdOrderedByPostalCode(int p0)throws javax.ejb.FinderException,java.rmi.RemoteException{
	com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
	java.util.Collection ids = ((PostalCodeBMPBean)entity).ejbFindAllByCountryIdOrderedByPostalCode(p0);
	this.idoCheckInPooledEntity(entity);
	return this.getEntityCollectionForPrimaryKeys(ids);
}

public java.util.Collection findAllOrdererByCode()throws javax.ejb.FinderException,java.rmi.RemoteException{
	com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
	java.util.Collection ids = ((PostalCodeBMPBean)entity).ejbFindAllOrdererByCode();
	this.idoCheckInPooledEntity(entity);
	return this.getEntityCollectionForPrimaryKeys(ids);
}

public PostalCode findByPostalCodeAndCountryId(java.lang.String p0,int p1)throws javax.ejb.FinderException,java.rmi.RemoteException{
	com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
	Object pk = ((PostalCodeBMPBean)entity).ejbFindByPostalCodeAndCountryId(p0,p1);
	this.idoCheckInPooledEntity(entity);
	return this.findByPrimaryKey(pk);
}

 public PostalCode findByPrimaryKey(Object pk) throws javax.ejb.FinderException{
  return (PostalCode) super.findByPrimaryKeyIDO(pk);
 }


public java.util.Collection findAllUniqueNames()throws java.rmi.RemoteException,javax.ejb.FinderException{
	com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
	java.util.Collection theReturn = ((PostalCodeBMPBean)entity).ejbHomeFindAllUniqueNames();
	this.idoCheckInPooledEntity(entity);
	return theReturn;
}

public java.util.Collection findByName(java.lang.String p0)throws javax.ejb.FinderException{
	com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
	java.util.Collection theReturn = ((PostalCodeBMPBean)entity).ejbHomeFindByName(p0);
	this.idoCheckInPooledEntity(entity);
	return theReturn;
}

public java.util.Collection findByPostalCode(java.lang.String p0)throws javax.ejb.FinderException{
	com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
	java.util.Collection theReturn = ((PostalCodeBMPBean)entity).ejbHomeFindByPostalCode(p0);
	this.idoCheckInPooledEntity(entity);
	return theReturn;
}

public java.util.Collection findByPostalCodeFromTo(java.lang.String p0,java.lang.String p1)throws javax.ejb.FinderException{
	com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
	java.util.Collection theReturn = ((PostalCodeBMPBean)entity).ejbHomeFindByPostalCodeFromTo(p0,p1);
	this.idoCheckInPooledEntity(entity);
	return theReturn;
}


}