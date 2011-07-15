package com.idega.repository;

import javax.jcr.RepositoryException;

import com.idega.repository.access.AccessControlList;

public interface RepositorySession {

	public AccessControlList getAccessControlList(String path) throws RepositoryException;

	public boolean storeAccessControlList(AccessControlList acl) throws RepositoryException;

	public String getUserHomeFolder() throws RepositoryException;

}