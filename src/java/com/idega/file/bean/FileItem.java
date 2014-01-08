package com.idega.file.bean;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.idega.core.file.data.ICFile;
import com.idega.repository.bean.RepositoryItem;
import com.idega.util.ArrayUtil;
import com.idega.util.ListUtil;

public class FileItem implements RepositoryItem {

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
	public boolean isDirectory() {
		if (icFile != null) {
			return false;
		}

		return file.isDirectory();
	}

	private List<FileItem> children;

	@Override
	public List<FileItem> getChildren() {
		if (children != null) {
			return children;
		}

		children = new ArrayList<FileItem>();

		if (icFile != null) {
			@SuppressWarnings("unchecked")
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