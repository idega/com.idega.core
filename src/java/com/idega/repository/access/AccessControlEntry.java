package com.idega.repository.access;

import javax.jcr.security.Privilege;

public interface AccessControlEntry extends javax.jcr.security.AccessControlEntry {

	public static final int PRINCIPAL_TYPE_OTHER = -1;
	public static final int PRINCIPAL_TYPE_STANDARD = 0;
	public static final int PRINCIPAL_TYPE_ROLE = 2;
	public static final int PRINCIPAL_TYPE_USER = 3;
	public static final int PRINCIPAL_TYPE_GROUP = 4;

	public int getPrincipalType();

	public boolean isNegative();

	public void setInherited(boolean inherited);
	public boolean isInherited();

	public void setInheritedFrom(String inheritedFrom);
	public String getInheritedFrom();

	public void addPrivilege(Privilege privilege);

	public void clearPrivileges();
}