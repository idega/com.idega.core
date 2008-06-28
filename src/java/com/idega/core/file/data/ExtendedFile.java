package com.idega.core.file.data;

import java.net.URI;

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
	private URI fileUri;
	
	public ExtendedFile() {}
	
	public ExtendedFile(URI fileUri, String info) {
		setFileUri(fileUri);
		setFileInfo(info);
	}

	public String getFileInfo() {
		return fileInfo;
	}

	public void setFileInfo(String fileInfo) {
		this.fileInfo = fileInfo;
	}

	public URI getFileUri() {
		return fileUri;
	}

	public void setFileUri(URI fileUri) {
		this.fileUri = fileUri;
	}	
}