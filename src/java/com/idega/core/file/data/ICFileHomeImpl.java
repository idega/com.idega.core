package com.idega.core.file.data;

import java.util.Arrays;
import java.util.Collection;
import java.util.logging.Level;

import javax.ejb.FinderException;

import com.idega.data.IDOEntity;
import com.idega.data.SimpleQuerier;
import com.idega.util.ListUtil;
import com.idega.util.StringUtil;


public class ICFileHomeImpl extends com.idega.data.IDOFactory implements ICFileHome
{
 @Override
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


	public ICFile findByHash(Integer hash) throws FinderException {
		com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
		Object pk = ((ICFileBMPBean)entity).ejbFindByHash(hash);
		this.idoCheckInPooledEntity(entity);
		return this.findByPrimaryKey(pk);
	}

	/*
	 * (non-Javadoc)
	 * @see com.idega.core.file.data.ICFileHome#findByUUID(java.lang.String)
	 */
	@Override
	public ICFile findByUUID(String uuid) {
		if (!StringUtil.isEmpty(uuid)) {
			StringBuilder query = new StringBuilder();
			query.append("SELECT icf.IC_FILE_ID FROM ic_file icf ");
			query.append("JOIN ic_file_ic_metadata icficm ");
			query.append("ON icf.IC_FILE_ID = icficm.IC_FILE_ID ");
			query.append("JOIN ic_metadata icm ");
			query.append("ON icm.IC_METADATA_ID = icficm.IC_METADATA_ID ");
			query.append("AND icm.METADATA_VALUE = '").append(uuid).append("'");

			String[] primaryKeys = null;
			try {
				primaryKeys = SimpleQuerier.executeStringQuery(query.toString());
			} catch (Exception e) {
				getLog().log(Level.WARNING, "Failed to execute query: " + query.toString() + " cause of: ", e);
			}

			Collection<IDOEntity> entities = null;
			try {
				entities = getEntityCollectionForPrimaryKeys(Arrays.asList(primaryKeys));
			} catch (FinderException e) {
				getLog().log(Level.WARNING, "Failed to get entities by primary keys: " + primaryKeys);
			}

			if (!ListUtil.isEmpty(entities)) {
				return (ICFile) entities.iterator().next();
			}
		}

		return null;
	}
}