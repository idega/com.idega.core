package com.idega.core.file.data;

import java.io.File;

/**
 *
 * 
 * @author <a href="anton@idega.com">Anton Makarov</a>
 * @version Revision: 1.0 
 *
 * Last modified: May 8, 2008 by Author: Anton 
 *
 */

public class ExtendedFile {
	private String fileInfo;
	private File file;
	
	public ExtendedFile() {}
	
	public ExtendedFile(File file, String info) {
		setFile(file);
		setFileInfo(info);
	}

	public String getFileInfo() {
		return fileInfo;
	}

	public void setFileInfo(String fileInfo) {
		this.fileInfo = fileInfo;
	}

	public File getFile() {
		return file;
	}

	public void setFile(File file) {
		this.file = file;
	}	
}
