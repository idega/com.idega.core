package com.idega.file.bean;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import javax.jcr.RepositoryException;

import com.idega.repository.bean.RepositoryItem;
import com.idega.util.ArrayUtil;

public class FileItem extends RepositoryItem {

	private static final long serialVersionUID = 9168933422320317136L;

	private File file;

	public FileItem(File file) {
		this.file = file;
	}

	@Override
	public InputStream getInputStream() throws IOException {
		return new FileInputStream(file);
	}

	@Override
	public String getName() {
		return file.getName();
	}

	@Override
	public long getLength() {
		return file.length();
	}

	@Override
	public boolean delete() {
		return file.delete();
	}

	@Override
	public Collection<RepositoryItem> getChildResources() {
		return null;
	}

	@Override
	public boolean isCollection() {
		return false;
	}

	@Override
	public boolean exists() {
		return false;
	}

	@Override
	public String getPath() {
		return null;
	}

	@Override
	public URL getHttpURL() {
		return null;
	}

	@Override
	public String getMimeType() {
		return null;
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
}