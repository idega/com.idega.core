package com.idega.core.file.data;


public class ICFileHomeImpl extends com.idega.data.IDOFactory implements ICFileHome
{
 protected Class getEntityInterfaceClass(){
  return ICFile.class;
 }


 public ICFile create() throws javax.ejb.CreateException{
  return (ICFile) super.createIDO();
 }


public java.util.Collection findAllDescendingOrdered()throws javax.ejb.FinderException{
	com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
	java.util.Collection ids = ((ICFileBMPBean)entity).ejbFindAllDescendingOrdered();
	this.idoCheckInPooledEntity(entity);
	return this.getEntityCollectionForPrimaryKeys(ids);
}

public ICFile findByFileName(java.lang.String p0)throws javax.ejb.FinderException{
	com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
	Object pk = ((ICFileBMPBean)entity).ejbFindByFileName(p0);
	this.idoCheckInPooledEntity(entity);
	return this.findByPrimaryKey(pk);
}

public ICFile findEntityOfSpecificVersion(com.idega.core.version.data.ICVersion p0)throws javax.ejb.FinderException{
	com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
	Object pk = ((ICFileBMPBean)entity).ejbFindEntityOfSpecificVersion(p0);
	this.idoCheckInPooledEntity(entity);
	return this.findByPrimaryKey(pk);
}

public ICFile findRootFolder()throws javax.ejb.FinderException{
	com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
	Object pk = ((ICFileBMPBean)entity).ejbFindRootFolder();
	this.idoCheckInPooledEntity(entity);
	return this.findByPrimaryKey(pk);
}

 public ICFile findByPrimaryKey(Object pk) throws javax.ejb.FinderException{
  return (ICFile) super.findByPrimaryKeyIDO(pk);
 }

 public java.util.Collection findChildren(ICFile parent, java.util.Collection visibleMimeTypes, java.util.Collection hiddenMimeTypes, String orderBy) throws javax.ejb.FinderException{
	 com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
	 java.util.Collection ids = ((ICFileBMPBean)entity).ejbFindChildren(parent, visibleMimeTypes, hiddenMimeTypes,orderBy);
	 this.idoCheckInPooledEntity(entity);
	 return this.getEntityCollectionForPrimaryKeys(ids);
	 
 }

 public java.util.Collection findChildren(ICFile parent, java.util.Collection visibleMimeTypes, java.util.Collection hiddenMimeTypes, String orderBy, int starting, int numberOfReturns) throws javax.ejb.FinderException{
	 com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
	 java.util.Collection ids = ((ICFileBMPBean)entity).ejbFindChildren(parent, visibleMimeTypes, hiddenMimeTypes,orderBy, starting, numberOfReturns);
	 this.idoCheckInPooledEntity(entity);
	 return this.getEntityCollectionForPrimaryKeys(ids);
	 
}
}