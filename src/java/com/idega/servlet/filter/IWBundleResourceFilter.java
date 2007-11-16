/*
 * $Id: IWBundleResourceFilter.java,v 1.33 2007/11/16 02:38:44 eiki Exp $
 * Created on 27.1.2005
 * 
 * Copyright (C) 2005 Idega Software hf. All Rights Reserved.
 * 
 * This software is the proprietary information of Idega hf. Use is subject to
 * license terms.
 */
package com.idega.servlet.filter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.idega.core.file.business.FileIconSupplier;
import com.idega.idegaweb.DefaultIWBundle;
import com.idega.idegaweb.IWBundle;
import com.idega.idegaweb.IWMainApplication;
import com.idega.util.FileUtil;

/**
 * <p>
 * Filter that can feed out resources (images/css etc.) from a set directory for
 * all bundles.<br>
 * This can be set with a System property idegaweb.bundles.resource.dir to to be
 * the directory for the Eclipse workspace when developing. (Setting
 * -Didegaweb.bundles.resource.dir=/idega/eclipse/workspace in the tomcat plugin
 * preference pane).
 * </p>
 * 
 * Last modified: $Date: 2007/11/16 02:38:44 $ by $Author: eiki $
 * 
 * @author <a href="mailto:tryggvil@idega.com">tryggvil</a>
 * @version $Revision: 1.33 $
 */
public class IWBundleResourceFilter extends BaseFilter {

	private static final Logger log = Logger.getLogger(IWBundleResourceFilter.class.getName());
	
	protected boolean feedFromSetBundleDir = false;
	protected boolean feedFromJarFiles = IWMainApplication.loadBundlesFromJars;
	protected String sBundlesDirectory;

	protected List flushedResources = new ArrayList();
	static String BUNDLES_STANDARD_DIR = "/idegaweb/bundles/";
	static String BUNDLE_SUFFIX = DefaultIWBundle.BUNDLE_FOLDER_STANDARD_SUFFIX;
	
	private static String SVG = "svg";
	private static String JSP = "jsp";
	private static String XHTML = "xhtml";
	private static String PSVG = "psvg";
	private static String AXIS_JWS = "jws";

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.Filter#init(javax.servlet.FilterConfig)
	 */
	public void init(FilterConfig arg0) throws ServletException {
		String directory = System.getProperty(DefaultIWBundle.SYSTEM_BUNDLES_RESOURCE_DIR);
		if (directory != null) {
			this.sBundlesDirectory = directory;
			this.feedFromSetBundleDir = true;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.Filter#doFilter(javax.servlet.ServletRequest,
	 *      javax.servlet.ServletResponse, javax.servlet.FilterChain)
	 */
	public void doFilter(ServletRequest sreq, ServletResponse sres, FilterChain chain) throws IOException, ServletException {

		HttpServletRequest request = (HttpServletRequest) sreq;
		HttpServletResponse response = (HttpServletResponse) sres;
		String requestUriWithoutContextPath = getURIMinusContextPath(request);
		
		
		if(!flushedResources.contains(requestUriWithoutContextPath)){
			IWMainApplication iwma = getIWMainApplication(request);
			String webappDir = iwma.getApplicationRealPath();
			
			if (this.feedFromSetBundleDir) {
				try {
					if (!speciallyHandleFile(request, this.sBundlesDirectory, webappDir, requestUriWithoutContextPath)) {
						
						File realFile = getFileInWorkspace(this.sBundlesDirectory, requestUriWithoutContextPath);
						if (realFile.exists()) {
							feedOutFile(request,response, realFile);
							return;
						}
					}
				}
				catch (Exception e) {
					log.log(Level.WARNING, "Error serving file from workspace", e);
				}
			}
			else if (feedFromJarFiles) {
				if (requestUriWithoutContextPath.startsWith(BUNDLES_STANDARD_DIR)) {
					//check if we have flushed the file from the jar before and then do nothing OR flush it and then do nothing
					//THIS IS VERY SIMPLE CACHING that invalidates on restart
					try {
						copyResourceFromJarToWebapp(iwma,requestUriWithoutContextPath);
						flushedResources.add(requestUriWithoutContextPath);
						//old way without flushing to webapp
						//String mimeType = getMimeType(pathWithinBundle);
						//feedOutFile(request, response, mimeType, stream);
						//return;
						
					}catch (Exception e) {
						log.log(Level.WARNING, "Error serving file from jar : "+ requestUriWithoutContextPath, e);
					}
				}
			}
		}
		
		// if file is specially handled, flushed from the jar file or any error occurs, then let the server keep on going with it
		chain.doFilter(sreq, sres);
	}

	protected static String getBundleFromRequest(String requestUriWithoutContextPath) {
		int index = requestUriWithoutContextPath.indexOf(BUNDLE_SUFFIX);
		return requestUriWithoutContextPath.substring(BUNDLES_STANDARD_DIR.length(), index);
	}

	protected static String getResourceWithinBundle(String requestUriWithoutContextPath) {
		String rest = null;
		int index = requestUriWithoutContextPath.indexOf(BUNDLE_SUFFIX);
		if(index!=-1){
			rest = requestUriWithoutContextPath.substring(index+BUNDLE_SUFFIX.length()+1);
		}
		else{
			String URIWithoutBundlesURI = requestUriWithoutContextPath.substring(BUNDLES_STANDARD_DIR.length()+1);
			index = URIWithoutBundlesURI.indexOf("/");
			rest = URIWithoutBundlesURI.substring(index);
		}
		
		return rest;
	}
	
	/**
	 * <p>
	 * Copies a resource from within a Jar File into the webapp folder if it doesn't
	 * already exists
	 * </p>
	 * @param iwma
	 * @param requestUriWithoutContextPath
	 */
	public synchronized static void copyResourceFromJarToWebapp(IWMainApplication iwma,String requestUriWithoutContextPath){
		
		String bundleIdentifier = getBundleFromRequest(requestUriWithoutContextPath);
		String pathWithinBundle = getResourceWithinBundle(requestUriWithoutContextPath);
		String webappFilePath = iwma.getApplicationRealPath() + requestUriWithoutContextPath;
		File webappFile = new File(webappFilePath);
		IWBundle bundle = iwma.getBundle(bundleIdentifier);
		
//		long bundleLastModified = bundle.getResourceTime(pathWithinBundle);
//		if (webappFile.exists()) {
//			long webappLastModified = webappFile.lastModified();
//			if (webappLastModified > bundleLastModified) {
//				return;
//			}
//		}
		
		try {
			webappFile = FileUtil.getFileAndCreateRecursiveIfNotExists(webappFilePath);
			InputStream input = bundle.getResourceInputStream(pathWithinBundle);
			FileUtil.streamToFile(input, webappFile);
//			webappFile.setLastModified(bundleLastModified);
		}
		catch (IOException e) {
			log.log(Level.WARNING, "Could not copy resource from jar to " + requestUriWithoutContextPath, e);
		}
	}
	

	/**
	 * @param realFile
	 */
	// private boolean d(HttpServletRequest request,String
	// bundleIdentifier,String filePathInBundle,File file) {
	private boolean speciallyHandleFile(HttpServletRequest request, String workspaceDir, String webappDir, String requestUriWithoutContextPath) {
		String fileEnding = getFileEnding(requestUriWithoutContextPath);
		
		if (fileEnding.equalsIgnoreCase(PSVG)) {
			copyWorkspaceFileToWebapp(workspaceDir, webappDir, requestUriWithoutContextPath);
			return true;
		}
		else if (fileEnding.equalsIgnoreCase(SVG)) {
			copyWorkspaceFileToWebapp(workspaceDir, webappDir, requestUriWithoutContextPath);
			return true;
		}
		else if (fileEnding.equalsIgnoreCase(JSP)) {
			copyWorkspaceFileToWebapp(workspaceDir, webappDir, requestUriWithoutContextPath);
			return true;
		}
		else if (fileEnding.equalsIgnoreCase(XHTML)) {
			copyWorkspaceFileToWebapp(workspaceDir, webappDir, requestUriWithoutContextPath);
			return true;
		}
		else if (fileEnding.equalsIgnoreCase(AXIS_JWS)) {
			// do nothing: should be handled by axis:
			return true;
		}
		return false;
	}

	private String getFileEnding(String filePath) {
		// String fileName = realFile.getName();
		String fileName = filePath;
		int index = fileName.lastIndexOf(".");
		if (index != -1) {
			return fileName.substring(index + 1, fileName.length());
		}
		return null;
	}

	
	/**
	 * @param response
	 * @param realFile
	 */
	private void feedOutFile(HttpServletRequest request, HttpServletResponse response, File realFile) {

		try {
			FileInputStream fis = new FileInputStream(realFile);
			String mimeType = getMimeType(realFile);
			feedOutFile(request, response, mimeType,fis);
		}
		catch (FileNotFoundException e) {
			log.warning("File not found: " + realFile.getPath());
		}
	}
	
	/**
	 * @param response
	 * @param realFile
	 */
	private void feedOutFile(HttpServletRequest request, HttpServletResponse response,String mimeType, InputStream streamToResource) {

		try {
			if (mimeType != null) {
				response.setContentType(mimeType);
			}
			OutputStream out = response.getOutputStream();
			int buffer = 1000;
			byte[] barray = new byte[buffer];
			int read = streamToResource.read(barray);
			// out.write(barray);
			while (read != -1) {
				out.write(barray, 0, read);
				read = streamToResource.read(barray);
			}
			streamToResource.close();
			out.flush();
			out.close();
		}
		catch (IOException e) {
			log.warning("Error streaming resource to " + request.getRequestURI());
		}
	}

	
	protected String getMimeType(String filePath){
		String mimeType = FileIconSupplier.getInstance().guessMimeTypeFromFileName(filePath);
		return mimeType;
	}

	protected String getMimeType(File realFile) {
		String mimeType = getMimeType(realFile.getName());
		return mimeType;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.Filter#destroy()
	 */
	public void destroy() {
		// TODO Auto-generated method stub
	}

	/**
	 * <p>
	 * Copies a file to a the real webapplication folder from the (eclipse)
	 * workspace if the lastmodified timestamp is more recent on the file in the
	 * workspace.
	 * </p>
	 * 
	 * @param workspaceDir
	 *          Something like '/home/tryggvil/eclipseworkspace/'
	 * @param webappDir
	 *          Something like
	 *          '/home/tryggvil/eclipseworkspace/applications/mywebapp/target/mywebapp/'
	 * @param requestUriWithoutContextPath
	 *          Something like
	 *          '/idegaweb/bundles/com.idega.core.bundle/jsp/myjsp.jsp'
	 */
	public static void copyWorkspaceFileToWebapp(String workspaceDir, String webappDir, String requestUriWithoutContextPath) {

		if (webappDir.endsWith(File.separator)) {
			// cut the slash:
			webappDir = webappDir.substring(0, webappDir.length() - 1);
		}
		if (workspaceDir.endsWith(File.separator)) {
			// cut the slash:
			workspaceDir = workspaceDir.substring(0, workspaceDir.length() - 1);
		}
		File fileInWorkspace = getFileInWorkspace(workspaceDir, requestUriWithoutContextPath);
		File fileInWebapp = new File(webappDir, requestUriWithoutContextPath);
		long webappModified = fileInWebapp.lastModified();
		long workspaceLastModified = fileInWorkspace.lastModified();
		if (workspaceLastModified > webappModified) {
			try {
				if (!fileInWebapp.exists()) {
					FileUtil.createFileIfNotExistent(fileInWebapp);
				}
				FileUtil.copyFile(fileInWorkspace, fileInWebapp);
				fileInWebapp.setLastModified(workspaceLastModified);
			}
			catch (FileNotFoundException e) {
				log.warning("File not found: " + fileInWorkspace.getPath());
			}
			catch (IOException e) {
				log.warning("Error copying file: " + fileInWorkspace.getPath());
			}
		}
	}

	/**
	 * <p>
	 * Gets the file or tries to guess to its location inside in the 'workspace'
	 * out from a requestUri.
	 * </p>
	 * 
	 * @param workspaceDir
	 * @param requestUriWithoutContextPath
	 * @return
	 */
	public static File getFileInWorkspace(String workspaceDir, String requestUriWithoutContextPath) {
		if (requestUriWithoutContextPath.startsWith(BUNDLES_STANDARD_DIR)) {
			// cut it from the string as the bundle is directly under the workspace
			// but keep the last slash:
			requestUriWithoutContextPath = requestUriWithoutContextPath.substring(BUNDLES_STANDARD_DIR.length() - 1);
		}
		String sFileInWorkspace = workspaceDir + requestUriWithoutContextPath;
		File fileInWorkspace = new File(sFileInWorkspace);
		if (!fileInWorkspace.exists()) {
			// Hack: trying to remove the .bundle suffix if the suffix doesn't exist
			// on the folder in the workspace:
			int index = sFileInWorkspace.indexOf(BUNDLE_SUFFIX);
			if (index != -1) {
				sFileInWorkspace = sFileInWorkspace.substring(0, index) + sFileInWorkspace.substring(index + BUNDLE_SUFFIX.length());
			}
			fileInWorkspace = new File(sFileInWorkspace);
		}
		return fileInWorkspace;
	}
}
