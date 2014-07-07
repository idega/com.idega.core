package com.idega.core.contact.data;

import java.util.Collection;
import java.util.Collections;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ejb.FinderException;


public class PhoneHomeImpl extends com.idega.data.IDOFactory implements PhoneHome
{
 protected Class getEntityInterfaceClass(){
  return Phone.class;
 }

 public Phone create() throws javax.ejb.CreateException{
  return (Phone) super.idoCreate();
 }

 public Phone createLegacy(){
	try{
		return create();
	}
	catch(javax.ejb.CreateException ce){
		throw new RuntimeException("CreateException:"+ce.getMessage());
	}

 }

 public Phone findByPrimaryKey(int id) throws javax.ejb.FinderException{
  return (Phone) super.idoFindByPrimaryKey(id);
 }

 public Phone findByPrimaryKey(Object pk) throws javax.ejb.FinderException{
  return (Phone) super.idoFindByPrimaryKey(pk);
 }

 public Phone findByPrimaryKeyLegacy(int id) throws java.sql.SQLException{
	try{
		return findByPrimaryKey(id);
	}
	catch(javax.ejb.FinderException fe){
		throw new java.sql.SQLException(fe.getMessage());
	}

 }

public Phone findUsersHomePhone(com.idega.user.data.User user)throws javax.ejb.FinderException,java.rmi.RemoteException
{
	com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
	Object pk = ((PhoneBMPBean)entity).ejbFindUsersHomePhone(user);
	this.idoCheckInPooledEntity(entity);
	return this.findByPrimaryKey(pk);
}

public Phone findUsersWorkPhone(com.idega.user.data.User user)throws javax.ejb.FinderException,java.rmi.RemoteException
{
	com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
	Object pk = ((PhoneBMPBean)entity).ejbFindUsersWorkPhone(user);
	this.idoCheckInPooledEntity(entity);
	return this.findByPrimaryKey(pk);
}

public Phone findUsersMobilePhone(com.idega.user.data.User user)throws javax.ejb.FinderException,java.rmi.RemoteException
{
	com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
	Object pk = ((PhoneBMPBean)entity).ejbFindUsersMobilePhone(user);
	this.idoCheckInPooledEntity(entity);
	return this.findByPrimaryKey(pk);
}

public Collection findUsersPhones(int userId,int type){
	try{
		com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
		Collection pks = ((PhoneBMPBean)entity).ejbFindUsersPhones(userId, type);
		this.idoCheckInPooledEntity(entity);
		return getEntityCollectionForPrimaryKeys(pks);
	}
	catch (FinderException e) {
	}
	catch(Exception e){
		getLogger().log(Level.WARNING, "Failed finding phones for user " + userId + " and type " + type, e);
	}
	return Collections.EMPTY_LIST;
}
private Logger getLogger(){
	return Logger.getLogger(PhoneHomeImpl.class.getName());
}
public Phone findUsersFaxPhone(com.idega.user.data.User user)throws javax.ejb.FinderException,java.rmi.RemoteException
{
	com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
	Object pk = ((PhoneBMPBean)entity).ejbFindUsersFaxPhone(user);
	this.idoCheckInPooledEntity(entity);
	return this.findByPrimaryKey(pk);
}



}