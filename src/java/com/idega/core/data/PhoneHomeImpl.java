package com.idega.core.data;


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


}