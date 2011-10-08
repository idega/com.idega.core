/**
 *
 */
package com.idega.core.accesscontrol.dao.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.persistence.Query;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.idega.core.accesscontrol.dao.PermissionDAO;
import com.idega.core.accesscontrol.data.bean.ICPermission;
import com.idega.core.accesscontrol.data.bean.ICRole;
import com.idega.core.accesscontrol.data.bean.PermissionGroup;
import com.idega.core.persistence.Param;
import com.idega.core.persistence.impl.GenericDaoImpl;
import com.idega.user.data.bean.Group;

@Scope("singleton")
@Repository("permissionDAO")
@Transactional(readOnly = true)
public class PermissionDAOImpl extends GenericDaoImpl implements PermissionDAO {

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
		return find(PermissionGroup.class, primaryKey);
	}

	@Override
	public PermissionGroup getPermissionGroup(String name) {
		Param param = new Param("name", name);

		return getSingleResult("permissionGroup.findByName", PermissionGroup.class, param);
	}

	@Override
	@Transactional(readOnly = false)
	public ICPermission createPermission(String contextType, String contextValue, Group group, String permissionString, boolean permissionValue) {
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
		Query query = getEntityManager().createNamedQuery("permission.deleteByCriteria");
		query.setParameter("contextType", contextType);
		query.setParameter("contextValue", contextValue);
		query.setParameter("permissionString", permissionString);
		query.setParameter("groups", groups);

		query.executeUpdate();
	}

	@Override
	public List<ICPermission> findPermissions(String contextType) {
		Param param1 = new Param("contextType", contextType);

		return getResultList("permission.findByContextType", ICPermission.class, param1);
	}

	@Override
	public List<ICPermission> findPermissions(String contextType, String contextValue) {
		Param param1 = new Param("contextType", contextType);
		Param param2 = new Param("contextValue", contextValue);

		return getResultList("permission.findByContext", ICPermission.class, param1, param2);
	}

	@Override
	public List<ICPermission> findPermissions(String contextType, String contextValue, String permissionString) {
		Param param1 = new Param("contextType", contextType);
		Param param2 = new Param("contextValue", contextValue);
		Param param3 = new Param("permissionString", permissionString);

		return getResultList("permission.findAllPermissionsByContextTypeAndContextValueAndPermissionString", ICPermission.class, param1, param2, param3);
	}

	@Override
	public ICPermission findPermission(String contextType, String contextValue, String permissionString, Group group) {
		Param param1 = new Param("contextType", contextType);
		Param param2 = new Param("contextValue", contextValue);
		Param param3 = new Param("permissionString", permissionString);
		Param param4 = new Param("group", group);

		return getSingleResult("permission.findByCriteria", ICPermission.class, param1, param2, param3, param4);
	}

	@Override
	public List<ICPermission> findPermissions(String contextType, String contextValue, String permissionString, String permissionValue) {
		Param param1 = new Param("contextType", contextType);
		Param param2 = new Param("contextValue", contextValue);
		Param param3 = new Param("permissionString", permissionString);
		Param param4 = new Param("permissionValue", permissionValue);

		return getResultList("permission.findByValues", ICPermission.class, param1, param2, param3, param4);
	}

	@Override
	public List<ICPermission> findPermissions(String contextType, Collection<String> contextValues, Group group) {
		Param param1 = new Param("contextType", contextType);
		Param param2 = new Param("contextValues", contextValues);
		Param param3 = new Param("group", group);

		return getResultList("permission.findByGroupAndContext", ICPermission.class, param1, param2, param3);
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

		return getResultList("permission.findAllPermissionsByPermissionGroupAndPermissionStringAndContextTypeOrderedByContextValue", ICPermission.class, param1, param2, param3);
	}

	@Override
	public List<ICPermission> findAllPermissionsByContextTypeAndContextValueAndPermissionStringCollectionAndPermissionGroup(String contextType, String contextValue, Collection<String> permissionStrings, Group group) {
		Param param1 = new Param("contextType", contextType);
		Param param2 = new Param("contextValue", contextValue);
		Param param3 = new Param("permissionStrings", permissionStrings);
		Param param4 = new Param("group", group);

		return getResultList("permission.findAllPermissionsByContextTypeAndContextValueAndPermissionStringCollectionAndPermissionGroup", ICPermission.class, param1, param2, param3, param4);
	}

	@Override
	public List<ICPermission> findAllPermissionsByContextTypeAndPermissionGroupOrderedByContextValue(String contextType, Group group) {
		Param param1 = new Param("contextType", contextType);
		Param param2 = new Param("group", group);

		return getResultList("permission.findAllPermissionsByContextTypeAndPermissionGroupOrderedByContextValue", ICPermission.class, param1, param2);
	}

	@Override
	public List<ICPermission> findAllPermissionsByContextTypeAndPermissionGroupOrderedByContextValue(String contextType, Collection<Group> groups) {
		Param param1 = new Param("contextType", contextType);
		Param param2 = new Param("groups", groups);

		return getResultList("permission.findAllPermissionsByContextTypeAndPermissionGroupsOrderedByContextValue", ICPermission.class, param1, param2);
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

		return getResultList("permission.findAllPermissionsByPermissionGroupsCollectionAndPermissionStringAndContextTypeOrderedByContextValue", ICPermission.class, param1, param2, param3);
	}

	@Override
	public ICPermission findPermissionByPermissionGroupAndPermissionStringAndContextTypeAndContextValue(Group group, String permissionString, String contextType, String contextValue) {
		Param param1 = new Param("group", group);
		Param param2 = new Param("permissionString", permissionString);
		Param param3 = new Param("contextType", contextType);
		Param param4 = new Param("contextValue", contextValue);

		return getSingleResult("permission.findPermissionByPermissionGroupAndPermissionStringAndContextTypeAndContextValue", ICPermission.class, param1, param2, param3, param4);
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
}