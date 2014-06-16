/*
 * $Id: IWBundleResourceFilter.java,v 1.56 2009/03/11 08:47:50 civilis Exp $
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
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.jar.JarInputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;

import javax.faces.context.FacesContext;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.idega.core.file.util.MimeTypeUtil;
import com.idega.idegaweb.DefaultIWBundle;
import com.idega.idegaweb.IWBundle;
import com.idega.idegaweb.IWMainApplication;
import com.idega.idegaweb.IWModuleLoader;
import com.idega.util.CoreConstants;
import com.idega.util.CoreUtil;
import com.idega.util.FileUtil;
import com.idega.util.IOUtil;
import com.idega.util.ListUtil;
import com.idega.util.RequestUtil;
import com.idega.util.StringHandler;
import com.idega.util.StringUtil;
import com.idega.util.resources.ResourcesAdder;

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
 * Last modified: $Date: 2009/03/11 08:47:50 $ by $Author: civilis $
 *
 * @author <a href="mailto:tryggvil@idega.com">tryggvil</a>
 * @version $Revision: 1.56 $
 */
public class IWBundleResourceFilter extends BaseFilter {

	private static final Logger LOGGER = Logger.getLogger(IWBundleResourceFilter.class.getName());

	protected boolean feedFromSetBundleDir = false;
	protected boolean feedFromJarFiles = IWMainApplication.loadBundlesFromJars;

	protected String sBundlesDirectory;

	protected Map<String, Boolean> flushedResources = new HashMap<String, Boolean>();

	public static String BUNDLES_STANDARD_DIR = "/idegaweb/bundles/";
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
	@Override
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
	@Override
	public void doFilter(ServletRequest sreq, ServletResponse sres, FilterChain chain) throws IOException, ServletException {
		HttpServletRequest request = (HttpServletRequest) sreq;
		HttpServletResponse response = (HttpServletResponse) sres;
		String requestUriWithoutContextPath = getURIMinusContextPath(request);

		/* Removing ;jessionid before checking in cache */
		if (requestUriWithoutContextPath.contains(CoreConstants.SEMICOLON)) {
			requestUriWithoutContextPath = requestUriWithoutContextPath.substring(
					0, requestUriWithoutContextPath.indexOf(CoreConstants.SEMICOLON));
		}

		if (!flushedResources.containsKey(requestUriWithoutContextPath)) {
			IWMainApplication iwma = getIWMainApplication(request);
			String webappDir = iwma.getApplicationRealPath();

			boolean fileExists = false;

			if (this.feedFromSetBundleDir) {
				try {
					if (!speciallyHandleFile(request, this.sBundlesDirectory, webappDir, requestUriWithoutContextPath)) {

						File realFile = getFileInWorkspace(this.sBundlesDirectory, requestUriWithoutContextPath);
						if (realFile.exists()) {
							feedOutFile(request,response, realFile);
							return;
						}
					}
				} catch (Exception e) {
					LOGGER.log(Level.WARNING, "Error serving file from workspace", e);
				}
			}
			if (feedFromJarFiles || !fileExists) {

				if (requestUriWithoutContextPath.startsWith(BUNDLES_STANDARD_DIR)) {
					//check if we have flushed the file from the jar before and then do nothing OR flush it and then do nothing
					//THIS IS VERY SIMPLE CACHING that invalidates on restart
					try {
						File realFile = copyResourceFromJarOrCustomContentToWebapp(iwma, requestUriWithoutContextPath, null, true);
						//old way without flushing to webapp
						//String mimeType = getMimeType(pathWithinBundle);
						//feedOutFile(request, response, mimeType, stream);
						if (realFile == null) {
							LOGGER.warning("File does not exist at " + requestUriWithoutContextPath);
						} else {
							feedOutFile(request, response, realFile);
							flushedResources.put(requestUriWithoutContextPath, Boolean.TRUE);
							LOGGER.log(Level.FINE, "Flushed file to webapp : " + requestUriWithoutContextPath);
							return;
						}
					} catch (Exception e) {
						LOGGER.log(Level.WARNING, "Error serving file from jar: " + requestUriWithoutContextPath, e);
					}
				}
			}
		}

		// if file is specially handled, flushed from the jar file or any error occurs, then let the server keep on going with it
		chain.doFilter(sreq, sres);
	}

	protected static String getBundleFromRequest(String requestUriWithoutContextPath) {
		requestUriWithoutContextPath = requestUriWithoutContextPath.trim();
		int index = requestUriWithoutContextPath.indexOf(BUNDLE_SUFFIX);

		if (index == -1) {
			if (requestUriWithoutContextPath.startsWith("http://")) {
				return null;
			}

			requestUriWithoutContextPath = StringHandler.replace(requestUriWithoutContextPath, BUNDLES_STANDARD_DIR, CoreConstants.EMPTY);
			int firstSlashIndex = requestUriWithoutContextPath.indexOf(CoreConstants.SLASH);
			if (firstSlashIndex == -1) {
				return requestUriWithoutContextPath;
			}
			return requestUriWithoutContextPath.substring(0, firstSlashIndex);
		}

		return requestUriWithoutContextPath.substring(BUNDLES_STANDARD_DIR.length(), index);
	}

	protected static String getResourceWithinBundle(String requestUriWithoutContextPath) {
		String rest = null;
		int index = requestUriWithoutContextPath.indexOf(BUNDLE_SUFFIX);
		if (index != -1) {
			rest = requestUriWithoutContextPath.substring(index + BUNDLE_SUFFIX.length() + 1);
		} else {
			String URIWithoutBundlesURI = requestUriWithoutContextPath.substring(BUNDLES_STANDARD_DIR.length()+1);
			index = URIWithoutBundlesURI.indexOf(CoreConstants.SLASH);
			rest = URIWithoutBundlesURI.substring(index);
		}

		if (rest.indexOf(";jsessionid") != -1)
			rest = rest.substring(0, rest.indexOf(";jsessionid"));

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
	public static File copyResourceFromJarToWebapp(IWMainApplication iwma, String requestUriWithoutContextPath) {
		return copyResourceFromJarOrCustomContentToWebapp(iwma, requestUriWithoutContextPath, null);
	}

	public static File copyResourceFromJarOrCustomContentToWebapp(
			IWMainApplication iwma,
			String requestUriWithoutContextPath,
			String fileContent
	) {
		return copyResourceFromJarOrCustomContentToWebapp(iwma, requestUriWithoutContextPath, fileContent, true);
	}

	private static File copyResourceFromJarOrCustomContentToWebapp(
			IWMainApplication iwma,
			String requestUriWithoutContextPath,
			String fileContent,
			boolean alwaysReturnFile
	) {
		String bundleIdentifier = getBundleFromRequest(requestUriWithoutContextPath);
		if (StringUtil.isEmpty(bundleIdentifier)) {
			if (requestUriWithoutContextPath.startsWith(CoreConstants.WEBDAV_SERVLET_URI + CoreConstants.PATH_FILES_ROOT + CoreConstants.SLASH)) {
				return getFileFromRepository(requestUriWithoutContextPath);
			}

			LOGGER.warning("Unknown bundle identifier, can not return requested file: " + requestUriWithoutContextPath);
			return null;
		}

		String pathWithinBundle = getResourceWithinBundle(requestUriWithoutContextPath);

		requestUriWithoutContextPath = StringHandler.replace(requestUriWithoutContextPath, CoreConstants.SLASH + CoreConstants.SLASH, File.separator);

		String webappFilePath = iwma.getApplicationRealPath() + requestUriWithoutContextPath;
		File webappFile = new File(webappFilePath);

		IWBundle bundle = iwma.getBundle(bundleIdentifier);
		long bundleLastModified = bundle.getResourceTime(pathWithinBundle);

		if (webappFile.exists()) {
			long webappLastModified = webappFile.lastModified();
			if (webappLastModified > bundleLastModified) {
				if (alwaysReturnFile) {
					return webappFile;
				}

				return null;
			}
		}

		if (StringUtil.isEmpty(fileContent) && requestUriWithoutContextPath.indexOf(ResourcesAdder.OPTIMIZIED_RESOURCES) != -1) {
			if (alwaysReturnFile) {
				return webappFile;
			}

			return null;
		}

		return copyFileContentToWebApp(iwma, requestUriWithoutContextPath, fileContent, pathWithinBundle, bundle, bundleLastModified);
	}

	private static File copyFileContentToWebApp(
			IWMainApplication iwma,
			String requestUriWithoutContextPath,
			String content,
			String pathWithinBundle,
			IWBundle bundle,
			long lastModified
	) {
		InputStream stream = null;
		String webappFilePath = iwma.getApplicationRealPath() + requestUriWithoutContextPath;
		String doubleSlash = CoreConstants.SLASH + CoreConstants.SLASH;
		try {
			//Special Windows handling:
			char separatorChar = File.separatorChar;
			//This is to handle the case when the URI contains the '/' character (in requestUriWithoutContextPath)
			// this '/' needs to be replaced with '\' for Windows
			if (separatorChar == FileUtil.WINDOWS_FILE_SEPARATOR) {
				webappFilePath = webappFilePath.replace(FileUtil.UNIX_FILE_SEPARATOR, FileUtil.WINDOWS_FILE_SEPARATOR);
			}

			if (StringUtil.isEmpty(content)) {
				if (webappFilePath.indexOf(doubleSlash) != -1) {
					webappFilePath = StringHandler.replace(webappFilePath, doubleSlash, File.separator);
				}
				if (pathWithinBundle.indexOf(doubleSlash) != -1) {
					pathWithinBundle = StringHandler.replace(pathWithinBundle, doubleSlash, File.separator);
				}

				stream = bundle.getResourceInputStream(pathWithinBundle);
			} else {
				stream = StringHandler.getStreamFromString(content);
			}
			File webappFile = FileUtil.getFileAndCreateRecursiveIfNotExists(webappFilePath);
			FileUtil.streamToFile(stream, webappFile);
			webappFile.setLastModified(lastModified);
			return webappFile;
		} catch (FileNotFoundException fnf) {
			if (pathWithinBundle.startsWith(CoreConstants.WEBDAV_SERVLET_URI + CoreConstants.PATH_FILES_ROOT + CoreConstants.SLASH)) {
				return getFileFromRepository(pathWithinBundle);
			} else {
				if (requestUriWithoutContextPath.indexOf(doubleSlash) != -1) {
					requestUriWithoutContextPath = StringHandler.replace(requestUriWithoutContextPath, doubleSlash, File.separator);
				}

				LOGGER.log(Level.WARNING, "Could not copy resource from jar (" + bundle.getBundleIdentifier() + ") to " + requestUriWithoutContextPath, fnf);
			}
		} catch (Exception e) {
			if (requestUriWithoutContextPath.indexOf(doubleSlash) != -1) {
				requestUriWithoutContextPath = StringHandler.replace(requestUriWithoutContextPath, doubleSlash, File.separator);
			}

			LOGGER.log(Level.WARNING, "Could not copy resource from jar (" + bundle.getBundleIdentifier() + ") to " + requestUriWithoutContextPath, e);
		} finally {
			IOUtil.closeInputStream(stream);
		}

		return null;
	}

	private static File getFileFromRepository(String path) {
		try {
			File file = CoreUtil.getFileFromRepository(path);
			if (file == null || !file.exists()) {
				throw new FileNotFoundException("File '" + path + "' can not be loaded from repository");
			}
			return file;
		} catch (Exception e) {
			LOGGER.log(Level.WARNING, "Error getting file '" + path + "' from repository", e);
		}
		return null;
	}

	/**
	 * Loads ALL resources (if any found) from bundle's JAR directory to real web app's directory
	 *
	 * @param iwma
	 * @param bundle
	 * @param pathInBundle - like 'resources/resourcesToLoadDirectory/'
	 */
	@SuppressWarnings("unchecked")
	public static final List<File> copyAllFilesFromJarDirectory(IWMainApplication iwma, IWBundle bundle, String pathInBundle) {
		Set<String> paths = iwma.getResourcePaths(IWModuleLoader.DEFAULT_LIB_PATH);
		if (ListUtil.isEmpty(paths)) {
			return null;
		}

		String expectedBundleJar = new StringBuilder(bundle.getBundleIdentifier()).append("-").toString();
		String bundleJar = null;
		for (Iterator<String> pathsIter = paths.iterator(); (pathsIter.hasNext() && bundleJar == null);) {
			bundleJar = pathsIter.next();

			if (bundleJar.indexOf(expectedBundleJar) == -1) {
				bundleJar = null;	//	Not bundle's JAR file
			}
		}
		if (StringUtil.isEmpty(bundleJar)) {
			return null;
		}

		if (bundleJar.startsWith(File.separator)) {
			bundleJar = bundleJar.replaceFirst(File.separator, CoreConstants.EMPTY);
		}
		String jarPath = IWMainApplication.getDefaultIWMainApplication().getApplicationRealPath() + bundleJar;
		JarInputStream jarStream = null;
		try {
			jarStream = new JarInputStream(new FileInputStream(new File(jarPath)));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (jarStream == null) {
			return null;
		}

		if (pathInBundle.startsWith(File.separator)) {
			pathInBundle = pathInBundle.replaceFirst(File.separator, CoreConstants.EMPTY);
		}
		String badBundlePathStart = "bundle" + File.separator;
		if (pathInBundle.startsWith(badBundlePathStart)) {
			pathInBundle = pathInBundle.replaceFirst(badBundlePathStart, CoreConstants.EMPTY);
		}

		String applicationPath = iwma.getApplicationRealPath();
		if (!applicationPath.endsWith(File.separator)) {
			applicationPath += File.separator;
		}
		String bundleRootPath = bundle.getRootVirtualPath();
		if (bundleRootPath.startsWith(File.separator)) {
			bundleRootPath = bundleRootPath.replaceFirst(File.separator, CoreConstants.EMPTY);
		}
		if (!bundleRootPath.endsWith(File.separator)) {
			bundleRootPath += File.separator;
		}
		String realDirectoryForExtractedFiles = applicationPath + bundleRootPath;
		InputStream stream = null;
		File file = null;
		boolean needToCopyFile = true;
		List<File> copiedFiles = new ArrayList<File>();
		String realPathToFile = null;
		try {
			for (ZipEntry entry = null; (entry = jarStream.getNextEntry()) != null;) {
				if (entry.getName().startsWith((pathInBundle))) {
					if (!entry.isDirectory()) {
						realPathToFile = new StringBuilder(realDirectoryForExtractedFiles).append(entry.getName()).toString();

						file = new File(realPathToFile);
						//	If file doesn't exist OR modified LATER than file copied into web application - need to re-copy file
						needToCopyFile = !file.exists() || bundle.getResourceTime(entry.getName()) > file.lastModified();

						if (needToCopyFile) {
							file = FileUtil.getFileAndCreateRecursiveIfNotExists(realPathToFile);
							stream = IOUtil.getStreamFromCurrentZipEntry(jarStream);
							FileUtil.streamToFile(stream, file);
							file.setLastModified(bundle.getResourceTime(pathInBundle));
							IOUtil.closeInputStream(stream);
						}

						copiedFiles.add(file);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} finally {
			IOUtil.closeInputStream(stream);
			IOUtil.closeInputStream(jarStream);
		}

		return copiedFiles;
	}

	/**
	 * @param realFile
	 */
	// private boolean d(HttpServletRequest request,String
	// bundleIdentifier,String filePathInBundle,File file) {
	private boolean speciallyHandleFile(HttpServletRequest request, String workspaceDir, String webappDir, String requestUriWithoutContextPath) {
		String fileEnding = getFileEnding(requestUriWithoutContextPath);
		if (fileEnding == null) {
			LOGGER.warning(this.getClass().getName() +": file ending is null!");
			return false;
		}

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
		} catch (FileNotFoundException e) {
			LOGGER.warning("File not found: " + realFile.getPath());
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
			while (read != -1) {
				out.write(barray, 0, read);
				read = streamToResource.read(barray);
			}
			streamToResource.close();
			out.flush();
			out.close();
		} catch (IOException e) {
			LOGGER.warning("Error streaming resource to " + request.getRequestURI());
		}
	}

	protected String getMimeType(String filePath){
		return MimeTypeUtil.resolveMimeTypeFromFileName(filePath);
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
	@Override
	public void destroy() {
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
	public static File copyWorkspaceFileToWebapp(String workspaceDir, String webappDir, String requestUriWithoutContextPath) {
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
				LOGGER.warning("File not found: " + fileInWorkspace.getPath());
				return null;
			}
			catch (IOException e) {
				LOGGER.warning("Error copying file: " + fileInWorkspace.getPath());
				return null;
			}
		}
		return fileInWebapp;
	}

	public static void checkCopyOfResourceToWebapp(FacesContext context, String resourceURI) {
		checkCopyOfResourceToWebapp(IWMainApplication.getIWMainApplication(context), resourceURI);
	}

	public static void checkCopyOfResourceToWebapp(IWMainApplication iwma, String resourceURI) {
		String bundlesProperty = System.getProperty(DefaultIWBundle.SYSTEM_BUNDLES_RESOURCE_DIR);

		File copiedFile = null;
		if (bundlesProperty != null) {
			String webappDir = iwma.getApplicationRealPath();
			String workspaceDir = bundlesProperty;
			String pathToBundleFileInWorkspace = resourceURI;
			copiedFile = copyWorkspaceFileToWebapp(workspaceDir, webappDir, pathToBundleFileInWorkspace);
		}

		if (copiedFile == null || IWMainApplication.loadBundlesFromJars) {
			//	One more try to copy resource
			copyResourceFromJarOrCustomContentToWebapp(iwma, resourceURI, null, true);
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


	@Override
	protected String getURIMinusContextPath(HttpServletRequest request) {
		String uriMinusContextPath = RequestUtil.getURIMinusContextPath(request);
		if (uriMinusContextPath != null && uriMinusContextPath.lastIndexOf("?") > 0) {
			return uriMinusContextPath.substring(0, uriMinusContextPath.lastIndexOf("?"));
		} else {
			return uriMinusContextPath;
		}
	}
}
