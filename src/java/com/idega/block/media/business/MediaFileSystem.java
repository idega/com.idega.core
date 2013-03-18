/*
 * $Id: MediaFileSystem.java,v 1.3 2004/12/15 16:02:07 palli Exp $
 * Created on Dec 15, 2004
 *
 * Copyright (C) 2004 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.block.media.business;

import java.rmi.RemoteException;

import com.idega.business.IBOService;
import com.idega.core.file.business.ICFileSystem;
import com.idega.core.file.data.ICFile;

/**
 *
 *  Last modified: $Date: 2004/12/15 16:02:07 $ by $Author: palli $
 *
 * @author <a href="mailto:palli@idega.com">palli</a>
 * @version $Revision: 1.3 $
 */
public interface MediaFileSystem extends IBOService, ICFileSystem {

	/**
	 * @see com.idega.block.media.business.MediaFileSystemBean#getFileURI
	 */
	@Override
	public String getFileURI(ICFile file) throws java.rmi.RemoteException;

	/**
	 * @see com.idega.block.media.business.MediaFileSystemBean#getFileURI
	 */
	@Override
	public String getFileURI(int fileId) throws java.rmi.RemoteException;

	/**
	 * @see com.idega.block.media.business.MediaFileSystemBean#initialize
	 */
	@Override
	public void initialize() throws java.rmi.RemoteException;

	/**
	 * @see com.idega.block.media.business.MediaFileSystemBean#getFileIconURI
	 */
	@Override
	public String getFileIconURI(ICFile file) throws RemoteException;

	/**
	 * @see com.idega.block.media.business.MediaFileSystemBean#getIconURIByMimeType
	 */
	@Override
	public String getIconURIByMimeType(String mimeType) throws RemoteException;
}
