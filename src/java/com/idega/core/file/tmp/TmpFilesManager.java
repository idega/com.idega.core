package com.idega.core.file.tmp;

import java.io.File;
import java.io.InputStream;
import java.net.URI;
import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import com.idega.idegaweb.IWMainApplication;
import com.idega.util.CoreConstants;
import com.idega.util.EventTimer;
import com.idega.util.FileUtil;

/**
 * @author <a href="mailto:arunas@idega.com">Arūnas Vasmanas</a>
 * @version $Revision: 1.2 $
 *
 * Last modified: $Date: 2008/06/28 19:03:01 $ by $Author: civilis $
 */
@Scope("singleton")
@Service
public class TmpFilesManager {
	
    static String UPLOADS_PATH;
    private Collection<TmpFileResolver> resResolvers;
    
    public void init(Collection<TmpFileResolver> resResolvers) {
    	
    	IWMainApplication iwma = IWMainApplication.getDefaultIWMainApplication();
    	String realPath = iwma.getApplicationRealPath();
    	
    	UPLOADS_PATH = realPath + "tmpFiles/";
    	FileUtil.createFolder(UPLOADS_PATH);
    	
    	if(resResolvers != null) {
    		
    		for (TmpFileResolver resResolver : resResolvers) {
				
    			String ctxFolder = resResolver.getContextPath();
    			if(ctxFolder.startsWith(CoreConstants.SLASH))
    				ctxFolder = ctxFolder.substring(1);
    			
    			String custFolder = UPLOADS_PATH+ctxFolder;
    			FileUtil.createFolder(custFolder);
    			resResolver.setRealBasePath(custFolder);
			}
    	}
    	
    	this.resResolvers = resResolvers;
    }
    
    public void uploadToTmpDir(String pathDirInCtx, String fileName, InputStream input, TmpFileResolver resolver) {
    	
    	if(fileName == null || CoreConstants.EMPTY.equals(fileName)) {
    		Logger.getLogger(getClass().getName()).log(Level.WARNING, "Tried to upload file to tmp dir, but no fileName provided");
    		return;
    	}
    	
    	String basePath = resolver.getRealBasePath();
    	
    	if(!basePath.endsWith(CoreConstants.SLASH))
    		basePath = basePath+CoreConstants.SLASH;
    	
    	if(pathDirInCtx != null) {
    		
    		if(pathDirInCtx.startsWith(CoreConstants.SLASH))
    			pathDirInCtx = pathDirInCtx.substring(1);
    		
    		basePath += pathDirInCtx;
    	}
    	
    	FileUtil.streamToFile(input, basePath, fileName);
    }
    
    public void cleanup(String identifier, Object resource, TmpFileResolver resolver) {
    	
    	Collection<File> filesToClean = resolver.getFilesToCleanUp(identifier, resource);
    	
    	for (File f : filesToClean) {
			
    		if(f.exists()) {
    			
    			if(f.isDirectory()) {
        			FileUtil.deleteNotEmptyDirectory(f);
        		} else {
        			FileUtil.delete(f);
        		}
    		}
		}
    }
    
    public void doPeriodicalCleanup() {
    	
    	if(resResolvers != null) {
    		
    		for (TmpFileResolver resResolver : resResolvers) {
				
    			FileUtil.deleteAllFilesAndFolderInFolderOlderThan(resResolver.getRealBasePath(), EventTimer.THREAD_SLEEP_24_HOURS);
			}
    	}
    }
    
    /**
     * 
     * @param identifier - might be some specific identifier, that you want to get files for, e.g., variable in xforms submission data. Highly resolver dependent.
     * @param resource - the same as with identifier
     * @param resolver
     * @return files uris. Might be local files, or stored in slide etc.
     */
    public Collection<URI> getFilesUris(String identifier, Object resource, TmpFileResolver resolver) {
    	
    	return resolver.resolveFilesUris(identifier, resource);
    }
}