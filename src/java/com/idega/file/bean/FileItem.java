package com.idega.file.bean;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import com.idega.repository.bean.RepositoryItem;

public class FileItem implements RepositoryItem {

	private File file;
	
	public FileItem(File file) {
		this.file = file;
	}
	
	public InputStream getInputStream() throws IOException {
		return new FileInputStream(file);
	}

	public String getName() {
		return file.getName();
	}

	public long getLength() {
		return file.length();
	}

	public boolean delete() {
		return file.delete();
	}
}