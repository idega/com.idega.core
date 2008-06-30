package com.idega.core.file.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.URI;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

/**
 * 
 * @author <a href="civilis@idega.com">Vytautas Čivilis</a>
 * @version $Revision: 1.2 $
 *
 * Last modified: $Date: 2008/06/30 13:34:13 $ by $Author: civilis $
 *
 */
@Service
@Scope("singleton")
public class FilesystemFileURIHandler implements FileURIHandler {

	private static final String SCHEME = "file";
	
	public String getSupportedScheme() {
		return SCHEME;
	}

	public InputStream getFile(URI uri) throws FileNotFoundException {

		//uriStr = URLDecoder.decode(uriStr, "UTF-8");
		FileInputStream is = new FileInputStream(new File(uri));
		return is;
	}

	public FileInfo getFileInfo(URI uri) {
		
		final File f = new File(uri);
		
		if(!f.exists())
			Logger.getLogger(getClass().getName()).log(Level.WARNING, "File doesn't exist by uri provided="+uri);
		
		final FileInfo fi = new FileInfo();
		fi.setFileName(f.getName());
		fi.setContentLength(f.length());
		
		return fi;
	}
}