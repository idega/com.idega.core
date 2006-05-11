/*
 * Created on Dec 11, 2004
 * 
 * TODO To change the template for this generated file go to Window -
 * Preferences - Java - Code Style - Code Templates
 */
package com.idega.util;

import java.io.File;
import java.io.IOException;
import java.util.StringTokenizer;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import org.apache.commons.fileupload.FileItem;
import org.apache.myfaces.webapp.filter.MultipartRequestWrapper;
import com.idega.idegaweb.IWCacheManager;
import com.idega.io.UploadFile;
import com.idega.presentation.IWContext;
import com.idega.presentation.ui.FileInput;

public class FileUploadUtil {

	public FileUploadUtil() {
		super();
	}
	
	/**
	 * A backward compatability method for the "old" UploadFile support in
	 * IWContext. Only used when JSF rendering is turned on
	 * 
	 * @param iwc
	 * @throws IOException
	 * @throws IllegalAccessException
	 * @throws NoSuchFieldException
	 * @throws IllegalArgumentException
	 */
	public static void handleMyFacesMultiPartRequest(IWContext iwc) throws IOException, IllegalArgumentException,IllegalAccessException, NoSuchFieldException {
		
		HttpServletRequest request = iwc.getRequest();
		
		if (request instanceof HttpServletRequestWrapper) {
			
			HttpServletRequest childRequest = request;
			while( childRequest instanceof HttpServletRequestWrapper){
				
				if(childRequest instanceof MultipartRequestWrapper){
//					myfaces This ONLY supports one file now
					//Cast the request to a MultipartRequestWrapper
					MultipartRequestWrapper multiRequestWrapper = (MultipartRequestWrapper) childRequest;
					//get the uploaded file
					StringBuffer pathToFile = new StringBuffer();
					pathToFile.append(iwc.getIWMainApplication().getApplicationRealPath());
					pathToFile.append(IWCacheManager.IW_ROOT_CACHE_DIRECTORY);
					pathToFile.append(FileUtil.getFileSeparator());
					pathToFile.append("upload");
					pathToFile.append(FileUtil.getFileSeparator());
					FileUtil.createFolder(pathToFile.toString());

					FileItem file = multiRequestWrapper.getFileItem(FileInput.FILE_INPUT_DEFAULT_PARAMETER_NAME);
					String fileName = file.getName();
					int lastBloodySlash = fileName.lastIndexOf("\\");
					if(lastBloodySlash>-1){
						fileName = fileName.substring(lastBloodySlash+1);
					}
					
					String mimeType = file.getContentType();
					
					StringBuffer webPath = new StringBuffer();
					webPath.append('/');
					webPath.append(IWCacheManager.IW_ROOT_CACHE_DIRECTORY);
					webPath.append('/');
					webPath.append("upload");
					webPath.append('/');
					webPath.append(fileName);
					// Opera mimetype fix ( aron@idega.is )
					if (mimeType != null) {
						StringTokenizer tokenizer = new StringTokenizer(mimeType, " ;:");
						if (tokenizer.hasMoreTokens()) {
							mimeType = tokenizer.nextToken();
						}
					}
					//write the file from wherever it is to our favorite upload
					// folder
					File tempFile = FileUtil.streamToFile(file.getInputStream(), pathToFile.toString(), fileName);
					
					String filePath = pathToFile.toString()+fileName;
					UploadFile uploadFile = new UploadFile(fileName, filePath,
							iwc.getIWMainApplication().getTranslatedURIWithContext(webPath.toString()), mimeType,
							tempFile.length());
					iwc.setUploadedFile(uploadFile);
					//we can only handle one here
					break;
				}
					
				childRequest = (HttpServletRequest) ((HttpServletRequestWrapper)childRequest).getRequest();
			}	
		}
	}
}