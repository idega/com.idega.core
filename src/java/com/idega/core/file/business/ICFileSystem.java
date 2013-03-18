/*
 * Created on 8.7.2003 by  tryggvil in project com.project
 */
package com.idega.core.file.business;

import java.rmi.RemoteException;

import com.idega.business.IBOService;
import com.idega.core.file.data.ICFile;

/**
 * FileSystem: This is the interface to the file system in idegaWeb.
 * This interface should be used whenever possible instead of com.idega.block.media.business.MediaBusines.
 * Copyright (C) idega software 2003
 * @author <a href="mailto:tryggvi@idega.is">Tryggvi Larusson</a>
 * @version 1.0
 */
public interface ICFileSystem extends IBOService {
	/**
		 * Initializes the filesystem correctly
		 * @throws RemoteException
		 */
	public void initialize()throws RemoteException;

	/**
	 * Get the URI to a file on the webserver.
	 * @param file The file to get the url to
	 * @return A String which is the url to the file
	 * @throws RemoteException
	 */
	public String getFileURI(ICFile file)throws RemoteException;
	/**
	 * Get the URI to a file on the webserver.
	 * @param fileId The id of the file to get the url to
	 * @return A String which is the url to the file
	 * @throws RemoteException
	 */
	public String getFileURI(int fileId)throws RemoteException;

	/**
	 * Get the URI to a file on the webserver.
	 * @param fileId The id of the file to get the url to
	 * @param datasource The datasource of the db to get the file from
	 * @return A String which is the url to the file
	 * @throws RemoteException
	 */
	public String getFileURI(int fileId, String datasource)throws RemoteException;

	/**
	 * DRAFT OF METHODS TO BE IN THIS CLASS:
	 *
	 * public ICFile getPublicRootFolder();
	 * public ICFile getUserHomeFolder(ICUser user);
	 * public ICFile getGroupHomeFolder(ICGroup group);
	 *
	 * public ICFile createFileUnderPublicRoot(ICUser creator,String name);
	 * public ICFile createFileUnderUserHome(ICUser creator,String name);
	 * public ICFile createFileUnderGroupHome(ICUser creator,ICGroup group,String name);
	 *
	 * public ICFile createFileUnderFolder(ICUser creator,ICFile folder,String name);
	 *
	 * public void deleteFile(ICFile file,ICUser committer);
	 * public void moveFileUnder(ICFile file,ICFile oldFolder,ICFile newFolder,ICUser committer);
	 *
	 *
	 */

	/**
	 * Get the URI for an icon based on mime type
	 *
	 * @param mimeType A String representation of the mime type
	 * @return A String which is the URI for the icon
	 * @throws RemoteException
	 */
	public String getIconURIByMimeType(String mimeType) throws RemoteException;

	/**
	 * Get the URI for an icon for a file, based on the files mime type
	 *
	 * @param file The file to get the icon URI for
	 * @return A String which is the URI for the icon
	 * @throws RemoteException
	 */
	public String getFileIconURI(ICFile file) throws RemoteException;
}