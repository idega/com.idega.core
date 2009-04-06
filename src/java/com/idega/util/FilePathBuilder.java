package com.idega.util;

import java.io.File;

/**
 * maintains the file or folder path, where folder path contains leading separator symbol. default
 * separator symbol is slash
 * 
 * @author <a href="mailto:civilis@idega.com">Vytautas Čivilis</a>
 * @version $Revision: 1.3 $ Last modified: $Date: 2009/04/06 14:47:33 $ by $Author: civilis $
 */
public class FilePathBuilder {
	
	private StringBuilder path;
	private String separator = CoreConstants.SLASH;
	
	public FilePathBuilder() {
		path = new StringBuilder(CoreConstants.EMPTY);
	}
	
	public FilePathBuilder(String context) {
		
		path = new StringBuilder(CoreConstants.EMPTY);
		
		if (!StringUtil.isEmpty(context)) {
			
			addFolder(context);
		}
	}
	
	public FilePathBuilder(String context, String separator) {
		
		path = new StringBuilder(CoreConstants.EMPTY);
		this.separator = separator;
		
		if (!StringUtil.isEmpty(context)) {
			
			addFolder(context);
		}
	}
	
	public FilePathBuilder addFolder(String folderName) {
		
		if (path.length() != 0 && folderName.startsWith(separator)) {
			
			folderName = folderName.substring(1);
		}
		
		path.append(folderName);
		
		if (!folderName.endsWith(CoreConstants.SLASH))
			path.append(CoreConstants.SLASH);
		
		return this;
	}
	
	public FilePathBuilder addFile(String fileName) {
		
		path.append(fileName);
		return this;
	}
	
	/**
	 * doesn't modify the path
	 * 
	 * @param fileName
	 * @return adds the file name to the path, but doesn't modify it
	 */
	public String getPathWithAddedFile(String fileName) {
		
		return path + fileName;
	}
	
	public File getFile() {
		
		return new File(path.toString());
	}
	
	@Override
	public String toString() {
		return path.toString();
	}
}