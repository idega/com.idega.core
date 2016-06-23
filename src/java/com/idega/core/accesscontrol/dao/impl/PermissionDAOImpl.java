/**
 *
 */
package com.idega.core.accesscontrol.dao.impl;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;

import javax.persistence.Query;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.idega.core.accesscontrol.business.AccessController;
import com.idega.core.accesscontrol.dao.PermissionDAO;
import com.idega.core.accesscontrol.data.bean.ICPermission;
import com.idega.core.accesscontrol.data.bean.ICRole;
import com.idega.core.accesscontrol.data.bean.PermissionGroup;
import com.idega.core.persistence.Param;
import com.idega.core.persistence.impl.GenericDaoImpl;
import com.idega.data.SimpleQuerier;
import com.idega.idegaweb.IWMainApplicationSettings;
import com.idega.idegaweb.IWMainApplicationStartedEvent;
import com.idega.user.data.bean.Group;
import com.idega.util.ArrayUtil;
import com.idega.util.CoreConstants;
import com.idega.util.ListUtil;
import com.idega.util.StringUtil;

@Scope(BeanDefinition.SCOPE_SINGLETON)
@Repository("permissionDAO")
@Transactional(readOnly = true)
public class PermissionDAOImpl extends GenericDaoImpl implements PermissionDAO, ApplicationListener<IWMainApplicationStartedEvent> {

	@Override
	@Transactional(readOnly = false)
	public PermissionGroup createPermissionGroup(String name, String description) {
		PermissionGroup group = new PermissionGroup();
		group.setName(name);
		group.setDescription(description);
		persist(group);

		return group;
	}

	@Override
	public PermissionGroup findPermissionGroup(Object primaryKey) {
		Group group = find(Group.class, primaryKey);
		if (group instanceof PermissionGroup) {
			return (PermissionGroup) group;
		} else {
			getLogger().warning("Group " + group + " with ID is not type of " + PermissionGroup.class.getName());
		}
		return null;
	}

	@Override
	public PermissionGroup getPermissionGroup(String name) {
		Param param = new Param("name", name);

		List<PermissionGroup> results = null;
		try {
			results = getResultList("permissionGroup.findByName", PermissionGroup.class, param);
		} catch (Exception e) {
			getLogger().log(Level.WARNING, "Error getting permission group by name " + name, e);
		}
		return ListUtil.isEmpty(results) ? null : results.get(0);
	}

	@Override
	@Transactional(readOnly = false)
	public ICPermission createPermission(String contextType, String contextValue, Group group, String permissionString, boolean permissionValue) {
		if (group == null) {
			List<ICPermission> permissions = findPermissions(contextType, contextValue, permissionString, permissionValue ? CoreConstants.Y.charAt(0) : CoreConstants.N.charAt(0));
			if (!ListUtil.isEmpty(permissions))
				return permissions.iterator().next();
		} else {
			ICPermission permission = findPermission(contextType, contextValue, permissionString, group);
			if (permission != null)
				return permission;
		}

		ICPermission permission = new ICPermission();
		permission.setContextType(contextType);
		permission.setContextValue(contextValue);
		permission.setPermissionGroup(group);
		permission.setPermissionString(permissionString);
		permission.setPermissionValue(permissionValue);
		persist(permission);

		return permission;
	}

	@Override
	@Transactional(readOnly = false)
	public void removePermissions(String contextType, String contextValue, String permissionString, Collection<Group> groups) {
		Query query = getEntityManager().createNamedQuery(ICPermission.DELETE_BY_CRITERIA);
		query.setParameter("contextType", contextType);
		query.setParameter("contextValue", contextValue);
		query.setParameter("permissionStrings", permissionString);
		query.setParameter("groups", groups);

		query.executeUpdate();
	}

	@Override
	public List<ICPermission> findPermissions(String contextType) {
		Param param1 = new Param("contextType", contextType);

		return getResultList(ICPermission.BY_CONTEXT_TYPE, ICPermission.class, param1);
	}

	@Override
	public List<ICPermission> findPermissions(String contextType, String contextValue) {
		Param param1 = new Param("contextType", contextType);
		Param param2 = new Param("contextValue", contextValue);

		return getResultList(ICPermission.BY_CONTEXT, ICPermission.class, param1, param2);
	}

	@Override
	public List<ICPermission> findPermissions(String contextType, String contextValue, String permissionString) {
		Param param1 = new Param("contextType", contextType);
		Param param2 = new Param("contextValue", contextValue);
		Param param3 = new Param("permissionString", permissionString);

		return getResultList(ICPermission.BY_CONTEXT_TYPE_AND_CONTEXT_VALUE_AND_PERMISSION, ICPermission.class, param1, param2, param3);
	}

	@Override
	public ICPermission findPermission(String contextType, String contextValue, String permissionString, Group group) {
		Param param1 = new Param("contextType", contextType);
		Param param2 = new Param("contextValue", contextValue);
		Param param3 = new Param("permissionString", permissionString);
		Param param4 = new Param("group", group);

		return getSingleResult(ICPermission.BY_CRITERIA, ICPermission.class, param1, param2, param3, param4);
	}

	@Override
	public List<ICPermission> findPermissions(String contextType, String contextValue, String permissionString, Character permissionValue) {
		Param param1 = new Param("contextType", contextType);
		Param param2 = new Param("contextValue", contextValue);
		Param param3 = new Param("permissionString", permissionString);
		Param param4 = new Param("permissionValue", permissionValue);

		return getResultList(ICPermission.BY_VALUES, ICPermission.class, param1, param2, param3, param4);
	}

	@Override
	public List<ICPermission> findPermissions(String contextType, Collection<String> contextValues, Group group) {
		Param param1 = new Param("contextType", contextType);
		Param param2 = new Param("contextValues", contextValues);
		Param param3 = new Param("group", group);

		return getResultList(ICPermission.BY_GROUP_AND_CONTEXT, ICPermission.class, param1, param2, param3);
	}

	@Override
	public List<ICPermission> findPermissions(String contextType, Collection<String> contextValues) {
		Param param1 = new Param("contextType", contextType);
		Param param2 = new Param("contextValues", contextValues);

		return getResultList(ICPermission.BY_CONTEXTS, ICPermission.class, param1, param2);
	}

	@Override
	public List<ICPermission> findAllPermissionsByPermissionGroupAndPermissionStringAndContextTypeOrderedByContextValue(Group group, String permissionString, String contextType) {
		Collection<String> strings = new ArrayList<String>();
		strings.add(permissionString);

		return findAllPermissionsByPermissionGroupAndPermissionStringAndContextTypeOrderedByContextValue(group, strings, contextType);
	}

	@Override
	public List<ICPermission> findAllPermissionsByPermissionGroupAndPermissionStringAndContextTypeOrderedByContextValue(Group group, Collection<String> permissionStrings, String contextType) {
		Param param1 = new Param("group", group);
		Param param2 = new Param("permissionStrings", permissionStrings);
		Param param3 = new Param("contextType", contextType);

		return getResultList(ICPermission.BY_PERMISSION_GROUP_AND_PERMISSION_STRING, ICPermission.class, param1, param2, param3);
	}

	@Override
	public List<ICPermission> findAllPermissionsByContextTypeAndContextValueAndPermissionStringCollectionAndPermissionGroup(String contextType, String contextValue, Collection<String> permissionStrings, Group group) {
		List<Param> params = new ArrayList<Param>();
		if (StringUtil.isEmpty(contextType)) {
			try {
				throw new RuntimeException("Context type is not provided! Context value: " + contextValue + ", permission strings: " + permissionStrings + ", group: " + group);
			} catch (Exception e) {
				getLogger().log(Level.WARNING, e.getLocalizedMessage(), e);
			}
			return Collections.emptyList();
		} else {
			params.add(new Param("contextType", contextType));
		}

		if (StringUtil.isEmpty(contextValue)) {
			try {
				throw new RuntimeException("Context value is not provided! Context type: " + contextType + ", permission strings: " + permissionStrings + ", group: " + group);
			} catch (Exception e) {
				getLogger().log(Level.WARNING, e.getLocalizedMessage(), e);
			}
			getLogger().warning("Context value not provided, using method 'findAllPermissionsByPermissionGroupAndPermissionStringAndContextTypeOrderedByContextValue'");
			return findAllPermissionsByPermissionGroupAndPermissionStringAndContextTypeOrderedByContextValue(group, permissionStrings, contextType);
		} else {
			params.add(new Param("contextValue", contextValue));
		}

		if (permissionStrings == null) {
			try {
				throw new RuntimeException("Permission strings are not provided! Context type: " + contextType + ", context value: " + contextValue + ", group: " + group);
			} catch (Exception e) {
				getLogger().log(Level.WARNING, e.getLocalizedMessage(), e);
			}
			return Collections.emptyList();
		} else {
			params.add(new Param("permissionStrings", permissionStrings));
		}

		if (group == null) {
			try {
				throw new RuntimeException("Group is not provided! Context type: " + contextType + ", context value: " + contextValue + ", permission strings: " + permissionStrings);
			} catch (Exception e) {
				getLogger().log(Level.WARNING, e.getLocalizedMessage(), e);
			}
			return Collections.emptyList();
		} else {
			params.add(new Param("group", group));
		}

		return getResultList(ICPermission.BY_CONTEXT_TYPE_AND_CONTEXT_VALUE_AND_PERMISSION_AND_GROUP, ICPermission.class, ArrayUtil.convertListToArray(params));
	}

	@Override
	public List<ICPermission> findAllPermissionsByContextTypeAndPermissionGroupOrderedByContextValue(String contextType, Group group) {
		Param param1 = new Param("contextType", contextType);
		Param param2 = new Param("group", group.getID());

		return getResultList(ICPermission.BY_CONTEXT_TYPE_AND_PERMISSION_GROUP_ID, ICPermission.class, param1, param2);
	}

	/*
	 * (non-Javadoc)
	 * @see com.idega.core.accesscontrol.dao.PermissionDAO#findAll(java.lang.String, java.lang.Integer)
	 */
	@Override
	public List<ICPermission> findAll(String contextType, Collection<Integer> primaryKeys) {
		if (!ListUtil.isEmpty(primaryKeys) && !StringUtil.isEmpty(contextType)) {
			return getResultList(
					ICPermission.BY_CONTEXT_TYPE_AND_PERMISSION_GROUP_ID,
					ICPermission.class,
					new Param("contextType", contextType),
					new Param("group", primaryKeys));
		}

		return Collections.emptyList();
	}


	@Override
	public List<ICPermission> findAllPermissionsByContextTypeAndPermissionGroupOrderedByContextValue(String contextType, Collection<Group> groups) {
		Param param1 = new Param("contextType", contextType);
		Param param2 = new Param("groups", groups);

		return getResultList(ICPermission.BY_CONTEXT_TYPE_AND_PERMISSION_GROUPS, ICPermission.class, param1, param2);
	}

	@Override
	public List<ICPermission> findAllPermissionsByPermissionGroupsCollectionAndPermissionStringAndContextTypeOrderedByContextValue(Collection<Group> groups, String permissionString, String contextType) {
		Collection<String> strings = new ArrayList<String>();
		strings.add(permissionString);

		return findAllPermissionsByPermissionGroupsCollectionAndPermissionStringAndContextTypeOrderedByContextValue(groups, strings, contextType);
	}

	@Override
	public List<ICPermission> findAllPermissionsByPermissionGroupsCollectionAndPermissionStringAndContextTypeOrderedByContextValue(Collection<Group> groups, Collection<String> permissionStrings, String contextType) {
		Param param1 = new Param("groups", groups);
		Param param2 = new Param("permissionStrings", permissionStrings);
		Param param3 = new Param("contextType", contextType);

		return getResultList(ICPermission.BY_PERMISSION_GROUPS, ICPermission.class, param1, param2, param3);
	}

	@Override
	public ICPermission findPermissionByPermissionGroupAndPermissionStringAndContextTypeAndContextValue(Group group, String permissionString, String contextType, String contextValue) {
		Param param1 = new Param("group", group);
		Param param2 = new Param("permissionString", permissionString);
		Param param3 = new Param("contextType", contextType);
		Param param4 = new Param("contextValue", contextValue);

		return getSingleResult(ICPermission.BY_PERMISSION_GROUP, ICPermission.class, param1, param2, param3, param4);
	}

	@Override
	@Transactional(readOnly = false)
	public ICRole createRole(String roleKey, String roleDescriptionLocalizableKey, String roleNameLocalizableKey) {
		ICRole role = new ICRole();
		role.setRoleKey(roleKey);
		role.setRoleDescriptionLocalizableKey(roleDescriptionLocalizableKey);
		role.setRoleNameLocalizableKey(roleNameLocalizableKey);
		persist(role);

		return role;
	}

	@Override
	public ICRole findRole(String roleKey) {
		return find(ICRole.class, roleKey);
	}

	@Override
	public List<ICRole> findAllRoles() {
		return getResultList("role.findAll", ICRole.class);
	}

	@Override
	public void onApplicationEvent(IWMainApplicationStartedEvent event) {
		IWMainApplicationSettings settings = event.getIWMA().getSettings();
		if (settings.getBoolean("enlarge_perm_cntxt_column", Boolean.TRUE)) {
			try {
				SimpleQuerier.executeUpdate("ALTER TABLE " + ICPermission.ENTITY_NAME + " modify " + ICPermission.COLUMN_CONTEXT_VALUE +
						" varchar(" + ICRole.ROLE_KEY_MAX_LENGTH + ");", true);
				settings.getBoolean("enlarge_perm_cntxt_column", Boolean.FALSE);
			} catch (SQLException e) {
				settings.getBoolean("enlarge_perm_cntxt_column", Boolean.FALSE);
			}
		}
	}

	@Override
	public List<ICPermission> findPermissionsByContextTypeAndPermission(String contextType, String permissionString) {
		return getResultList(ICPermission.BY_CONTEXT_TYPE_AND_PERMISSION, ICPermission.class, new Param("contextType", contextType), new Param("permissionString", permissionString));
	}

	@Override
	public List<ICPermission> findPermissionsByRoles(List<String> roles) {
		if (ListUtil.isEmpty(roles)) {
			return null;
		}

		return getResultList(
				ICPermission.BY_CONTEXT_TYPE_AND_PERMISSIONS,
				ICPermission.class,
				new Param("contextType", AccessController.PERMISSION_KEY_ROLE),
				new Param("permissionStrings", roles)
		);
	}

}