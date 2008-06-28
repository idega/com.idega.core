package com.idega.core.file.tmp;

import java.io.File;
import java.net.URI;
import java.util.Collection;

/**
 * 
 * @author <a href="civilis@idega.com">Vytautas Čivilis</a>
 * @version $Revision: 1.2 $
 *
 * Last modified: $Date: 2008/06/28 19:02:39 $ by $Author: civilis $
 *
 */
public interface TmpFileResolver {

	public abstract Collection<URI> resolveFilesUris(String identifier, Object resource);
	
	public abstract void replaceAllFiles(Object resource, TmpFilesModifyStrategy replaceStrategy);
	
	public abstract String getRealBasePath();
	
	public abstract void setRealBasePath(String basePath);
	
	public abstract String getContextPath();

	public abstract Collection<File> getFilesToCleanUp(String identifier, Object resource);
}