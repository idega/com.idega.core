package com.idega.core.contact.data;


public class EmailHomeImpl extends com.idega.data.IDOFactory implements EmailHome
{
 protected Class getEntityInterfaceClass(){
  return Email.class;
 }


 public Email create() throws javax.ejb.CreateException{
  return (Email) super.createIDO();
 }


 public Email createLegacy(){
	try{
		return create();
	}
	catch(javax.ejb.CreateException ce){
		throw new RuntimeException("CreateException:"+ce.getMessage());
	}

 }


public java.util.Collection findEmailsForUser(com.idega.user.data.User p0)throws java.rmi.RemoteException,javax.ejb.FinderException{
	com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
	java.util.Collection ids = ((EmailBMPBean)entity).ejbFindEmailsForUser(p0);
	this.idoCheckInPooledEntity(entity);
	return this.getEntityCollectionForPrimaryKeys(ids);
}

public java.util.Collection findEmailsForUser(int p0)throws javax.ejb.FinderException{
	com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
	java.util.Collection ids = ((EmailBMPBean)entity).ejbFindEmailsForUser(p0);
	this.idoCheckInPooledEntity(entity);
	return this.getEntityCollectionForPrimaryKeys(ids);
}

public Email findEmailByAddress(java.lang.String p0)throws javax.ejb.FinderException{
	com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
	Object pk = ((EmailBMPBean)entity).ejbFindEmailByAddress(p0);
	this.idoCheckInPooledEntity(entity);
	return this.findByPrimaryKey(pk);
}

 public Email findByPrimaryKey(Object pk) throws javax.ejb.FinderException{
  return (Email) super.findByPrimaryKeyIDO(pk);
 }


 public Email findByPrimaryKey(int id) throws javax.ejb.FinderException{
  return (Email) super.findByPrimaryKeyIDO(id);
 }


 public Email findByPrimaryKeyLegacy(int id) throws java.sql.SQLException{
	try{
		return findByPrimaryKey(id);
	}
	catch(javax.ejb.FinderException fe){
		throw new java.sql.SQLException(fe.getMessage());
	}

 }



}