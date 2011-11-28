package com.idega.repository;

import javax.jcr.RepositoryException;

import com.idega.business.SpringBeanName;
import com.idega.repository.access.AccessControlList;

@SpringBeanName(RepositorySession.BEAN_NAME)
public interface RepositorySession {

	public static final String BEAN_NAME = "repositorySession";

	public AccessControlList getAccessControlList(String path) throws RepositoryException;

	public boolean storeAccessControlList(AccessControlList acl) throws RepositoryException;

	public String getUserHomeFolder() throws RepositoryException;

}