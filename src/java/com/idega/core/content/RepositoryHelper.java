package com.idega.core.content;

import java.io.IOException;
import java.io.InputStream;
import java.rmi.RemoteException;
import java.util.StringTokenizer;

import javax.jcr.ItemExistsException;
import javax.jcr.Node;
import javax.jcr.PathNotFoundException;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.ValueFormatException;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import com.idega.util.CoreConstants;

@Scope("singleton")
@Service(RepositoryHelper.SPRING_BEAN_IDENTIFIER)
public class RepositoryHelper {

	public static final String SPRING_BEAN_IDENTIFIER="repositoryHelper";
	
	public static String NODE_TYPE_FOLDER="nt:folder";
	public static String NODE_TYPE_FILE="nt:file";
	public static String NODE_TYPE_UNSTRUCTURED="nt:unstructured";
	
	public static String NODE_CONTENT="jcr:content";
	
	public static String PROPERTY_BINARY_DATA="jcr:data";
	
	public static String PATH_FILES=CoreConstants.PATH_FILES_ROOT;
	public static String PATH_CMS=CoreConstants.CONTENT_PATH;
	
	public static final String SLASH="/";
	
	public RepositoryHelper(){}
	
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
		return updateFileContents(session,absolutePath,fileContents,true);
	}
	
	public Node updateFileContents(Session session,String absolutePath,InputStream fileContents,boolean createFile) throws RepositoryException{
		Node fileNode=null;
		try{
			fileNode = getFile(session, absolutePath);
		}
		catch(PathNotFoundException pfe){
			if(createFile){
				//String parentFolderPath = absolutePath.substring(0,absolutePath.lastIndexOf(SLASH));
				//Node folder = getFolder(session, parentFolderPath);
				fileNode = createFile(session, absolutePath);
			}
			else{
				throw pfe;
			}
		}
		Node contentNode = fileNode.getNode(NODE_CONTENT);
		contentNode.getProperty(PROPERTY_BINARY_DATA).setValue(fileContents);
		return fileNode;
	}
	
	public InputStream getFileContents(Session session,String absolutePath) throws RepositoryException{
		Node fileNode = getFile(session, absolutePath);
		return getFileContents(fileNode);
	}

	public static InputStream getFileContents(Node fileNode)
			throws PathNotFoundException, RepositoryException,
			ValueFormatException {
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
	
	/**
	 * Creates all the folders in path 
	 * @param path Path with all the folders to create. 
	 * Should hold all the folders after Server URI (Typically /cms/content/)
	 * @throws HttpException
	 * @throws RemoteException
	 * @throws IOException
	 * @return true if it needed to create the folders
	 */
	public boolean createAllFoldersInPath(Session session,String path) throws RepositoryException, IOException {
		boolean hadToCreate = false;
		//WebdavResource rootResource = getWebdavRootResource(credentials);
		Node rootNode = session.getRootNode();
		hadToCreate = !getExistence(session,path);
		if(hadToCreate){
			//StringBuffer createPath = new StringBuffer(getWebdavServerURI());
			StringBuffer createPath = new StringBuffer();
			StringTokenizer st = new StringTokenizer(path,"/");
			while(st.hasMoreTokens()) {
				String childFolder = st.nextToken();
				//rootResource.mkcolMethod(createPath.toString());
				String sCreatePath = createPath.toString();
				Node node = rootNode.getNode(sCreatePath);
				try{
					Node childNode = node.addNode(childFolder, NODE_TYPE_FOLDER);
					childNode.save();
				}
				catch(ItemExistsException ie){}

				createPath.append("/").append(childFolder);
			}
		}
		return hadToCreate;
		
	}
	
	public boolean getExistence(Session session,String path) throws RepositoryException, IOException{
		if(path==null){
			return false;
		}
		try {
			Node node = session.getRootNode().getNode(path);
			if(node!=null){
				return true;
			}
		}
		catch (PathNotFoundException e) {
			return false;
		}
		return false;
	}
	/*
	public Node getRootNodeAuthenticatedAsRoot(){
		
	}
	
	public Session getRootUserSession(){
		
	}*/
	
}
