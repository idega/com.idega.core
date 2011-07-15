package com.idega.repository.access;

public interface AccessControlList extends javax.jcr.security.AccessControlList {

	public String getResourcePath();

	public void add(AccessControlEntry ace);
}