package com.idega.io.serialization;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.idega.core.file.data.ICFile;
import com.idega.presentation.IWContext;

/**
 * <p>Title: idegaWeb</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: idega Software</p>
 * @author <a href="thomas@idega.is">Thomas Hilbig</a>
 * @version 1.0
 * Created on Mar 18, 2004
 */
public class ICFileWriter extends WriterToFile {
	
	public ICFileWriter(IWContext iwc) {
		super(iwc);
	}
	
	public ICFileWriter(ICFile file, IWContext iwc) {
		super((Storable) file, iwc);
	}
	
	public String createContainer() throws IOException {

		
		long folderIdentifier = System.currentTimeMillis();
		String name = ((ICFile) storable).getName();
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
			writeData(destination);
		}
		finally {
			close(destination);
		}
		return getURLToFile(name, null, folderIdentifier);
	}

	public String getMimeType() {
		return ((ICFile) storable).getMimeType();
	}
		
	public boolean isMarkedAsDeleted() {
		return ((ICFile) storable).getDeleted();
	}
	
	
  public String getName() {
  	return ((ICFile) storable).getName();
  }

	public OutputStream writeData(OutputStream destination) throws IOException {
		InputStream source = ((ICFile) storable).getFileValue();
		try {
			writeFromStreamToStream(source, destination);
		}
		finally {
			close(source);
		}
		return destination;
	}
  
}		

