package com.idega.core.file.util;

import java.io.InputStream;

/**
 *
 * @author <a href="civilis@idega.com">Vytautas Čivilis</a>
 * @version $Revision: 1.1 $
 *
 * Last modified: $Date: 2008/06/28 19:03:51 $ by $Author: civilis $
 *
 */
public class FileInfo {

	private String fileName, type;

	private Long contentLength;

	private InputStream source;

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public Long getContentLength() {
		return contentLength;
	}

	public void setContentLength(Long contentLength) {
		this.contentLength = contentLength;
	}

	public InputStream getSource() {
		return source;
	}

	public void setSource(InputStream source) {
		this.source = source;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

}