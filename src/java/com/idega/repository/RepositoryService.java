package com.idega.repository;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import javax.jcr.Credentials;
import javax.jcr.Node;
import javax.jcr.Repository;
import javax.jcr.RepositoryException;
import javax.jcr.security.AccessControlPolicy;

import com.idega.builder.bean.AdvancedProperty;
import com.idega.repository.bean.RepositoryItemVersionInfo;

public interface RepositoryService extends Repository {

	public void initializeRepository(InputStream configSource, String repositoryName) throws Exception;

	public boolean uploadFileAndCreateFoldersFromStringAsRoot(String parentPath, String fileName, String fileContentString,	String contentType) throws RepositoryException;
	public boolean uploadFileAndCreateFoldersFromStringAsRoot(String parentPath, String fileName, InputStream stream, String contentType) throws RepositoryException;
	public boolean uploadXMLFileAndCreateFoldersFromStringAsRoot(String parentPath, String fileName, String fileContentString) throws RepositoryException;
	public boolean uploadFile(String uploadPath, String fileName, String contentType, InputStream stream) throws RepositoryException;

	public Node updateFileContents(String absolutePath, InputStream fileContents, AdvancedProperty... properties) throws RepositoryException;
	public Node updateFileContents(String absolutePath, InputStream fileContents, boolean createFile, AdvancedProperty... properties) throws RepositoryException;

	public InputStream getInputStream(String path) throws IOException, RepositoryException;
	public InputStream getInputStreamAsRoot(String path) throws IOException, RepositoryException;
	public InputStream getFileContents(Node fileNode) throws IOException, RepositoryException;

	public boolean deleteAsRootUser(String path) throws RepositoryException;
	public boolean delete(String path) throws RepositoryException;

	public boolean createFolder(String path) throws RepositoryException;
	public boolean createFolderAsRoot(String path) throws RepositoryException;

	public AccessControlPolicy[] applyAccessControl(String path, AccessControlPolicy[] acp) throws RepositoryException;

	public Node getNode(String absolutePath) throws RepositoryException;

	public boolean setProperties(Node node, AdvancedProperty... properties) throws RepositoryException;

	public String getRepositoryConstantFolderType();

	public String getWebdavServerURI();

	public Credentials getCredentials(String user, String password);

	public boolean generateUserFolders(String loginName) throws RepositoryException;

	public boolean getExistence(String absolutePath) throws RepositoryException;

	public List<RepositoryItemVersionInfo> getVersions(String parentPath, String fileName) throws RepositoryException;
}