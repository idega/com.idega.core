package com.idega.core.accesscontrol.business;

/**
 * @author eiki
 * 
 * A helper class to enable working woth roles since hasPermission always needs something to check against (the obj Object in all the methods).
 */
public class RoleHelperObject {

	private static RoleHelperObject roleObject = null;
	private static final String ROLE_STRING = "role_permission";

	public RoleHelperObject() {
	}
	public static RoleHelperObject getStaticInstance() {

		if (roleObject == null) {
			roleObject = new RoleHelperObject();
		}

		return roleObject;
	}

	@Override
	public String toString() {
		return ROLE_STRING;
	}
}