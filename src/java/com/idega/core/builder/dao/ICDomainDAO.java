package com.idega.core.builder.dao;

import com.idega.business.SpringBeanName;
import com.idega.core.builder.data.bean.ICDomain;
import com.idega.core.persistence.GenericDao;

@SpringBeanName("icDomainDAO")
public interface ICDomainDAO extends GenericDao {

	public ICDomain findDomain(Integer domainID);

}