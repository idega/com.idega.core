package com.idega.io.serialization;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.idega.idegaweb.IWCacheManager;
import com.idega.idegaweb.IWMainApplication;
import com.idega.presentation.IWContext;
import com.idega.util.FileUtil;

/**
 * <p>Title: idegaWeb</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: idega Software</p>
 * @author <a href="thomas@idega.is">Thomas Hilbig</a>
 * @version 1.0
 * Created on Mar 26, 2004
 */
public abstract class WriterToFile {
	
	protected static final String DOWNLOAD_FOLDER = "download";
	protected static final char DOT = '.';
	
	protected IWContext iwc = null;
	protected Storable storable = null;
	
	public WriterToFile(IWContext iwc) {
		this.iwc = iwc;
	}
	
	public WriterToFile(Storable storable, IWContext iwc) {
		this(iwc);
		setSource(storable);
	}
	
	public abstract String createContainer() throws IOException;
	
	public abstract String getName();
	
	public abstract String getMimeType();
	
	public abstract OutputStream writeData(OutputStream destination) throws IOException;
	
	public void setSource(Storable storable) {
		this.storable = storable;
	}
		


  protected String getRealPathToFile(String fileName, String extension, long folderIdentifier) {
    IWMainApplication mainApp = iwc.getIWMainApplication();
    String separator = FileUtil.getFileSeparator();
    StringBuffer path = new StringBuffer(mainApp.getApplicationRealPath());
    path.append(IWCacheManager.IW_ROOT_CACHE_DIRECTORY)
      .append(separator)
      .append(DOWNLOAD_FOLDER);
    // check if the folder exists create it if necessary (see call of method createFolder below)
    // usually the folder should be already be there.
    // the folder is never deleted by this class
    String folderPath = path.toString();
    FileUtil.deleteAllFilesAndFolderInFolderOlderThan(folderPath, 300000);
		path.append(separator);
		path.append(folderIdentifier);
		folderPath = path.toString();
		FileUtil.createFolder(folderPath);
		path.append(separator).append(fileName);
		if (extension != null && extension.length() > 0) {
      path.append(DOT).append(extension);
		}
    return path.toString();
  }
  
  protected String getURLToFile(String reportName, String extension, long folderIdentifier) {
    IWMainApplication mainApp = iwc.getIWMainApplication();
    String separator = "/";
    String appContextURI = mainApp.getApplicationContextURI();
    StringBuffer uri = new StringBuffer(appContextURI);
    if(!appContextURI.endsWith(separator)){
			uri.append(separator);
    }
      uri.append(IWCacheManager.IW_ROOT_CACHE_DIRECTORY)
      .append(separator)
      .append(DOWNLOAD_FOLDER)
      .append(separator)
      .append(folderIdentifier)
      .append(separator)
      .append(reportName);
		if (extension != null && extension.length() > 0) {
      uri.append(DOT).append(extension);
		}
    return uri.toString();
  }  
  

	
  protected void close(InputStream input) {
  	try {
			if (input != null) {
				input.close();
			}
		}
		catch (IOException io) {
			// do not hide an existing exception
		}
  }		
  
  protected void close(OutputStream output) {
  	try {
  		if (output != null) {
  			output.close();
  		}
  	}
  	catch (IOException io) {
		// do not hide an existing exception
  	}
  }
  
	protected void writeFromStreamToStream(InputStream source, OutputStream destination) throws IOException { 
		// parts of this method  were copied from "Java in a nutshell" by David Flanagan
    byte[] buffer = new byte[4096];  // A buffer to hold file contents
    int bytesRead;                       
    while((bytesRead = source.read(buffer)) != -1)  {  // Read bytes until EOF
      destination.write(buffer, 0, bytesRead);    
    }
	}

}
