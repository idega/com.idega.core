package com.idega.core.contact.data;


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


}