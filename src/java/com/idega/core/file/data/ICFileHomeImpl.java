package com.idega.core.file.data;

import java.util.Arrays;
import java.util.Collection;
import java.util.logging.Level;

import javax.ejb.FinderException;

import com.idega.data.IDOEntity;
import com.idega.data.SimpleQuerier;
import com.idega.util.ArrayUtil;
import com.idega.util.ListUtil;
import com.idega.util.StringUtil;


public class ICFileHomeImpl extends com.idega.data.IDOFactory implements ICFileHome
{
 @Override
protected Class getEntityInterfaceClass(){
  return ICFile.class;
 }


 @Override
public ICFile create() throws javax.ejb.CreateException{
  return (ICFile) super.createIDO();
 }


@Override
public java.util.Collection findAllDescendingOrdered()throws javax.ejb.FinderException{
	com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
	java.util.Collection ids = ((ICFileBMPBean)entity).ejbFindAllDescendingOrdered();
	this.idoCheckInPooledEntity(entity);
	return this.getEntityCollectionForPrimaryKeys(ids);
}

@Override
public ICFile findByFileName(java.lang.String p0)throws javax.ejb.FinderException{
	com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
	Object pk = ((ICFileBMPBean)entity).ejbFindByFileName(p0);
	this.idoCheckInPooledEntity(entity);
	return this.findByPrimaryKey(pk);
}

@Override
public ICFile findEntityOfSpecificVersion(com.idega.core.version.data.ICVersion p0)throws javax.ejb.FinderException{
	com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
	Object pk = ((ICFileBMPBean)entity).ejbFindEntityOfSpecificVersion(p0);
	this.idoCheckInPooledEntity(entity);
	return this.findByPrimaryKey(pk);
}

@Override
public ICFile findRootFolder()throws javax.ejb.FinderException{
	com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
	Object pk = ((ICFileBMPBean)entity).ejbFindRootFolder();
	this.idoCheckInPooledEntity(entity);
	return this.findByPrimaryKey(pk);
}

 @Override
public ICFile findByPrimaryKey(Object pk) throws javax.ejb.FinderException{
  return (ICFile) super.findByPrimaryKeyIDO(pk);
 }

 @Override
public java.util.Collection findChildren(ICFile parent, java.util.Collection visibleMimeTypes, java.util.Collection hiddenMimeTypes, String orderBy) throws javax.ejb.FinderException{
	 com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
	 java.util.Collection ids = ((ICFileBMPBean)entity).ejbFindChildren(parent, visibleMimeTypes, hiddenMimeTypes,orderBy);
	 this.idoCheckInPooledEntity(entity);
	 return this.getEntityCollectionForPrimaryKeys(ids);

 }

 @Override
public java.util.Collection findChildren(ICFile parent, java.util.Collection visibleMimeTypes, java.util.Collection hiddenMimeTypes, String orderBy, int starting, int numberOfReturns) throws javax.ejb.FinderException{
	 com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
	 java.util.Collection ids = ((ICFileBMPBean)entity).ejbFindChildren(parent, visibleMimeTypes, hiddenMimeTypes,orderBy, starting, numberOfReturns);
	 this.idoCheckInPooledEntity(entity);
	 return this.getEntityCollectionForPrimaryKeys(ids);

}

	@Override
	public ICFile findByHash(Integer hash) throws FinderException {
		com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
		Object pk = ((ICFileBMPBean)entity).ejbFindByHash(hash);
		this.idoCheckInPooledEntity(entity);
		return this.findByPrimaryKey(pk);
	}

	private ICFile getByUniqueId(StringBuilder query) throws Exception {
		String[] primaryKeys = null;
		try {
			primaryKeys = SimpleQuerier.executeStringQuery(query.toString());
		} catch (Exception e) {
			getLog().log(Level.WARNING, "Failed to execute query: " + query.toString() + " cause of: ", e);
		}
		if (ArrayUtil.isEmpty(primaryKeys)) {
			return null;
		}

		Collection<IDOEntity> entities = null;
		try {
			entities = getEntityCollectionForPrimaryKeys(Arrays.asList(primaryKeys));
		} catch (FinderException e) {
			getLog().log(Level.WARNING, "Failed to get entities by primary keys: " + primaryKeys);
		}

		if (ListUtil.isEmpty(entities)) {
			return null;
		}

		return (ICFile) entities.iterator().next();
	}

	@Override
	public ICFile findByUUID(String uuid) {
		if (!StringUtil.isEmpty(uuid)) {
			StringBuilder query = new StringBuilder();
			query.append("SELECT icf.IC_FILE_ID FROM ic_file icf where icf.unique_id = '").append(uuid).append("'");
			ICFile file = null;
			try {
				file = getByUniqueId(query);
			} catch (Exception e) {}
			if (file != null) {
				return file;
			}

			query = new StringBuilder();
			query.append("SELECT icf.IC_FILE_ID FROM ic_file icf ");
			query.append("JOIN ic_file_ic_metadata icficm ");
			query.append("ON icf.IC_FILE_ID = icficm.IC_FILE_ID ");
			query.append("JOIN ic_metadata icm ");
			query.append("ON icm.IC_METADATA_ID = icficm.IC_METADATA_ID ");
			query.append("AND icm.METADATA_VALUE = '").append(uuid).append("'");
			try {
				return getByUniqueId(query);
			} catch (Exception e) {
				getLog().log(Level.WARNING, "Error getting file by unique ID " + uuid, e);
			}
		}

		return null;
	}

	@Override
	public Collection<Integer> getFilesWithoutUniqueIds() {
		try {
			IDOEntity entity = this.idoCheckOutPooledEntity();
			Collection<Integer> ids = ((ICFileBMPBean) entity).ejbFindFilesWithoutUniqueIds();
			this.idoCheckInPooledEntity(entity);
			return ids;
		} catch (FinderException e) {
		} catch (Exception e) {
			getLog().log(Level.WARNING, "Error getting files without unique IDs", e);
		}
		return null;
	}

	@Override
	public Collection<Integer> getFilesWithoutTokens() {
		try {
			IDOEntity entity = this.idoCheckOutPooledEntity();
			Collection<Integer> ids = ((ICFileBMPBean) entity).ejbFindFilesWithoutTokens();
			this.idoCheckInPooledEntity(entity);
			return ids;
		} catch (FinderException e) {
		} catch (Exception e) {
			getLog().log(Level.WARNING, "Error getting files without tokens", e);
		}
		return null;
	}

	@Override
	public ICFile findByToken(String token) {
		if (StringUtil.isEmpty(token)) {
			return null;
		}

		try {
			com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
			Object pk = ((ICFileBMPBean) entity).ejbFindByToken(token);
			this.idoCheckInPooledEntity(entity);
			return this.findByPrimaryKey(pk);
		} catch (FinderException e) {
		} catch (Exception e) {
			getLog().log(Level.WARNING, "Error getting file by token " + token, e);
		}

		return null;
	}


	@Override
	public Collection<Integer> getIdsOfAllFiles() {
		try {
			IDOEntity entity = this.idoCheckOutPooledEntity();
			Collection<Integer> ids = ((ICFileBMPBean) entity).ejbFindIdsOfAllFiles();
			this.idoCheckInPooledEntity(entity);
			return ids;
		} catch (FinderException e) {
		} catch (Exception e) {
			getLog().log(Level.WARNING, "Error getting IDs of all files", e);
		}
		return null;
	}

}