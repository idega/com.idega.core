package com.idega.core.content;

import java.io.InputStream;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import com.idega.util.CoreConstants;

public class RepositoryHelper {

	public static String NODE_TYPE_FOLDER="nt:folder";
	public static String NODE_TYPE_FILE="nt:file";
	public static String NODE_TYPE_UNSTRUCTURED="nt:unstructured";
	
	public static String NODE_CONTENT="jcr:content";
	
	public static String PROPERTY_BINARY_DATA="jcr:data";
	
	public static String PATH_FILES=CoreConstants.PATH_FILES_ROOT;
	public static String PATH_CMS=CoreConstants.CONTENT_PATH;
	
	public Node createFolder(Session session,String absolutePath) throws RepositoryException{
		return session.getRootNode().addNode(absolutePath,NODE_TYPE_FOLDER);
	}
	
	public Node createFile(Session session,String absolutePath) throws RepositoryException{
		return session.getRootNode().addNode(absolutePath,NODE_TYPE_FILE);
	}
	
	public Node getFolder(Session session,String absolutePath) throws RepositoryException{
		return session.getRootNode().getNode(absolutePath);
	}
	
	public Node getFile(Session session,String absolutePath) throws RepositoryException{
		return session.getRootNode().getNode(absolutePath);
	}
	
	public Node updateFileContents(Session session,String absolutePath,InputStream fileContents) throws RepositoryException{
		Node fileNode = getFile(session, absolutePath);
		Node contentNode = fileNode.getNode(NODE_CONTENT);
		contentNode.getProperty(PROPERTY_BINARY_DATA).setValue(fileContents);
		return fileNode;
	}
	
	public InputStream getFileContents(Session session,String absolutePath) throws RepositoryException{
		Node fileNode = getFile(session, absolutePath);
		Node contentNode = fileNode.getNode(NODE_CONTENT);
		return contentNode.getProperty(PROPERTY_BINARY_DATA).getStream();
	}
	
	/**
	 * <p>
	 * This article returns the standard root or 'baseFolderPath' for files in the repository.<br/>
	 * By default this is /files
	 * </p>
	 * @return
	 * @throws RepositoryException 
	 */
	public Node getFilesFolder(Session session) throws RepositoryException{
		return getFolder(session, CoreConstants.PATH_FILES_ROOT);
	}
	
	/**
	 * <p>
	 * This article returns the standard root or 'baseFolderPath' for content in the cms system.<br/>
	 * By default this is /files/cms
	 * </p>
	 * @return
	 * @throws RepositoryException 
	 */
	public Node getContentBaseFolder(Session session) throws RepositoryException{
		return getFolder(session, CoreConstants.CONTENT_PATH);
	}
	
}
