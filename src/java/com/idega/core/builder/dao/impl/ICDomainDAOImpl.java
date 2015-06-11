package com.idega.core.builder.dao.impl;

import org.springframework.transaction.annotation.Transactional;

import com.idega.core.builder.dao.ICDomainDAO;
import com.idega.core.builder.data.bean.ICDomain;
import com.idega.core.persistence.impl.GenericDaoImpl;

public class ICDomainDAOImpl extends GenericDaoImpl implements ICDomainDAO {

	@Override
	@Transactional(readOnly = true)
	public ICDomain findDomain(Integer domainID) {
		if (domainID == null) {
			getLogger().warning("ID is not provided");
			return null;
		}

		return find(ICDomain.class, domainID);
	}

}