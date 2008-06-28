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

import com.idega.util.CoreConstants;

/**
 * 
 * @author <a href="civilis@idega.com">Vytautas Čivilis</a>
 * @version $Revision: 1.1 $
 *
 * Last modified: $Date: 2008/06/28 19:03:51 $ by $Author: civilis $
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

	public String getFileName(URI uri) {
		
		final String path = uri.getPath();
		final String fileName;
		
		if(path.contains(CoreConstants.SLASH)) {
			
			fileName = path.substring(path.lastIndexOf(CoreConstants.SLASH)+1);
		} else {
			fileName = path;
		}
		
		return fileName;
	}

	public Long getContentLength(URI uri) {
		
		File f = new File(uri);
		return f.length();
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