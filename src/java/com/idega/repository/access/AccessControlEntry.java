package com.idega.repository.access;

import java.security.Principal;

import javax.jcr.security.Privilege;

public class AccessControlEntry implements javax.jcr.security.AccessControlEntry {

	public static final int PRINCIPAL_TYPE_OTHER = -1;
	public static final int PRINCIPAL_TYPE_STANDARD = 0;
	public static final int PRINCIPAL_TYPE_ROLE = 2;
	public static final int PRINCIPAL_TYPE_USER = 3;
	public static final int PRINCIPAL_TYPE_GROUP = 4;

	@Override
	public Principal getPrincipal() {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public Privilege[] getPrivileges() {
		// TODO Auto-generated method stub
		return null;
	}

}