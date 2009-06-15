/*
 * Created on 23.8.2004
 * 
 * Copyright (C) 2004 Idega hf. All Rights Reserved.
 * 
 * This software is the proprietary information of Idega hf. Use is subject to
 * license terms.
 */
package com.idega.io;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;

import javax.servlet.http.HttpServletRequest;

import com.idega.core.file.business.ICFileSystem;
import com.idega.core.file.business.ICFileSystemFactory;
import com.idega.core.file.data.ICFile;
import com.idega.core.file.data.ICFileHome;
import com.idega.data.IDOLookup;
import com.idega.presentation.IWContext;
import com.idega.user.data.User;
import com.idega.util.CoreConstants;
import com.idega.util.FileUtil;
import com.idega.util.IOUtil;
import com.idega.util.ListUtil;
import com.idega.util.StringUtil;

/**
 * @author aron
 * 
 * DownloadWriter To be used when files are downloaded to the user. The response
 * is set so that the client browser opens a save dialog. Handles both files
 * from database and absolute paths from filesystem if read permission is active
 */
public class DownloadWriter implements MediaWritable {

	public final static String PRM_ABSOLUTE_FILE_PATH = "abs_fpath";

	public final static String PRM_RELATIVE_FILE_PATH = "rel_fpath";

	public final static String PRM_FILE_NAME = "alt_f_name";

	public final static String PRM_FILE_ID = "fileId";

	private File file = null;

	private ICFile icFile = null;

	private URL url = null;

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.idega.io.MediaWritable#getMimeType()
	 */
	public String getMimeType() {
		return "application/octet-stream";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.idega.io.MediaWritable#init(javax.servlet.http.HttpServletRequest,
	 *      com.idega.presentation.IWContext)
	 */
	public void init(HttpServletRequest req, IWContext iwc) {
		String fileId = iwc.getParameter(PRM_FILE_ID);
		String absPath = iwc.getParameter(PRM_ABSOLUTE_FILE_PATH);
		String relPath = iwc.getParameter(PRM_RELATIVE_FILE_PATH);
		String altFileName = iwc.getParameter(PRM_FILE_NAME);
		if (fileId != null) {
			try {
				ICFileSystem fsystem = ICFileSystemFactory.getFileSystem(iwc);
				String fileURL = fsystem.getFileURI(Integer.valueOf(fileId).intValue());
				this.file = new File(iwc.getIWMainApplication().getRealPath(fileURL));
				this.icFile = ((ICFileHome) IDOLookup.getHome(ICFile.class)).findByPrimaryKey(Integer.valueOf(fileId));
				//setAsDownload(iwc,icFile.getName(),icFile.getFileSize().intValue());
				setAsDownload(iwc, this.file.getName(), (int) this.file.length());
			}
			catch (Exception e) {
				this.icFile = null;
			}
		}
		else if (absPath != null) {
			this.file = new File(absPath);
			if (this.file != null && this.file.exists() && this.file.canRead()) {
				setAsDownload(iwc, this.file.getName(), (int) this.file.length());
			}
		}
		else if (relPath != null && altFileName == null) {
			this.file = new File(iwc.getIWMainApplication().getRealPath(relPath));
			if (this.file != null && this.file.exists() && this.file.canRead()) {
				setAsDownload(iwc, this.file.getName(), (int) this.file.length());
			}
		}
		else if (relPath != null && altFileName != null) {
			try {
				if(relPath.startsWith("/")){
					relPath = relPath.substring(1);
				}
				this.url = new URL(iwc.getServerURL()+relPath);
				setAsDownload(iwc, altFileName, -1);
			}
			catch (MalformedURLException e) {
				e.printStackTrace();
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.idega.io.MediaWritable#writeTo(java.io.OutputStream)
	 */
	public void writeTo(OutputStream out) throws IOException {
		InputStream downloadStream = null;
		if (this.file != null && this.file.exists() && this.file.canRead() && this.file.length() > 0) {
			downloadStream = new BufferedInputStream(new FileInputStream(this.file));
		}
		else if (this.icFile != null) {
			downloadStream = new BufferedInputStream(this.icFile.getFileValue());
		}
		else if (this.url != null) {
			//added for real relative path streaming
			downloadStream = new BufferedInputStream(this.url.openStream());
		}
		
		if (downloadStream == null) {
			throw new IOException("No file to download!");
		}
		
		try {
			FileUtil.streamToOutputStream(downloadStream, out);
		} catch(Exception e) {
			e.printStackTrace();
		} finally {
			out.flush();
			IOUtil.closeOutputStream(out);
			IOUtil.closeInputStream(downloadStream);
		}
	}

	public void setAsDownload(IWContext iwc, String filename, int fileLength) {
		setAsDownload(iwc, filename, fileLength, CoreConstants.EMPTY);
	}
	
	public void setAsDownload(IWContext iwc, String filename, int fileLength, Object icFileIdOrHashValue) {
		if (icFileIdOrHashValue instanceof String) {
			setAsDownload(iwc, filename, fileLength, icFileIdOrHashValue.toString());
		} else if (icFileIdOrHashValue instanceof Integer) {
			setAsDownload(iwc, filename, fileLength, (Integer) icFileIdOrHashValue);
		} else {
			setAsDownload(iwc, filename, fileLength);
		}
	}	
	
	public void setAsDownload(IWContext iwc, String filename, int fileLength, String icFileId) {
		if (!StringUtil.isEmpty(icFileId)) {
			markFileAsDownloaded(iwc, icFileId);
		}
		
		sendResponse(iwc, filename, fileLength);
	}
	
	public void setAsDownload(IWContext iwc, String filename, int fileLength, Integer hash) {
		if (hash != null) {
			markFileAsDownloaded(iwc, hash);
		}
		
		sendResponse(iwc, filename, fileLength);
	}
	
	private void sendResponse(IWContext iwc, String filename, int fileLength) {
		iwc.getResponse().setHeader("Content-Disposition", "attachment;filename=\"" + filename + "\"");
		if (fileLength > 0) {
			iwc.getResponse().setContentLength(fileLength);
		}
	}
	
	protected boolean markFileAsDownloaded(IWContext iwc, Integer hash) {
		return markFileAsDownloaded(iwc, getFile(hash));
	}
	
	protected boolean markFileAsDownloaded(IWContext iwc, String icFileId) {
		return markFileAsDownloaded(iwc, getFile(icFileId));
	}
	
	protected boolean markFileAsDownloaded(IWContext iwc, ICFile attachment) {
		if (attachment == null) {
			return false;
		}
		
		User user = iwc.isLoggedOn() ? iwc.getCurrentUser() : null;
		if (user == null) {
			return false;
		}
		
		Collection<User> downloadedBy = attachment.getDownloadedBy();
		if (!ListUtil.isEmpty(downloadedBy) && downloadedBy.contains(user)) {
			return true;
		}
		
		try {
			attachment.addDownloadedBy(user);
			attachment.store();
			return true;
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		return false;
	}
	
	private ICFile getFile(Integer hash) {
		if (hash == null) {
			return null;
		}
		
		try {
			ICFileHome fileHome = (ICFileHome) IDOLookup.getHome(ICFile.class);
			return fileHome.findByHash(hash);
		} catch(Exception e) {}
		
		return null;
	}
	
	private ICFile getFile(String icFileId) {
		if (StringUtil.isEmpty(icFileId)) {
			return null;
		}
		
		try {
			ICFileHome fileHome = (ICFileHome) IDOLookup.getHome(ICFile.class);
			return fileHome.findByPrimaryKey(icFileId);
		} catch(Exception e) {}
		
		return null;
	}
}