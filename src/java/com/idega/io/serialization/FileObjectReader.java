package com.idega.io.serialization;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import com.idega.idegaweb.IWMainApplication;
import com.idega.io.UploadFile;
import com.idega.presentation.IWContext;
import com.idega.util.FileUtil;
import com.idega.util.StringHandler;

/**
 * <p>Title: idegaWeb</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: idega Software</p>
 * @author <a href="thomas@idega.is">Thomas Hilbig</a>
 * @version 1.0
 * Created on Apr 23, 2004
 */
public class FileObjectReader extends ReaderFromFile implements StorableProvider {
	
	static final String IMPORT_FOLDER = "import";
	static final String DOT = ".";
	static final String SLASH = "/";
	static final String UNDERSCORE = "_";
	
	public FileObjectReader() {
	}
	
	public FileObjectReader(IWContext iwc) {
		super(iwc);
	}
	
	public FileObjectReader(Storable storable, IWContext iwc) {
		super(storable, iwc);
	}
	
	/* (non-Javadoc)
	 * @see com.idega.io.ReaderFromFile#openContainer(java.io.File)
	 */
	public void openContainer(File file) throws IOException {
		// TODO Auto-generated method stub
	}
	/* (non-Javadoc)
	 * @see com.idega.io.ReaderFromFile#setName(java.lang.String)
	 */
	public void setName(String name) {
		// TODO Auto-generated method stub
	}
	/* (non-Javadoc)
	 * @see com.idega.io.ReaderFromFile#setMimeType(java.lang.String)
	 */
	public void setMimeType(String mimeType) {
		// TODO Auto-generated method stub
	}
	/* (non-Javadoc)
	 * @see com.idega.io.ReaderFromFile#readData(java.io.InputStream)
	 */
	public InputStream readData(InputStream source) throws IOException {
		BufferedOutputStream outputStream = null;
    try {
      outputStream = new BufferedOutputStream(new FileOutputStream((File) this.storable));
    }
    catch (FileNotFoundException ex)  {
    	//logError("FileBusiness] problem creating file. Message is: "+ex.getMessage());
      throw new IOException("File could not be stored");
    }
    // now we have an output stream of the auxiliary file
    // write to the file
    try {
    	writeFromStreamToStream(source, outputStream);
    }
    finally {
    	close(outputStream);
    }
		return null;
	}
	

	
//  protected String getRealPathToFile(String fileName) {
//    IWMainApplication mainApp = iwc.getIWMainApplication();
//    String separator = FileUtil.getFileSeparator();
//    StringBuffer path = new StringBuffer(mainApp.getApplicationRealPath());
//    path.append(IMPORT_FOLDER);
//    // check if the folder exists create it if necessary
//    // usually the folder should be already be there.
//    // the folder is never deleted by this class
//    // check if there is already a file with the same name
//    String folderPath = path.toString();
//    File[] files = FileUtil.getAllFilesInDirectory(folderPath);
//    List existingFileNames = new ArrayList();
//    boolean conflictOfNames = false;
//    if(files!=null){
//	    for (int i = 0; i < files.length; i++) {
//	    	File file = files[i];
//	    	String name  = file.getName();
//	    	String nameWithoutExtension = StringHandler.cutExtension(fileName);
//	    	existingFileNames.add(nameWithoutExtension);
//	    	if (fileName.equals(name)) {
//	    		conflictOfNames = true;
//	    	}
//	    }
//	    if (conflictOfNames == true) {
//	    	String fileNameWithoutExtension = StringHandler.cutExtension(fileName);
//	    	String changedFileName = StringHandler.addOrIncreaseCounterIfNecessary(fileNameWithoutExtension, DOT, existingFileNames);
//	    	fileName = StringHandler.replaceNameKeepExtension(fileName, changedFileName);
//	    }
//    }
//		folderPath = path.toString();
//		FileUtil.createFolder(folderPath);
//		path.append(separator).append(fileName);
//    return path.toString();
//  }

	/* (non-Javadoc)
	 * @see com.idega.io.StorableProvider#getSource(java.lang.String)
	 */
	public Storable getSource(String value, String sourceClassName, IWContext iwc) throws IOException {
		IWMainApplication mainApp = iwc.getIWMainApplication();
		String separator = FileUtil.getFileSeparator();
    String path = mainApp.getApplicationRealPath();
    path = StringHandler.replace(path, separator, SLASH);
    if (value.startsWith(SLASH)) {
    	value = value.substring(1);
    }
    path = StringHandler.concat(path,value);
    // we just need a file wrapper that implements IBStorable
    // be careful: UploadFile overides getName() therefore set the name explicitly
    int indexOfLastSeparator = path.lastIndexOf(separator);
    String fileName = (indexOfLastSeparator == -1) ? path : path.substring(++indexOfLastSeparator);
		UploadFile file = new UploadFile(fileName,path,null,null,0);
		if (! file.exists()) {
			throw new IOException("[FileObjectReader] File couldn't be found. Used path: "+path);
		}
		return file;
	}

	/* (non-Javadoc)
	 * @see com.idega.io.StorableProvider#createSource(java.lang.String)
	 */
	public StorableHolder createSource(String value, String sourceClassName, IWContext iwc) throws IOException {
		// get file name 
		int lastPathSeparator = value.lastIndexOf(SLASH);
		String fileName = value.substring(++lastPathSeparator); 
    IWMainApplication mainApp = iwc.getIWMainApplication();
    String separator = FileUtil.getFileSeparator();
    StringBuffer realPath = new StringBuffer(mainApp.getApplicationRealPath());
    realPath.append(IMPORT_FOLDER);
    // check if the folder exists create it if necessary
    // usually the folder should be already be there.
    // the folder is never deleted by this class
    // check if there is already a file with the same name
    String folderPath = realPath.toString();
    File[] files = FileUtil.getAllFilesInDirectory(folderPath);
    List existingFileNames = new ArrayList();
    boolean conflictOfNames = false;
    if(files!=null){
	    for (int i = 0; i < files.length; i++) {
	    	File file = files[i];
	    	String name  = file.getName();
	    	String nameWithoutExtension = StringHandler.cutExtension(name);
	    	existingFileNames.add(nameWithoutExtension);
	    	if (fileName.equals(name)) {
	    		conflictOfNames = true;
	    	}
	    }
	    if (conflictOfNames == true) {
	    	String fileNameWithoutExtension = StringHandler.cutExtension(fileName);
	    	String changedFileName = StringHandler.addOrIncreaseCounterIfNecessary(fileNameWithoutExtension, UNDERSCORE, existingFileNames);
	    	fileName = StringHandler.replaceNameKeepExtension(fileName, changedFileName);
	    }
    }
		FileUtil.createFolder(folderPath);
		realPath.append(separator).append(fileName);
   // we just need a file wrapper that implements IBStorable
		UploadFile file = new UploadFile(fileName,realPath.toString(),null,null,0);
		((File) file).createNewFile();
		StorableHolder holder = new StorableHolder();
		holder.setStorable(file);
		// construct new value
		StringBuffer newValue = new StringBuffer(SLASH);
		newValue.append(IMPORT_FOLDER).append(SLASH).append(fileName);
		holder.setValue(newValue.toString());
		return holder;
	}


	
}
