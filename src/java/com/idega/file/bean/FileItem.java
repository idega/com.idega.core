package com.idega.file.bean;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.idega.core.file.data.ICFile;
import com.idega.repository.bean.RepositoryItem;

public class FileItem implements RepositoryItem {

	private File file = null;
	private ICFile icFile = null;
	
	public FileItem(File file) {
		this.file = file;
	}
	public FileItem(ICFile file) {
		this.icFile = file;
	}
	
	public InputStream getInputStream() throws IOException {
		if(icFile != null){
			return icFile.getFileValue();
		}
		return new FileInputStream(file);
	}

	public String getName() {
		if(icFile != null){
			return icFile.getName();
		}
		return file.getName();
	}

	public long getLength() {
		if(icFile != null){
			return icFile.getFileSize().intValue();
		}
		return file.length();
	}

	public boolean delete() {
		if(icFile != null){
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
}