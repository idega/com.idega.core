package com.idega.io.serialization;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import com.idega.core.file.data.ICFile;
import com.idega.idegaweb.IWCacheManager;
import com.idega.presentation.IWContext;
import com.idega.util.FileUtil;

/**
 * <p>Title: idegaWeb</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: idega Software</p>
 * @author <a href="thomas@idega.is">Thomas Hilbig</a>
 * @version 1.0
 * Created on Mar 31, 2004
 */

public class ICFileReader extends ReaderFromFile {
	
	public final static String AUXILIARY_FOLDER = "auxiliaryDataFolder";
	public final static String AUXILIARY_FILE = "auxililary_data_file_";

	public ICFileReader(IWContext iwc) {
		super(iwc);
	}
	
	public ICFileReader(Storable storable, IWContext iwc) {
		super(storable, iwc);
	}
	/* (non-Javadoc)
	 * @see com.idega.io.ReaderFromFile#createContainer()
	 */
	public void openContainer(File file) throws IOException {
		readData(new BufferedInputStream(new FileInputStream(file)));
	}

	/* (non-Javadoc)
	 * @see com.idega.io.ReaderFromFile#setName(java.lang.String)
	 */
	public void setName(String name) {
		((ICFile) storable).setName(name);
		((ICFile) storable).store();
	}
	/* (non-Javadoc)
	 * @see com.idega.io.ReaderFromFile#setMimeType(java.lang.String)
	 */
	public void setMimeType(String mimeType) {
		((ICFile) storable).setMimeType(mimeType);
		((ICFile) storable).store();
	}

/* (non-Javadoc)
	 * @see com.idega.io.ReaderFromFile#readData(java.io.InputStream)
	 */
	public InputStream readData(InputStream source) throws IOException {
    String fileId = ((ICFile) storable).getPrimaryKey().toString();
    // To avoid problems with databases (e.g. MySQL) 
    // we do not write directly to the ICFile object but
    // create an auxiliary file on the hard disk and write the xml file to that file.
    // After that we read the file on the hard disk an write it to the ICFile object.
    // Finally we delete the auxiliary file.
    
    // write the output first to a file object  
    // get the output stream      
    String separator = FileUtil.getFileSeparator();
    StringBuffer path = new StringBuffer(iwc.getIWMainApplication().getApplicationRealPath());
           
    path.append(IWCacheManager.IW_ROOT_CACHE_DIRECTORY)
      .append(separator)
      .append(AUXILIARY_FOLDER);
    // check if the folder exists create it if necessary
    // usually the folder should be already be there.
    // the folder is never deleted by this class
    FileUtil.createFolder(path.toString());
    // set name of auxiliary file
    path.append(separator).append(AUXILIARY_FILE).append(fileId);
    BufferedOutputStream outputStream = null;
    File auxiliaryFile = null;
    try {
      auxiliaryFile = new File(path.toString());
      outputStream = new BufferedOutputStream(new FileOutputStream(auxiliaryFile));
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
    // writing finished
    // get size of the file
    int size = (int) auxiliaryFile.length();
    // get the input stream of the auxiliary file
    BufferedInputStream auxInputStream = null;
    try {
      auxInputStream = new BufferedInputStream(new FileInputStream(auxiliaryFile));
        }
    catch (FileNotFoundException ex)  {
      //logError("[XMLData] problem reading file. Message is: "+ex.getMessage());
      throw new IOException("File could not be stored");
    }
    // now we have an input stream of the auxiliary file
    // write to the ICFile object
    ((ICFile) storable).setFileSize(size);
    try {
    	((ICFile) storable).setFileValue(auxInputStream);
    	((ICFile) storable).store();
    }
    finally {
    	close(auxInputStream);
    }
    auxiliaryFile.delete();
    return source;
	}
}
