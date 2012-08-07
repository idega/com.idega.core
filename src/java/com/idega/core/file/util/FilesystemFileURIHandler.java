package com.idega.core.file.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.URI;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import com.idega.util.CoreConstants;

/**
 *
 * @author <a href="civilis@idega.com">Vytautas Čivilis</a>
 * @version $Revision: 1.3 $
 *
 * Last modified: $Date: 2009/01/27 18:17:53 $ by $Author: anton $
 *
 */
@Service
@Scope(BeanDefinition.SCOPE_SINGLETON)
public class FilesystemFileURIHandler implements FileURIHandler {

	private static final String SCHEME = "file";

	@Override
	public String getSupportedScheme() {
		return SCHEME;
	}

	@Override
	public InputStream getFile(URI uri) throws FileNotFoundException {
		FileInputStream is = new FileInputStream(new File(uri));
		return is;
	}

	@Override
	public FileInfo getFileInfo(URI uri) {
		final File f = new File(uri);

		if(!f.exists())
			Logger.getLogger(getClass().getName()).log(Level.WARNING, "File doesn't exist by uri provided="+uri);

		final FileInfo fi = new FileInfo();

		String fileName = f.getName();

		//		replace windows absolute path filename with just filename
		if(fileName.contains(CoreConstants.BACK_SLASH)) {
			int lastBackSlashIndex = fileName.lastIndexOf(CoreConstants.BACK_SLASH);
			fileName = fileName.substring(lastBackSlashIndex + 1);
		}

		fi.setFileName(fileName);
		fi.setContentLength(f.length());

		return fi;
	}
}