package com.idega.file.bean;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.jcr.RepositoryException;

import com.idega.core.file.data.ICFile;
import com.idega.core.file.util.MimeTypeUtil;
import com.idega.repository.bean.RepositoryItem;
import com.idega.util.ArrayUtil;
import com.idega.util.ListUtil;

public class FileItem extends RepositoryItem {

	private static final long serialVersionUID = 9168933422320317136L;

	private File file = null;
	private ICFile icFile = null;

	public FileItem(File file) {
		this.file = file;
	}
	public FileItem(ICFile file) {
		this.icFile = file;
	}

	@Override
	public InputStream getInputStream() throws IOException {
		if (icFile != null) {
			return icFile.getFileValue();
		}

		return new FileInputStream(file);
	}

	@Override
	public String getName() {
		if (icFile != null) {
			return icFile.getName();
		}

		return file.getName();
	}

	@Override
	public long getLength() {
		if (icFile != null) {
			return icFile.getFileSize();
		}

		return file.length();
	}

	@Override
	public boolean delete() {
		if (icFile != null) {
			try {
				icFile.delete();
			} catch (SQLException e) {
				Logger.getLogger(this.getClass().getName()).log(Level.WARNING,
						"Failed to delete icFile " + icFile.getId(), e);
				return false;
			}
			return true;
		}

		return file.delete();
	}

	@Override
	public Collection<RepositoryItem> getChildResources() {
		return null;
	}

	@Override
	public boolean isCollection() {
		return icFile == null ?
				file == null ? false : file.isDirectory() :
				icFile.isFolder();
	}

	@Override
	public boolean exists() {
		return icFile == null ?
				file == null ? false : file.exists() :
				true;
	}

	@Override
	public String getPath() {
		String path =	icFile == null ?
							file == null ? null : file.getPath() :
						icFile.getFileUri();
		return path;
	}

	@Override
	public URL getHttpURL() {
		return null;
	}

	@Override
	public String getMimeType() {
		return MimeTypeUtil.resolveMimeTypeFromFileName(getName());
	}

	@Override
	public long getCreationDate() {
		return getLastModified();
	}

	@Override
	public long getLastModified() {
		return file.lastModified();
	}

	@Override
	public String getParentPath() {
		return file.getParent();
	}

	@Override
	public RepositoryItem getParenItem() {
		return new FileItem(file.getParentFile());
	}

	@Override
	public Collection<RepositoryItem> getSiblingResources() {
		try {
			File parent = file.getParentFile();
			if (parent == null)
				return Collections.emptyList();

			File[] children = parent.listFiles();
			if (ArrayUtil.isEmpty(children))
				return Collections.emptyList();

			Collection<RepositoryItem> siblings = new ArrayList<RepositoryItem>();
			for (File child: children) {
				if (child.equals(file))
					continue;

				siblings.add(new FileItem(child));
			}

			return siblings;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return Collections.emptyList();
	}

	@Override
	public boolean createNewFile() throws IOException, RepositoryException {
		if (file == null)
			return false;

		if (!file.exists()) {
			return file.createNewFile();
		}

		return true;
	}

	@Override
	public boolean isDirectory() {
		if (icFile != null) {
			return false;
		}

		return file.isDirectory();
	}

	private List<RepositoryItem> children;

	@Override
	public List<RepositoryItem> getChildren() {
		if (children != null) {
			return children;
		}

		children = new ArrayList<RepositoryItem>();

		if (icFile != null) {
			Collection<ICFile> icFileChildren = icFile.getChildren();
			if (!ListUtil.isEmpty(icFileChildren)) {
				for (ICFile child: icFileChildren) {
					children.add(new FileItem(child));
				}
			}
			return children;
		}

		File[] files = file.listFiles();
		if (ArrayUtil.isEmpty(files)) {
			return children;
		}
		for (File file: files) {
			children.add(new FileItem(file));
		}
		return children;
	}

	@Override
	public String toString() {
		return "Name: " + getName() + ", directory: " + isDirectory() + ", children: " + getChildren();
	}
}