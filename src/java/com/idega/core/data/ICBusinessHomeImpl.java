package com.idega.core.data;


public class ICBusinessHomeImpl extends com.idega.data.IDOFactory implements ICBusinessHome
{
 protected Class getEntityInterfaceClass(){
  return ICBusiness.class;
 }

 public ICBusiness create() throws javax.ejb.CreateException{
  return (ICBusiness) super.idoCreate();
 }

 public ICBusiness createLegacy(){
	try{
		return create();
	}
	catch(javax.ejb.CreateException ce){
		throw new RuntimeException("CreateException:"+ce.getMessage());
	}

 }

 public ICBusiness findByPrimaryKey(int id) throws javax.ejb.FinderException{
  return (ICBusiness) super.idoFindByPrimaryKey(id);
 }

 public ICBusiness findByPrimaryKey(Object pk) throws javax.ejb.FinderException{
  return (ICBusiness) super.idoFindByPrimaryKey(pk);
 }

 public ICBusiness findByPrimaryKeyLegacy(int id) throws java.sql.SQLException{
	try{
		return findByPrimaryKey(id);
	}
	catch(javax.ejb.FinderException fe){
		throw new java.sql.SQLException(fe.getMessage());
	}

 }


}