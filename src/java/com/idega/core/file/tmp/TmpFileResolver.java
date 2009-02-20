package com.idega.core.file.tmp;

import java.io.File;
import java.io.InputStream;
import java.net.URI;
import java.util.Collection;

/**
 * 
 * @author <a href="civilis@idega.com">Vytautas Čivilis</a>
 * @version $Revision: 1.3 $
 * 
 *          Last modified: $Date: 2009/02/20 14:26:23 $ by $Author: civilis $
 * 
 */
public interface TmpFileResolver {

	public abstract Collection<URI> resolveFilesUris(String identifier,
			Object resource);

	public abstract void replaceAllFiles(Object resource,
			TmpFilesModifyStrategy replaceStrategy);

	public abstract String getRealBasePath();

	public abstract void setRealBasePath(String basePath);

	public abstract String getContextPath();

	public abstract Collection<File> getFilesToCleanUp(String identifier,
			Object resource);

	public abstract void uploadToTmpLocation(String pathDirRelativeToBase,
			String fileName, InputStream inputStream);

	public abstract String getTmpUploadDir(String pathDirRelativeToBase);
	
	public abstract File getFile(String pathDirRelativeToBase,
			String fileName);
}