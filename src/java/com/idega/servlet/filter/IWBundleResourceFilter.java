/*
 * $Id: IWBundleResourceFilter.java,v 1.10 2006/02/22 20:52:48 laddi Exp $
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
import java.io.OutputStream;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.idega.core.file.business.FileIconSupplier;
import com.idega.idegaweb.DefaultIWBundle;
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
 * Last modified: $Date: 2006/02/22 20:52:48 $ by $Author: laddi $
 * 
 * @author <a href="mailto:tryggvil@idega.com">tryggvil</a>
 * @version $Revision: 1.10 $
 */
public class IWBundleResourceFilter extends BaseFilter {

	private boolean feedFromSetBundleDir = false;
	private String sBundlesDirectory;
	static String BUNDLES_STANDARD_DIR = "/idegaweb/bundles/";
	static String BUNDLE_SUFFIX = ".bundle";

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.Filter#init(javax.servlet.FilterConfig)
	 */
	public void init(FilterConfig arg0) throws ServletException {
		String directory = System.getProperty(DefaultIWBundle.SYSTEM_BUNDLES_RESOURCE_DIR);
		if (directory != null) {
			sBundlesDirectory = directory;
			feedFromSetBundleDir = true;
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

		if (feedFromSetBundleDir) {
			try {
				String requestUriWithoutContextPath = getURIMinusContextPath(request);
				String webappDir = getIWMainApplication(request).getApplicationRealPath();
				if (speciallyHandleFile(request, sBundlesDirectory, webappDir, requestUriWithoutContextPath)) {// bundleIdentifier,urlWithinBundle,
																																																				// realFile)){
					chain.doFilter(sreq, sres);
				}
				else {
					File realFile = getFileInWorkspace(sBundlesDirectory, requestUriWithoutContextPath);// bundleDir,urlWithinBundle);
					if (realFile.exists()) {
						feedOutFile(response, realFile);
					}
					else {
						// if the file doesn't exist try to use the default:
						chain.doFilter(sreq, sres);
					}
				}
			}
			catch (Exception e) {
				chain.doFilter(sreq, sres);
			}
		}
		else {
			chain.doFilter(sreq, sres);
		}
	}

	private static String SVG = "svg";
	private static String JSP = "jsp";
	private static String PSVG = "psvg";
	private static String AXIS_JWS = "jws";

	/**
	 * @param realFile
	 */
	// private boolean speciallyHandleFile(HttpServletRequest request,String
	// bundleIdentifier,String filePathInBundle,File file) {
	private boolean speciallyHandleFile(HttpServletRequest request, String workspaceDir, String webappDir, String requestUriWithoutContextPath) {
		String fileEnding = getFileEnding(requestUriWithoutContextPath);

		if (fileEnding.equalsIgnoreCase(PSVG)) {
			copyFileToWebapp(request, workspaceDir, webappDir, requestUriWithoutContextPath);
			return true;
		}
		else if (fileEnding.equalsIgnoreCase(SVG)) {
			copyFileToWebapp(request, workspaceDir, webappDir, requestUriWithoutContextPath);
			return true;
		}
		else if (fileEnding.equalsIgnoreCase(JSP)) {
			copyFileToWebapp(request, workspaceDir, webappDir, requestUriWithoutContextPath);
			return true;
		}
		else if (fileEnding.equalsIgnoreCase(AXIS_JWS)) {
			// do nothing: should be handled by axis:
			return true;
		}
		return false;
	}

	protected void copyFileToWebapp(HttpServletRequest request, String workspaceDir, String webappDir, String requestUriWithoutContextPath) {
		copyWorkspaceFileToWebapp(workspaceDir, webappDir, requestUriWithoutContextPath);
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
	private void feedOutFile(HttpServletResponse response, File realFile) {
		FileInputStream fis;
		try {
			// First set and guess the mime type
			setContentType(response, realFile);

			fis = new FileInputStream(realFile);
			OutputStream out = response.getOutputStream();
			int buffer = 1000;
			byte[] barray = new byte[buffer];
			int read = fis.read(barray);
			// out.write(barray);
			while (read != -1) {
				out.write(barray, 0, read);
				read = fis.read(barray);
			}
			out.flush();
			out.close();
		}
		catch (FileNotFoundException e) {
			String message = e.getMessage();
			if (message == null) {
				message = "";
			}
			System.err.println("IWBundleResourceFilter: " + e.getClass().getName() + "  " + message + " for resource: " + realFile.toString());
		}
		catch (IOException e) {
			String message = e.getMessage();
			if (message == null) {
				message = "";
			}
			System.err.println("IWBundleResourceFilter: " + e.getClass().getName() + " " + message + " for resource: " + realFile.toString());
		}
	}

	protected void setContentType(HttpServletResponse response, File realFile) {
		String mimeType = getMimeType(realFile);
		if (mimeType != null) {
			response.setContentType(mimeType);
		}
	}

	protected String getMimeType(File realFile) {
		String mimeType = FileIconSupplier.getInstance().guessMimeTypeFromFileName(realFile.getName());
		return mimeType;
	}

	protected String[] parseBundleDir(String requestUri) {
		String urlBeginningWithBundle = requestUri.substring(BUNDLES_STANDARD_DIR.length(), requestUri.length());
		String bundleDir = urlBeginningWithBundle.substring(0, urlBeginningWithBundle.indexOf(SLASH));
		String bundleIdentifier = bundleDir;
		if (bundleDir.endsWith(BUNDLE_SUFFIX)) {
			bundleIdentifier = bundleDir.substring(0, bundleDir.indexOf(BUNDLE_SUFFIX));
		}
		// String start = BUNDLES_STANDARD_DIR+bundleDir;
		String restUrl = urlBeginningWithBundle.substring(bundleDir.length(), urlBeginningWithBundle.length());

		String[] theReturn = { bundleIdentifier, restUrl };

		return theReturn;

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
		File jspFileInWorkspace = getFileInWorkspace(workspaceDir, requestUriWithoutContextPath);
		File jspFileInWebapp = new File(webappDir, requestUriWithoutContextPath);
		long webappModified = jspFileInWebapp.lastModified();
		long workspaceLastModified = jspFileInWorkspace.lastModified();
		if (workspaceLastModified > webappModified) {
			try {
				if (!jspFileInWebapp.exists()) {
					FileUtil.createFileIfNotExistent(jspFileInWebapp);
				}
				FileUtil.copyFile(jspFileInWorkspace, jspFileInWebapp);
			}
			catch (FileNotFoundException e) {
				e.printStackTrace();
			}
			catch (IOException e) {
				e.printStackTrace();
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
		String idegawebStandardBundles = "/idegaweb/bundles/";
		if (requestUriWithoutContextPath.startsWith(idegawebStandardBundles)) {
			// cut it from the string as the bundle is directly under the workspace
			// but keep the last slash:
			requestUriWithoutContextPath = requestUriWithoutContextPath.substring(idegawebStandardBundles.length() - 1, requestUriWithoutContextPath.length());
		}
		String sFileInWorkspace = workspaceDir + requestUriWithoutContextPath;
		// String sJspFileInWebapp = webappDir+node.getResourceURI();
		File fileInWorkspace = new File(sFileInWorkspace);
		if (!fileInWorkspace.exists()) {
			// Hack: trying to remove the .bundle suffix if the suffix doesn't exist
			// on the folder in the workspace:
			String bundleSuffix = ".bundle";
			int index = sFileInWorkspace.indexOf(bundleSuffix);
			if (index != -1) {
				sFileInWorkspace = sFileInWorkspace.substring(0, index) + sFileInWorkspace.substring(index + bundleSuffix.length(), sFileInWorkspace.length());
			}
			fileInWorkspace = new File(sFileInWorkspace);
		}
		return fileInWorkspace;
	}
}
