package com.idega.core.data;


public class GenericGroupHomeImpl extends com.idega.data.IDOFactory implements GenericGroupHome
{
 protected Class getEntityInterfaceClass(){
  return GenericGroup.class;
 }

 public GenericGroup create() throws javax.ejb.CreateException{
  return (GenericGroup) super.idoCreate();
 }

 public GenericGroup createLegacy(){
	try{
		return create();
	}
	catch(javax.ejb.CreateException ce){
		throw new RuntimeException("CreateException:"+ce.getMessage());
	}

 }

 public GenericGroup findByPrimaryKey(int id) throws javax.ejb.FinderException{
  return (GenericGroup) super.idoFindByPrimaryKey(id);
 }

 public GenericGroup findByPrimaryKey(Object pk) throws javax.ejb.FinderException{
  return (GenericGroup) super.idoFindByPrimaryKey(pk);
 }

 public GenericGroup findByPrimaryKeyLegacy(int id) throws java.sql.SQLException{
	try{
		return findByPrimaryKey(id);
	}
	catch(javax.ejb.FinderException fe){
		throw new java.sql.SQLException(fe.getMessage());
	}

 }


}