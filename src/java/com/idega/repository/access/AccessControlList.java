package com.idega.repository.access;

import java.util.List;

import com.idega.core.accesscontrol.data.bean.ICPermission;

public interface AccessControlList extends javax.jcr.security.AccessControlList {

	public String getResourcePath();

	public void add(AccessControlEntry ace);

	public List<ICPermission> getPermissions();
	public void addPermission(ICPermission permission);

	public void setAces(AccessControlEntry[] aces);
}