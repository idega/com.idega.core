package com.idega.core.contact.data;

import java.util.Collection;
import java.util.Collections;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ejb.FinderException;


public class PhoneTypeHomeImpl extends com.idega.data.IDOFactory implements PhoneTypeHome
{
 protected Class getEntityInterfaceClass(){
  return PhoneType.class;
 }

 public PhoneType create() throws javax.ejb.CreateException{
  return (PhoneType) super.idoCreate();
 }

 public PhoneType createLegacy(){
	try{
		return create();
	}
	catch(javax.ejb.CreateException ce){
		throw new RuntimeException("CreateException:"+ce.getMessage());
	}

 }

 public PhoneType findByPrimaryKey(int id) throws javax.ejb.FinderException{
  return (PhoneType) super.idoFindByPrimaryKey(id);
 }

 public PhoneType findByPrimaryKey(Object pk) throws javax.ejb.FinderException{
  return (PhoneType) super.idoFindByPrimaryKey(pk);
 }

 public PhoneType findByPrimaryKeyLegacy(int id) throws java.sql.SQLException{
	try{
		return findByPrimaryKey(id);
	}
	catch(javax.ejb.FinderException fe){
		throw new java.sql.SQLException(fe.getMessage());
	}

 }

@SuppressWarnings("unchecked")
public Collection<PhoneType> getPhoneTypes(int maxAmount) {
	com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
	try {
		Collection<Integer> ids = ((PhoneTypeBMPBean)entity).ejbFindPhoneTypes(maxAmount);
		this.idoCheckInPooledEntity(entity);
		return findByPrimaryKeyCollection(ids);
	} catch (FinderException e) {
		Logger.getLogger(this.getClass().getName()).log(Level.WARNING, "failed finding any phone types");
		return Collections.emptyList();
	}
}


}