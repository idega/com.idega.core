package com.idega.core.contact.data;


public class EmailTypeHomeImpl extends com.idega.data.IDOFactory implements EmailTypeHome
{
 protected Class getEntityInterfaceClass(){
  return EmailType.class;
 }

 public EmailType create() throws javax.ejb.CreateException{
  return (EmailType) super.idoCreate();
 }

 public EmailType createLegacy(){
	try{
		return create();
	}
	catch(javax.ejb.CreateException ce){
		throw new RuntimeException("CreateException:"+ce.getMessage());
	}

 }

 public EmailType findByPrimaryKey(int id) throws javax.ejb.FinderException{
  return (EmailType) super.idoFindByPrimaryKey(id);
 }

 public EmailType findByPrimaryKey(Object pk) throws javax.ejb.FinderException{
  return (EmailType) super.idoFindByPrimaryKey(pk);
 }

 public EmailType findByPrimaryKeyLegacy(int id) throws java.sql.SQLException{
	try{
		return findByPrimaryKey(id);
	}
	catch(javax.ejb.FinderException fe){
		throw new java.sql.SQLException(fe.getMessage());
	}

 }


}