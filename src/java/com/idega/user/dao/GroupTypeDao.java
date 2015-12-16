package com.idega.user.dao;

import com.idega.business.SpringBeanName;
import com.idega.core.persistence.GenericDao;
import com.idega.user.data.bean.GroupType;

@SpringBeanName(GroupTypeDao.BEAN_NAME)
public interface GroupTypeDao extends GenericDao {
	public static final String BEAN_NAME = "groupTypeDAO";

	public GroupType getGroupTypeByTypeName(String typeName);

}
