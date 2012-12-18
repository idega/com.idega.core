package com.idega.repository;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collection;
import java.util.List;
import java.util.zip.ZipInputStream;

import javax.jcr.Binary;
import javax.jcr.Credentials;
import javax.jcr.Node;
import javax.jcr.Repository;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.lock.Lock;
import javax.jcr.security.AccessControlPolicy;
import javax.jcr.version.VersionManager;

import org.springframework.context.ApplicationListener;

import com.idega.builder.bean.AdvancedProperty;
import com.idega.business.SpringBeanName;
import com.idega.repository.access.AccessControlList;
import com.idega.repository.bean.Property;
import com.idega.repository.bean.RepositoryItem;
import com.idega.repository.bean.RepositoryItemVersionInfo;
import com.idega.repository.event.RepositoryEventListener;
import com.idega.user.data.bean.User;

@SpringBeanName(RepositoryService.BEAN_NAME)
public interface RepositoryService extends Repository, ApplicationListener {

	public static final String BEAN_NAME = "repositoryService";

	public void initializeRepository(InputStream configSource, String repositoryName) throws Exception;

	public boolean uploadFileAndCreateFoldersFromStringAsRoot(String parentPath, String fileName, String fileContentString,	String contentType)
			throws RepositoryException;
	public boolean uploadFileAndCreateFoldersFromStringAsRoot(String parentPath, String fileName, InputStream stream, String contentType)
			throws RepositoryException;
	public boolean uploadXMLFileAndCreateFoldersFromStringAsRoot(String parentPath, String fileName, String fileContentString)
			throws RepositoryException;
	public boolean uploadFile(String uploadPath, String fileName, String contentType, InputStream stream) throws RepositoryException;

	public boolean uploadZipFileContents(ZipInputStream zipStream, String uploadPath) throws RepositoryException;

	public Node updateFileContents(String absolutePath, InputStream fileContents, AdvancedProperty... properties) throws RepositoryException;
	public Node updateFileContents(String absolutePath, InputStream fileContents, boolean createFile, AdvancedProperty... properties)
			throws RepositoryException;

	public Binary getBinary(String path) throws RepositoryException;
	public InputStream getInputStream(String path) throws IOException, RepositoryException;
	public InputStream getInputStreamAsRoot(String path) throws IOException, RepositoryException;
	public InputStream getFileContents(String path) throws IOException, RepositoryException;
	public InputStream getFileContents(User user, String path) throws IOException, RepositoryException;

	public boolean deleteAsRootUser(String path) throws RepositoryException;
	public boolean delete(String path) throws RepositoryException;
	public boolean delete(String path, User user) throws RepositoryException;

	public boolean createFolder(String path) throws RepositoryException;
	public boolean createFolderAsRoot(String path) throws RepositoryException;

	public AccessControlPolicy[] applyAccessControl(String path, AccessControlPolicy[] acp) throws RepositoryException;

	public Node getNode(String absolutePath) throws RepositoryException;
	public Node getNodeAsRootUser(String absolutePath) throws RepositoryException;
	public Node getNodeAsRootUser(String absolutePath, boolean closeSession) throws RepositoryException;

	public boolean setProperties(String path, Property... properties) throws RepositoryException;

	public String getRepositoryConstantFolderType();

	public String getWebdavServerURL();
	public String getURI(String path);

	public Credentials getCredentials(String user, String password);

	public boolean generateUserFolders(String loginName) throws RepositoryException;

	public boolean getExistence(String absolutePath) throws RepositoryException;

	public List<RepositoryItemVersionInfo> getVersions(String parentPath, String fileName) throws RepositoryException;

	public <T extends RepositoryItem> T getRepositoryItem(String path) throws RepositoryException;
	public RepositoryItem getRepositoryItem(User user, String path) throws RepositoryException;
	public <T extends RepositoryItem> T getRepositoryItemAsRootUser(String path) throws RepositoryException;

	public Collection<RepositoryItem> getChildNodes(User user, String path) throws RepositoryException;
	public Collection<RepositoryItem> getChildNodesAsRootUser(String path) throws RepositoryException;
	public Collection<RepositoryItem> getChildResources(String path) throws RepositoryException;

	public boolean isFolder(String path) throws RepositoryException;
	public boolean isHiddenFile(String name) throws RepositoryException;

	public void registerRepositoryEventListener(RepositoryEventListener listener);

	public int getChildCountExcludingFoldersAndHiddenFiles(String path) throws RepositoryException;
	public List<String> getChildPathsExcludingFoldersAndHiddenFiles(String path) throws RepositoryException;
	public List<String> getChildFolderPaths(String path) throws RepositoryException;
	public String getParentPath(String path) throws RepositoryException;

	public String createUniqueFileName(String path, String scope);

	public String getUserHomeFolder(User user);

	public VersionManager getVersionManager() throws RepositoryException;

	public void removeProperty(String path, String name) throws RepositoryException;

	public String getPath(String path);

	public Session getSession(User user) throws RepositoryException;

	public void addRepositoryChangeListeners(RepositoryEventListener listener);

	public OutputStream getOutputStream(String path) throws IOException, RepositoryException;

	public AccessControlList getAccessControlList(String path);
	public void storeAccessControlList(AccessControlList acl);

	public String getName(String path) throws RepositoryException;

	public long getCreationDate(String path) throws RepositoryException;
	public long getLastModified(String path) throws RepositoryException;

	public long getLength(String path) throws RepositoryException;

	public boolean isLocked(String path) throws RepositoryException;
	public Lock lock(String path, boolean isDeep, boolean isSessionScoped) throws RepositoryException;
	public void unLock(String path) throws RepositoryException;

	public List<RepositoryItem> getSiblingResources(String path) throws RepositoryException;

	public void logout(Session session);

}