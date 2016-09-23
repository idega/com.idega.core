package com.idega.core.dao.impl;

import java.util.List;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.idega.core.dao.ICFileDAO;
import com.idega.core.file.data.bean.ICFile;
import com.idega.core.persistence.Param;
import com.idega.core.persistence.impl.GenericDaoImpl;

@Scope(BeanDefinition.SCOPE_SINGLETON)
@Repository(ICFileDAO.BEAN_NAME)
@Transactional(readOnly = true)
public class ICFileDAOImpl extends GenericDaoImpl implements ICFileDAO {

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

}
