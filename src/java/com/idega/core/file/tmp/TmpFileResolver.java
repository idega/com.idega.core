package com.idega.core.file.tmp;

import java.io.File;
import java.util.Collection;

/**
 * 
 * @author <a href="civilis@idega.com">Vytautas Čivilis</a>
 * @version $Revision: 1.1 $
 *
 * Last modified: $Date: 2008/05/01 15:36:54 $ by $Author: civilis $
 *
 */
public interface TmpFileResolver {

	public abstract Collection<File> resolveFiles(String identifier, Object resource);

	public abstract String getRealBasePath();
	
	public abstract void setRealBasePath(String basePath);
	
	public abstract String getContextPath();

	public abstract Collection<File> getFilesToCleanUp(String identifier, Object resource);
}