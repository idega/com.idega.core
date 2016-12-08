package com.idega.core.dao.impl;

import java.io.ByteArrayInputStream;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;

import javax.ejb.CreateException;
import javax.ejb.FinderException;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.idega.core.dao.ICFileDAO;
import com.idega.core.file.data.ICFileHome;
import com.idega.core.file.data.bean.ICFile;
import com.idega.core.file.data.bean.ICMimeType;
import com.idega.core.persistence.Param;
import com.idega.core.persistence.impl.GenericDaoImpl;
import com.idega.data.IDOLookup;
import com.idega.data.IDOLookupException;
import com.idega.util.StringUtil;

@Scope(BeanDefinition.SCOPE_SINGLETON)
@Repository(ICFileDAO.BEAN_NAME)
@Transactional(readOnly = true)
public class ICFileDAOImpl extends GenericDaoImpl implements ICFileDAO {

	private ICFileHome getICFileHome() {
		try {
			return (ICFileHome) IDOLookup.getHome(com.idega.core.file.data.ICFile.class);
		} catch (IDOLookupException e) {
			getLogger().log(Level.WARNING, "Failed to get " + ICFileHome.class + " cause of: ", e);
		}

		return null;
	}

	@Override
	public ICFile findById(Integer id) {
		if (id != null) {
			return getSingleResult(ICFile.QUERY_FIND_BY_ID,
					ICFile.class, new Param("id", id));
		}
		return null;
	}

	@Override
	public List<ICFile> findAll() {
		return getResultList(ICFile.QUERY_FIND_ALL, ICFile.class);
	}

	@Override
	@Transactional(readOnly = false)
	public boolean removeFile(Integer id) {
		try {
			if(id == null) {
				return false;
			}
			ICFile icFile = findById(id);
			if(icFile == null) {
				return false;
			}
			getEntityManager().remove(icFile);
		} catch(Exception e) {
			return false;
		}
		return true;
	}

	/*
	 * (non-Javadoc)
	 * @see com.idega.core.dao.ICFileDAO#update(com.idega.core.file.data.bean.ICFile)
	 */
	@Override
	public ICFile update(ICFile file) {
		if (file != null) {
			if (findById(file.getId()) != null) {
				persist(file);
				return findById(file.getId());
			} else {
				return merge(file);
			}
		}

		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see com.idega.core.dao.ICFileDAO#update(java.lang.Integer, java.lang.String, java.lang.String, java.sql.Blob, java.util.Date, java.util.Date, java.lang.Integer)
	 */
	@Override
	public ICFile update(
			Integer id, 
			String name, 
			String description, 
			byte[] fileValue, 
			Date creationDate,
			Date modificationDate, 
			Integer fileSize, 
			ICMimeType type) {

		/*
		 * Setting up entity bean first
		 */
		com.idega.core.file.data.ICFile entityBean = null;
		try {
			entityBean = getICFileHome().findByPrimaryKey(id);
		} catch (FinderException e1) {}

		if (entityBean == null) {
			try {
				entityBean = getICFileHome().create();
			} catch (CreateException e) {
				getLogger().log(Level.WARNING, "Failed to create entity: ", e);
			}
		}

		if (!StringUtil.isEmpty(name)) {
			entityBean.setName(name);
		}

		if (!StringUtil.isEmpty(description)) {
			entityBean.setDescription(description);
		}

		if (creationDate != null) {
			entityBean.setCreationDate(new Timestamp(creationDate.getTime()));
		}

		if (modificationDate != null) {
			entityBean.setModificationDate(new Timestamp(modificationDate.getTime()));
		}

		if (fileSize != null) {
			entityBean.setFileSize(fileSize);
		}

		if (fileValue != null) {
			entityBean.setFileValue(new ByteArrayInputStream(fileValue));
		}

		entityBean.store();

		entityBean.setMetaData("uuid", UUID.randomUUID().toString());
		entityBean.store();
		
		ICFile file = findById(Integer.valueOf(entityBean.getPrimaryKey().toString()));
		if (type != null) {
			file.setMimetype(type);
		}

		return update(file);
	}
}
