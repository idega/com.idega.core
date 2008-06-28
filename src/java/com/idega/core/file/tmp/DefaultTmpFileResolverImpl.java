package com.idega.core.file.tmp;

import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import com.idega.util.CoreConstants;
import com.idega.util.FileUtil;


/**
 * 
 * @author <a href="civilis@idega.com">Vytautas Čivilis</a>
 * @version $Revision: 1.2 $
 *
 * Last modified: $Date: 2008/06/28 19:02:23 $ by $Author: civilis $
 *
 */
@Scope("singleton")
@TmpFileResolverType("defaultResolver")
@Service("defUplResResolver")
public class DefaultTmpFileResolverImpl implements TmpFileResolver {

	public static String UPLOADS_PATH;

	public Collection<URI> resolveFilesUris(String identifier, Object resource) {
		
		String realBasePath = getRealBasePath();
		
		String filesFolder = concatFolderWithFolder(realBasePath, identifier);
		
		File[] files = FileUtil.getAllFilesInDirectory(filesFolder);
		
		if(files != null) {
		
			ArrayList<URI> uris = new ArrayList<URI>(files.length);
			
			for (File file : files) {
				
				URI uri = file.toURI();
				uris.add(uri);
			}
			
			return uris;
		}
		
		return new ArrayList<URI>(0);
	}
	
	public String getRealBasePath() {
		
		return UPLOADS_PATH;
	}
	
	public void setRealBasePath(String basePath) {
		
		UPLOADS_PATH = basePath;
	}
	
	public String getContextPath() {
		
		return "defaultFiles/";
	}
	
	private String concatFolderWithFolder(String fol1, String fol2) {
		
		if(fol2.startsWith(CoreConstants.SLASH))
			fol2 = fol2.substring(1);
		
		if(!fol1.endsWith(CoreConstants.SLASH))
			fol1 += CoreConstants.SLASH;
		
		return fol1+fol2;
	}
	
	public Collection<File> getFilesToCleanUp(String identifier, Object resource) {
		
		String realBasePath = getRealBasePath();
		
		String filesFolder = concatFolderWithFolder(realBasePath, identifier);
		
		ArrayList<File> fs = new ArrayList<File>(1);
		fs.add(new File(filesFolder));
		
		return fs;
	}

	public Collection<URI> resolveAllFilesUris(Object resource) {
		
		throw new UnsupportedOperationException("Not supported yet");
	}

	public void replaceAllFiles(Object resource,
			TmpFilesModifyStrategy replaceStrategy) {
		
		throw new UnsupportedOperationException("Not supported");
	}
}