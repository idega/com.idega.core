package com.idega.file.bean;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Collection;

import com.idega.repository.bean.RepositoryItem;

public class FileItem implements RepositoryItem {

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
}