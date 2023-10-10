package com.idega.core.dao.impl;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.idega.core.dao.ICFileDAO;
import com.idega.core.file.data.ICFileHome;
import com.idega.core.file.data.bean.ICFile;
import com.idega.core.file.data.bean.ICMimeType;
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
			return find(ICFile.class, id);
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
			if (findById(file.getFileId()) != null) {
				persist(file);
				return findById(file.getFileId());
			} else {
				return merge(file);
			}
		}

		return null;
	}

	@Override
	public ICFile update(
			Integer id,
			String name,
			String description,
			byte[] fileValue,
			Date creationDate,
			Date modificationDate,
			Integer fileSize,
			ICMimeType type
	) {
		return update(id, name, description, fileValue, creationDate, modificationDate, fileSize, type, Boolean.FALSE);
	}

	@Override
	public ICFile update(
			Integer id,
			String name,
			String description,
			byte[] fileValue,
			Date creationDate,
			Date modificationDate,
			Integer fileSize,
			ICMimeType type,
			Boolean publiclyAvailable
	) {
		ICFile entity = findById(id);
		if (entity == null) {
			entity = new ICFile();
		}

		if (!StringUtil.isEmpty(name)) {
			entity.setName(name);
		}

		if (!StringUtil.isEmpty(description)) {
			entity.setDescription(description);
		}

		if (creationDate != null) {
			entity.setCreationDate(new Timestamp(creationDate.getTime()));
		}

		if (modificationDate != null) {
			entity.setModificationDate(new Timestamp(modificationDate.getTime()));
		}

		if (fileSize != null) {
			entity.setFileSize(fileSize);
		}

		if (fileValue != null) {
			entity.setValue(fileValue);
		}

		entity.setPubliclyAvailable(publiclyAvailable);

		entity = update(entity);
		if (entity != null) {
			if (type != null) {
				entity.setMimetype(type);
			}
		}

		return update(entity);
	}

}