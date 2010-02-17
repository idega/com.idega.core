/*
 * $Id: ZipInstaller.java,v 1.1.4.1 2009/06/22 12:06:11 laddi Exp $
 * Created on Dec 6, 2004
 *
 * Copyright (C) 2004 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.io;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import com.idega.util.FileUtil;


/**
 * Extracts zip files and keeps the original timestamps. 
 * E.g. used fo extracting idegaweb archive bundles.
 * 
 *  Last modified: $Date: 2009/06/22 12:06:11 $ by $Author: laddi $
 * 
 * @author <a href="mailto:thomas@idega.com">thomas</a>
 * @version $Revision: 1.1.4.1 $
 */
public class ZipInstaller {
	
	public boolean extract(File zipFile, File destinationFolder) throws IOException {
		ZipInputStream zipInputStream = null;
		HashMap fileNamesTimes = new HashMap();
		try {
			ZipEntry entry = null;
			zipInputStream = new ZipInputStream(new BufferedInputStream(new FileInputStream(zipFile)));
			while ((entry = zipInputStream.getNextEntry()) != null) {
				try {
					OutputStream outputStream = null;
					String fileName = entry.getName();
					long timestamp = entry.getTime();
					File file = FileUtil.createFileRelativeToFile(destinationFolder, fileName);
					if (file.isFile()) {
						try {
							outputStream = getOutputStream(file);
							writeFromStreamToStream(zipInputStream, outputStream);
						}
						finally {
							close(outputStream);
							setTimestamp(file, timestamp);
						}
					}
					else {
						fileNamesTimes.put(file, new Long(timestamp));
					}
				}
				finally {
					closeEntry(zipInputStream);
				}
			}
		}
		catch (FileNotFoundException e) {
			throw new IOException("[ZipInstaller] Zip file could not be found: " + zipFile.getPath());
		}
		finally {
			close(zipInputStream);
		}
		// set the timestamps of the folders (they might be not correct because the files have been extracted and therefore modified the folder)
		Iterator iterator = fileNamesTimes.keySet().iterator();
		while (iterator.hasNext()) {
			File file = (File) iterator.next();
			Long timestamp = (Long) fileNamesTimes.get(file);
			setTimestamp(file, timestamp);
		}
		return true;
	}
	
	private OutputStream getOutputStream(File file) throws FileNotFoundException { 			
		return new BufferedOutputStream(new FileOutputStream(file));
	} 
	
	public void writeFromStreamToStream(InputStream source, OutputStream destination) throws IOException { 
		// parts of this method  were copied from "Java in a nutshell" by David Flanagan
		byte[] buffer = new byte[4096];  // A buffer to hold file contents
		int bytesRead;                       
		while((bytesRead = source.read(buffer)) != -1)  {  // Read bytes until EOF
			destination.write(buffer, 0, bytesRead);    
		}
	}
	
	
	private void setTimestamp(File file, long time) {
		if (file != null) {
			file.setLastModified(time);
		}
	}
	
	private void setTimestamp(File file, Long time) {
		setTimestamp(file, time.longValue());
	}
	
	private void closeEntry(ZipInputStream input) {
	  	try {
	  		if (input != null) {
	  			input.closeEntry();
	  		}
	  	}
	  	catch (IOException io) {
	  	  	// do not hide an existing exception
	  	}
	}
	
	
	 private void close(InputStream input) {
	  	try {
	  		if (input != null) {
	  			input.close();
	  		}
	  	}
	  	catch (IOException io) {
	  	  	// do not hide an existing exception
	  	}
	  }	
	
	 private void close(OutputStream output) {
	  	try {
	  		if (output != null) {
	  			output.close();
	  		}
	  	}
	  	catch (IOException io) {
	  	  	// do not hide an existing exception
	  	}
	  }
}
