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
import java.net.URLEncoder;
import java.util.Collection;

import javax.ejb.FinderException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;

import com.idega.core.file.business.ICFileSystem;
import com.idega.core.file.business.ICFileSystemFactory;
import com.idega.core.file.data.ICFile;
import com.idega.core.file.data.ICFileHome;
import com.idega.data.IDOLookup;
import com.idega.presentation.IWContext;
import com.idega.repository.RepositoryService;
import com.idega.user.data.User;
import com.idega.util.CoreConstants;
import com.idega.util.FileUtil;
import com.idega.util.IOUtil;
import com.idega.util.ListUtil;
import com.idega.util.StringUtil;
import com.idega.util.expression.ELUtil;

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

	private String fileName;

	@Autowired
	private RepositoryService repositoryService;

	/*
	 * (non-Javadoc)
	 *
	 * @see com.idega.io.MediaWritable#getMimeType()
	 */
	@Override
	public String getMimeType() {
		return "application/octet-stream";
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.idega.io.MediaWritable#init(javax.servlet.http.HttpServletRequest,
	 *      com.idega.presentation.IWContext)
	 */
	@Override
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
	@Override
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
		this.fileName = filename;

		if (!StringUtil.isEmpty(icFileId)) {
			markFileAsDownloaded(iwc, icFileId);
		}

		sendResponse(iwc, filename, fileLength);
	}

	public void setAsDownload(IWContext iwc, String filename, int fileLength, Integer hash) {
		this.fileName = filename;

		if (hash != null) {
			markFileAsDownloaded(iwc, hash);
		}

		sendResponse(iwc, filename, fileLength);
	}

	private void sendResponse(IWContext iwc, String filename, int fileLength) {
		HttpServletResponse response = iwc.getResponse();
		response.setHeader("Expires", String.valueOf(0));
		response.setHeader("Cache-Control", "must-revalidate, post-check=0, pre-check=0");
		response.setHeader("Content-Type", "application/force-download");
		response.setHeader("Content-Type", "application/octet-stream");
		response.setHeader("Content-Type", "application/download");
        response.setHeader("Content-Disposition", "attachment;filename=\"" + filename + "\"");
        response.setHeader("Content-Transfer-Encoding", "binary");
		if (fileLength > 0)
			response.setContentLength(fileLength);
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

		ICFileHome fileHome = null;
		try {
			fileHome = (ICFileHome) IDOLookup.getHome(ICFile.class);
		} catch(Exception e) {
			e.printStackTrace();
		}
		if (fileHome == null) {
			return null;
		}

		ICFile file = null;
		try {
			file = fileHome.findByHash(hash);
		} catch(FinderException e) {
		} catch(Exception e) {
			e.printStackTrace();
		}

		if (file == null) {
			file = createFile(fileHome, hash);
		}

		return file;
	}

	private ICFile createFile(ICFileHome fileHome, Integer hash) {
		try {
			ICFile file = fileHome.create();
			file.setHash(hash);

			if (!StringUtil.isEmpty(getFileName())) {
				file.setName(URLEncoder.encode(getFileName(), CoreConstants.ENCODING_UTF8));
			}

			file.store();
			return file;
		} catch(Exception e) {
			e.printStackTrace();
		}
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

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	protected RepositoryService getRepositoryService() {
		if (repositoryService == null) {
			ELUtil.getInstance().autowire(this);
		}
		return repositoryService;
	}
}