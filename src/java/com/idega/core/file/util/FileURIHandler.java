package com.idega.core.file.util;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.URI;

/**
 * 
 * @author <a href="civilis@idega.com">Vytautas Čivilis</a>
 * @version $Revision: 1.1 $
 *
 * Last modified: $Date: 2008/06/28 19:03:51 $ by $Author: civilis $
 *
 */
public interface FileURIHandler {
	
	public abstract String getSupportedScheme();
	
	public abstract InputStream getFile(URI uri) throws FileNotFoundException;
	
	public abstract FileInfo getFileInfo(URI uri);
}