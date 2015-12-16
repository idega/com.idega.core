package com.idega.user.dao.impl;

import java.util.List;
import java.util.logging.Level;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.idega.core.persistence.Param;
import com.idega.core.persistence.impl.GenericDaoImpl;
import com.idega.user.dao.GroupTypeDao;
import com.idega.user.data.bean.GroupType;
import com.idega.util.StringUtil;

@Scope(BeanDefinition.SCOPE_SINGLETON)
@Repository(GroupTypeDao.BEAN_NAME)
@Transactional(readOnly = true)
public class GroupTypeDaoImpl extends GenericDaoImpl implements GroupTypeDao {


	@Override
	public GroupType getGroupTypeByTypeName(String typeName) {
		if (StringUtil.isEmpty(typeName)) {
			return null;
		}

		Param param = new Param("groupType", typeName);

		try {
			List<GroupType> groupTypes = getResultList("groupType.findByType", GroupType.class, param);
			if (groupTypes != null && !groupTypes.isEmpty()) {
				for (GroupType groupType : groupTypes) {
					if (groupType != null && !StringUtil.isEmpty(groupType.getGroupType()) && groupType.getGroupType().equalsIgnoreCase(typeName)) {
						return groupType;
					}
				}
			}
		} catch (Exception e) {
			getLogger().log(Level.WARNING, "Error getting group type by type name: " + typeName, e);
		}
		return null;
	}

}
