package com.idega.core.data;


public class ICFileHomeImpl extends com.idega.data.IDOFactory implements ICFileHome
{
 protected Class getEntityInterfaceClass(){
  return ICFile.class;
 }


 public ICFile create() throws javax.ejb.CreateException{
  return (ICFile) super.createIDO();
 }


public ICFile findByFileName(java.lang.String p0)throws javax.ejb.FinderException{
	com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
	Object pk = ((ICFileBMPBean)entity).ejbFindByFileName(p0);
	this.idoCheckInPooledEntity(entity);
	return this.findByPrimaryKey(pk);
}

 public ICFile findByPrimaryKey(Object pk) throws javax.ejb.FinderException{
  return (ICFile) super.findByPrimaryKeyIDO(pk);
 }
 
  public ICFile createLegacy(){
	try{
		return create();
	}
	catch(javax.ejb.CreateException ce){
		throw new RuntimeException("CreateException:"+ce.getMessage());
	}

 }
 
 public ICFile findByPrimaryKeyLegacy(int id) throws java.sql.SQLException{
	try{
		return findByPrimaryKey(id);
	}
	catch(javax.ejb.FinderException fe){
		throw new java.sql.SQLException(fe.getMessage());
	}

 }
 
public ICFile findByPrimaryKey(int id) throws javax.ejb.FinderException{
  return (ICFile) super.idoFindByPrimaryKey(id);
 }


}