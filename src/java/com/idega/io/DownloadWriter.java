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
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ejb.FinderException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;

import com.idega.core.file.business.ICFileSystem;
import com.idega.core.file.business.ICFileSystemFactory;
import com.idega.core.file.data.ICFile;
import com.idega.core.file.data.ICFileHome;
import com.idega.core.file.util.MimeTypeUtil;
import com.idega.data.IDOLookup;
import com.idega.file.security.FileAccessService;
import com.idega.presentation.IWContext;
import com.idega.repository.RepositoryService;
import com.idega.user.data.User;
import com.idega.util.CoreConstants;
import com.idega.util.CoreUtil;
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

	public final static String	PRM_ABSOLUTE_FILE_PATH = "abs_fpath",
								PRM_RELATIVE_FILE_PATH = "rel_fpath",
								PRM_FILE_NAME = "alt_f_name",
								PRM_FILE_UNIQUE_ID = "fileUUId",
								PRM_FILE_TOKEN = "fileToken";

	private Logger LOGGER = null;

	private File file = null;

	private ICFile icFile = null;

	private URL url = null;

	private String fileName;

	@Autowired
	private FileAccessService fileAccessService;

	@Autowired
	private RepositoryService repositoryService;

	protected FileAccessService getFileAccessService() {
		if (fileAccessService == null) {
			ELUtil.getInstance().autowire(this);
		}
		return fileAccessService;
	}

	protected Logger getLogger() {
		if (LOGGER == null) {
			LOGGER = Logger.getLogger(getClass().getName());
		}
		return LOGGER;
	}

	protected void setFile(File file) {
		if (file == null) {
			getLogger().warning("File is undefined!");
			return;
		}
		if (!file.exists()) {
			getLogger().warning("File " + file + " does not exist!");
			return;
		}
		if (!file.canRead()) {
			getLogger().warning("There are no rights provided to read from file: " + file);
			return;
		}

		this.file = file;
	}

	@Override
	public String getMimeType() {
		if (fileName == null)
			return MimeTypeUtil.MIME_TYPE_APPLICATION;

		String mimeType = MimeTypeUtil.resolveMimeTypeFromFileName(fileName);
		return StringUtil.isEmpty(mimeType) ? MimeTypeUtil.MIME_TYPE_APPLICATION : mimeType;
	}

	protected boolean isAvailable(IWContext iwc, ICFile file, String fileUniqueId, String fileToken) throws Exception {
		boolean available = getFileAccessService().isAvailable(iwc, file, fileUniqueId, fileToken);

		if (!available) {
			this.icFile = null;
		}

		return available;
	}

	protected boolean hasPermission(IWContext iwc, String path) throws Exception {
		return getFileAccessService().hasPermission(iwc, path);
	}

	protected boolean hasPermission(IWContext iwc, User user, Collection<String> paths) throws Exception {
		return getFileAccessService().hasPermission(iwc, user, paths);
	}

	@Override
	public void init(HttpServletRequest req, IWContext iwc) {
		String fileUniqueId = iwc.getParameter(PRM_FILE_UNIQUE_ID);

		String fileToken = iwc.getParameter(PRM_FILE_TOKEN);
		if (StringUtil.isEmpty(fileToken) && !StringUtil.isEmpty(fileUniqueId)) {
			getLogger().warning("File's token not provided");
			this.icFile = null;
			return;
		}

		String absPath = iwc.getParameter(PRM_ABSOLUTE_FILE_PATH);
		String relPath = iwc.getParameter(PRM_RELATIVE_FILE_PATH);
		String altFileName = iwc.getParameter(PRM_FILE_NAME);
		if (fileUniqueId != null) {
			try {
				ICFile icFile = ((ICFileHome) IDOLookup.getHome(ICFile.class)).findByUUID(fileUniqueId);
				if (icFile == null) {
					getLogger().warning("Did not find file by unique ID " + fileUniqueId);
					this.icFile = null;
					return;
				}

				if (!isAvailable(iwc, icFile, fileUniqueId, fileToken)) {
					this.icFile = null;
					return;
				}

				this.icFile = icFile;

				ICFileSystem fsystem = ICFileSystemFactory.getFileSystem(iwc);
				String fileURL = fsystem.getFileURI(iwc, fileUniqueId, fileToken);
				this.file = new File(iwc.getIWMainApplication().getRealPath(fileURL));
				setAsDownload(iwc, this.file.getName(), (int) this.file.length(), icFile.getId());
			} catch (Exception e) {
				getLogger().log(Level.WARNING, "No access to file + " + fileUniqueId, e);
				this.icFile = null;
			}

		} else if (absPath != null) {
			try {
				if (!getFileAccessService().hasPermission(iwc, absPath)) {
					this.url = null;
					this.file = null;
					return;
				}
			} catch (Exception e) {
				getLogger().log(Level.WARNING, "No access to abs. path " + absPath, e);
				this.url = null;
				this.file = null;
				return;
			}

			if (absPath.startsWith("http")) {
				try {
					this.url = new URL(absPath);
				} catch (MalformedURLException e) {
					getLogger().log(Level.WARNING, "Error creating URL: " + absPath, e);
				}
				setAsDownload(iwc, altFileName, -1);
			} else {
				this.file = new File(absPath);
				if (this.file != null && this.file.exists() && this.file.canRead()) {
					setAsDownload(iwc, this.file.getName(), (int) this.file.length());
				}
			}

		} else if (relPath != null && altFileName == null) {
			try {
				if (!getFileAccessService().hasPermission(iwc, relPath)) {
					this.url = null;
					this.file = null;
					return;
				}
			} catch (Exception e) {
				getLogger().log(Level.WARNING, "No access to rel. path " + relPath, e);
				this.url = null;
				this.file = null;
				return;
			}

			this.file = new File(iwc.getIWMainApplication().getRealPath(relPath));
			if (this.file != null && this.file.exists() && this.file.canRead()) {
				setAsDownload(iwc, this.file.getName(), (int) this.file.length());
			}

		} else if (relPath != null && altFileName != null) {
			try {
				if (!getFileAccessService().hasPermission(iwc, relPath)) {
					this.url = null;
					this.file = null;
					return;
				}
			} catch (Exception e) {
				getLogger().log(Level.WARNING, "No access to rel. path " + relPath + " and file " + altFileName, e);
				this.url = null;
				this.file = null;
				return;
			}

			try {
				if (relPath.startsWith(CoreConstants.SLASH)) {
					relPath = relPath.substring(1);
				}
				this.url = new URL(iwc.getServerURL() + relPath);
				setAsDownload(iwc, altFileName, -1);
			} catch (MalformedURLException e) {
				getLogger().log(Level.WARNING, "Error creating URL: " + relPath, e);
			}

		} else {
			getLogger().warning("Can not download: file's unique ID: " + fileUniqueId + ", file's token: " + fileToken + ", abs. path: " + absPath + ", rel. path: " + relPath +
					", alt. file name: " + altFileName);
		}
	}

	protected File getFileFromRepository(IWContext iwc, String pathInRepository) throws IOException {
		try {
			if (getFileAccessService().hasPermission(iwc, pathInRepository)) {
				return CoreUtil.getFileFromRepository(pathInRepository);
			}
		} catch (Exception e) {
			getLogger().log(Level.WARNING, "Error getting file " + pathInRepository, e);
			throw new IOException(e);
		}

		throw new IOException("No access to " + pathInRepository);
	}

	@Override
	public void writeTo(IWContext iwc, OutputStream out) throws IOException {
		InputStream downloadStream = null;
		if (this.file != null && this.file.exists() && this.file.canRead() && this.file.length() > 0) {
			getLogger().info("Dowloading file: " + file);
			downloadStream = new BufferedInputStream(new FileInputStream(this.file));

		} else if (this.icFile != null) {
			getLogger().info("Dowloading IC file: " + icFile);
			downloadStream = new BufferedInputStream(this.icFile.getFileValue());

		} else if (this.url != null) {
			getLogger().info("Dowloading from URL: " + url);
			downloadStream = new BufferedInputStream(this.url.openStream());
		}

		if (downloadStream == null) {
			throw new IOException("No file to download!");
		}

		try {
			FileUtil.streamToOutputStream(downloadStream, out);
		} catch(Exception e) {
			getLogger().log(Level.WARNING, "Error streaming from input to output streams", e);
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
			getLogger().log(Level.WARNING, "Error while setting that user " + user + " has downloaded file " + attachment, e);
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
			getLogger().log(Level.WARNING, "Error getting instance of " + ICFileHome.class, e);
		}
		if (fileHome == null) {
			return null;
		}

		ICFile file = null;
		try {
			file = fileHome.findByHash(hash);
		} catch(FinderException e) {
		} catch(Exception e) {
			getLogger().log(Level.WARNING, "Error getting file by hash: " + hash, e);
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
			getLogger().log(Level.WARNING, "Error while creating file using hash: " + hash, e);
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
		if (repositoryService == null)
			ELUtil.getInstance().autowire(this);
		return repositoryService;
	}

	protected File getFile() {
		return file;
	}

	protected ICFile getICFile() {
		return icFile;
	}

	protected void setICFile(ICFile icFile) {
		this.icFile = icFile;
	}

}