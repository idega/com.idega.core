package com.idega.io;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.idega.core.file.data.ICFile;
import com.idega.idegaweb.IWApplicationContext;
import com.idega.idegaweb.IWCacheManager;
import com.idega.idegaweb.IWMainApplication;
import com.idega.util.FileUtil;

/**
 * <p>Title: idegaWeb</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: idega Software</p>
 * @author <a href="thomas@idega.is">Thomas Hilbig</a>
 * @version 1.0
 * Created on Mar 18, 2004
 */
public class ICFileWriter {
	
	protected static final String DOWNLOAD_FOLDER = "download";
	protected static final char DOT = '.';
	
	private IWApplicationContext iwac = null;
	
	public ICFileWriter(IWApplicationContext iwac) {
		this.iwac = iwac;
	}
		
	public String createContainer(Object file) throws IOException {
		//String mimeType = ((ICFile) file).getMimeType();
		
		long folderIdentifier = System.currentTimeMillis();
		String name = ((ICFile) file).getName();
		String path = getRealPathToFile(name, null, folderIdentifier);
		
		File realFile = new File(path);
		OutputStream destination = null;
		try {
			destination = new BufferedOutputStream(new FileOutputStream(realFile));
			// the exception is quite impossible because getRealPathToFile ensures that the path exists
		} catch (FileNotFoundException e) {
//			log(e);
//			logError("[FileBusiness] Folder could not be found");
			throw new IOException("Folder could not be found");
		}
		try {
			writeData(file, destination);
		}
		finally {
			close(destination);
		}
		return getURLToFile(name, null, folderIdentifier);
	}

  public void close(OutputStream output) {
  	try {
  		if (output != null) {
  			output.close();
  		}
  	}
  	// do not hide an existing exception
  	catch (IOException io) {
  	}
  }

  public String getName(Object file) {
  	return ((ICFile)file).getName();
  }

  public String getRealPathToFile(String fileName, String extension, long folderIdentifier) {
    IWMainApplication mainApp = iwac.getIWMainApplication();
    String separator = FileUtil.getFileSeparator();
    StringBuffer path = new StringBuffer(mainApp.getApplicationRealPath());
    path.append(IWCacheManager.IW_ROOT_CACHE_DIRECTORY)
      .append(separator)
      .append(DOWNLOAD_FOLDER);
    // check if the folder exists create it if necessary
    // usually the folder should be already be there.
    // the folder is never deleted by this class
    String folderPath = path.toString();
    File[] files = FileUtil.getAllFilesInDirectory(folderPath);
    
    if(files!=null){
    	long currentTime = System.currentTimeMillis();
	    for (int i = 0; i < files.length; i++) {
	    	File file = files[i];
	    	long modifiedFile = file.lastModified();
	    	if (currentTime - modifiedFile > 300000)	{
	    		String pathToFile = file.getAbsolutePath();
	    		FileUtil.deleteAllFilesInDirectory(pathToFile);
	    		file.delete();
	    	}
	    }
    }
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
  
  public String getURLToFile(String reportName, String extension, long folderIdentifier) {
    IWMainApplication mainApp = iwac.getIWMainApplication();
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
  
	public void writeData(Object file, OutputStream destination) throws IOException {
		InputStream source = ((ICFile) file).getFileValue();
		try {
			writeFromStreamToStream(source, destination);
		}
		finally {
			close(source);
		}
	}
	
  public void close(InputStream input) {
  	try {
			if (input != null) {
				input.close();
			}
		}
		// do not hide an existing exception
		catch (IOException io) {
		}
  }		
  
	private void writeFromStreamToStream(InputStream source, OutputStream destination) throws IOException { 
		// parts of this method  were copied from "Java in a nutshell" by David Flanagan
    byte[] buffer = new byte[4096];  // A buffer to hold file contents
    int bytesRead;                       
    while((bytesRead = source.read(buffer)) != -1)  {  // Read bytes until EOF
      destination.write(buffer, 0, bytesRead);    
    }
	}
}		

