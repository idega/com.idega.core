package com.idega.io.serialization;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.activation.MimetypesFileTypeMap;

import com.idega.presentation.IWContext;

/**
 * <p>Title: idegaWeb</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: idega Software</p>
 * @author <a href="thomas@idega.is">Thomas Hilbig</a>
 * @version 1.0
 * Created on Apr 23, 2004
 */
public class FileObjectWriter extends WriterToFile {
	
	public FileObjectWriter(IWContext iwc) {
		super(iwc);
	}
	
	public FileObjectWriter(File file, IWContext iwc) {
		super((Storable) file, iwc);
	}
	
	/* (non-Javadoc)
	 * @see com.idega.io.WriterToFile#createContainer()
	 */
	public String createContainer() throws IOException {
		
		long folderIdentifier = System.currentTimeMillis();
		String name = ((File) this.storable).getName();
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


	public String getName() {
		return ((File) this.storable).getName();
	}

	
	public String getMimeType() {
		MimetypesFileTypeMap map = new MimetypesFileTypeMap();
		String name = ((File) this.storable).getName();
		name = (name == null) ? "" : name;
		return map.getContentType(name);
	}

	
	public OutputStream writeData(OutputStream destination) throws IOException {
		InputStream source = new BufferedInputStream(new FileInputStream((File) this.storable));
		try {
			writeFromStreamToStream(source, destination);
		}
		finally {
			close(source);
		}
		return destination;
	}
}
