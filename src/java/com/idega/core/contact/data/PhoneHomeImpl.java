package com.idega.core.contact.data;

import java.util.Collection;
import java.util.Collections;
import java.util.logging.Level;

import javax.ejb.CreateException;
import javax.ejb.FinderException;

import com.idega.data.IDOStoreException;
import com.idega.util.ListUtil;
import com.idega.util.StringUtil;



public class PhoneHomeImpl extends com.idega.data.IDOFactory implements PhoneHome
{
	private static final long serialVersionUID = 6038610517398600545L;

protected Class getEntityInterfaceClass(){
  return Phone.class;
 }

 public Phone create() throws javax.ejb.CreateException{
  return (Phone) super.createEntity();
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
  return (Phone) super.findByPrimaryKeyIDO(pk);
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
	return this.findByPrimaryKey(pk);
}

public Phone findUsersWorkPhone(com.idega.user.data.User user)throws javax.ejb.FinderException,java.rmi.RemoteException
{
	com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
	Object pk = ((PhoneBMPBean)entity).ejbFindUsersWorkPhone(user);
	return this.findByPrimaryKey(pk);
}

public Phone findUsersMobilePhone(com.idega.user.data.User user)throws javax.ejb.FinderException,java.rmi.RemoteException
{
	com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
	Object pk = ((PhoneBMPBean)entity).ejbFindUsersMobilePhone(user);
	return this.findByPrimaryKey(pk);
}

public Phone findUsersFaxPhone(com.idega.user.data.User user)throws javax.ejb.FinderException,java.rmi.RemoteException
{
	com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
	Object pk = ((PhoneBMPBean)entity).ejbFindUsersFaxPhone(user);
	return this.findByPrimaryKey(pk);
}

	/*
	 * (non-Javadoc)
	 * @see com.idega.core.contact.data.PhoneHome#findByPhoneNumber(java.lang.String)
	 */
	@Override
	public Collection<Phone> findByPhoneNumber(String phoneNumber) {
		PhoneBMPBean entity = (PhoneBMPBean) idoCheckOutPooledEntity();
		Collection<Object> ids = ((PhoneBMPBean)entity).ejbFindByPhoneNumber(phoneNumber);
		try {
			return getEntityCollectionForPrimaryKeys(ids);
		} catch (FinderException e) {
			java.util.logging.Logger.getLogger(getClass().getName()).log(
					Level.WARNING, 
					"Failed to get " + getEntityInterfaceClass().getName() + 
					"'s by primary keys: " + ids);
		}

		return Collections.emptyList();
	}

	/*
	 * (non-Javadoc)
	 * @see com.idega.core.contact.data.PhoneHome#update(java.lang.String, java.lang.String)
	 */
	@Override
	public Phone update(String primaryKey, String phoneNumber, String phoneTypeId) {
		Phone phone = null;
		if (!StringUtil.isEmpty(primaryKey)) {
			try {
				phone = findByPrimaryKey(primaryKey);
			} catch (FinderException e) {
				java.util.logging.Logger.getLogger(getClass().getName()).log(
						Level.WARNING, "Phone by primary key: '" + primaryKey + 
						"' not found! Will check by phone number!");
			}
		}

		if (!StringUtil.isEmpty(phoneNumber)) {
			if (phone == null) {
				Collection<Phone> phones = findByPhoneNumber(phoneNumber);
				if (!ListUtil.isEmpty(phones)) {
					phone = phones.iterator().next();
				} else {
					try {
						phone = createEntity();
					} catch (CreateException e) {
						java.util.logging.Logger.getLogger(getClass().getName()).log(
								Level.WARNING, 
								"Failed to create " + this.getClass().getName() + 
								" cause of: ", e);
						return null;
					}
				}
			}
			
			phone.setNumber(phoneNumber);
		}

		if (phone == null) {
			return null;
		}

		if (!StringUtil.isEmpty(phoneTypeId)) {
			try {
				phone.setPhoneTypeId(Integer.valueOf(phoneTypeId));
			} catch (NumberFormatException e) {
				java.util.logging.Logger.getLogger(getClass().getName()).log(
						Level.WARNING, 
						"Failed to convert " + phoneTypeId + 
						" to: " + Integer.class.getName());
			}
		}
		
		try {
			phone.store();
			return phone;
		} catch (IDOStoreException e) {
			java.util.logging.Logger.getLogger(getClass().getName()).log(
					Level.WARNING, 
					"Failed to store " + this.getClass().getName() + 
					" cause of: ", e);
		}

		return null;
	}
}