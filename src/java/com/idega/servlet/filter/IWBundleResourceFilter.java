/*
 * $Id: IWBundleResourceFilter.java,v 1.4 2005/02/01 00:55:03 tryggvil Exp $
 * Created on 27.1.2005
 *
 * Copyright (C) 2005 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
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
import com.idega.idegaweb.IWBundle;
import com.idega.util.FileUtil;


/**
 *  <p>
 *  Filter that can feed out resources (images/css etc.) from a set directory for all bundles.<br>
 *  This can be set with a System property idegaweb.bundles.resource.dir to to be the directory for the Eclipse workspace when developing.
 *  (Setting -Didegaweb.bundles.resource.dir=/idega/eclipse/workspace in the tomcat plugin preference pane).
 *  </p>
 * 
 *  Last modified: $Date: 2005/02/01 00:55:03 $ by $Author: tryggvil $
 * 
 * @author <a href="mailto:tryggvil@idega.com">tryggvil</a>
 * @version $Revision: 1.4 $
 */
public class IWBundleResourceFilter extends BaseFilter {
	
	private boolean feedFromSetBundleDir=false;
	private String idegawebDir = "/idegaweb";
	private String sBundlesDirectory;
	private File fBundlesDirectory;
	
	

	static String BUNDLES_STANDARD_DIR = "/idegaweb/bundles/";
	static String BUNDLE_SUFFIX = ".bundle";

	/* (non-Javadoc)
	 * @see javax.servlet.Filter#init(javax.servlet.FilterConfig)
	 */
	public void init(FilterConfig arg0) throws ServletException {
		String directory = System.getProperty("idegaweb.bundles.resource.dir");
		if(directory!=null){
			sBundlesDirectory=directory;
			feedFromSetBundleDir=true;
		}
	}

	/* (non-Javadoc)
	 * @see javax.servlet.Filter#doFilter(javax.servlet.ServletRequest, javax.servlet.ServletResponse, javax.servlet.FilterChain)
	 */
	public void doFilter(ServletRequest sreq, ServletResponse sres, FilterChain chain) throws IOException,
			ServletException {

		HttpServletRequest request = (HttpServletRequest)sreq;
		HttpServletResponse response = (HttpServletResponse)sres;
		
		if(feedFromSetBundleDir){
			try{
				String requestUriWithoutContextPath = getURIMinusContextPath(request);
				String[] parses = parseBundleDir(requestUriWithoutContextPath);
				String bundleIdentifier = parses[0];
				String urlWithinBundle = parses[1];
				
				//String bundleIdentifier = parseBundleIdentifier(requestUriWithoutContextPath);
				File bundleDir = getSetBundleDirectory(bundleIdentifier);
				//String urlWithinBundle = parseUrlWithinBundle(requestUriWithoutContextPath);
				File realFile = new File(bundleDir,urlWithinBundle);
				if(speciallyHandleFile(request,bundleIdentifier,urlWithinBundle, realFile)){
					chain.doFilter(sreq,sres);
				}
				else{
					if(realFile.exists()){
						feedOutFile(response,realFile);
					}
					else{
						//if the file doesn't exist try to use the default:
						chain.doFilter(sreq,sres);
					}
				}
			}
			catch(Exception e){
				chain.doFilter(sreq,sres);
			}
		}
		else{
			chain.doFilter(sreq,sres);
		}
	}
	
	
	private static String SVG="svg";
	private static String JSP="jsp";
	private static String PSVG="psvg";
	/**
	 * @param realFile
	 */
	private boolean speciallyHandleFile(HttpServletRequest request,String bundleIdentifier,String filePathInBundle,File file) {
		String fileEnding = getFileEnding(file);
		
		if(fileEnding.equalsIgnoreCase(PSVG)){
			copyFileToBundle(request,bundleIdentifier,filePathInBundle,file);
			return true;
		}
		else if(fileEnding.equalsIgnoreCase(SVG)){
			copyFileToBundle(request,bundleIdentifier,filePathInBundle,file);
			return true;
		}
		else if(fileEnding.equalsIgnoreCase(JSP)){
			copyFileToBundle(request,bundleIdentifier,filePathInBundle,file);
			return true;
			
		}
		return false;
	}
	
	protected void copyFileToBundle(HttpServletRequest request,String bundleIdentifier,String filePathInBundle,File file) {
		IWBundle iwb = getIWMainApplication(request).getBundle(bundleIdentifier);
		String bundleRealPath = iwb.getBundleBaseRealPath();
		File realBundleDir = new File(bundleRealPath);
		File realBundleFile = new File(realBundleDir,filePathInBundle);
		try {
			FileUtil.copyFile(file,realBundleFile);
		}
		catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private String getFileEnding(File realFile) {
		String fileName = realFile.getName();
		int index = fileName.lastIndexOf(".");
		if(index!=-1){
			return fileName.substring(index+1,fileName.length());
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
			//First set and guess the mime type
			setContentType(response,realFile);
			
			fis = new FileInputStream(realFile);
			OutputStream out = response.getOutputStream();
			int buffer = 1000;
			byte[] barray = new byte[buffer];
			int read = fis.read(barray);
			//out.write(barray);
			while(read!=-1){
				out.write(barray,0,read);
				read = fis.read(barray);
			}
			out.flush();
			out.close();
		}
		catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	protected void setContentType(HttpServletResponse response, File realFile) {
		String mimeType = getMimeType(realFile);
		if(mimeType!=null){
			response.setContentType(mimeType);
		}
	}

	protected String getMimeType(File realFile){
		String mimeType = FileIconSupplier.getInstance().guessMimeTypeFromFileName(realFile.getName());
		return mimeType;
	}
	
	private File getSetBundlesDirectory(){
		if(fBundlesDirectory==null){
			fBundlesDirectory=new File(getSetBundlesDirectoryAsString());
		}
		return fBundlesDirectory;
	}
	
	private String getSetBundlesDirectoryAsString(){
		return sBundlesDirectory;
	}
	
	private File getSetBundleDirectory(String bundleIdentifier){
		File f = new File(getSetBundlesDirectory(),bundleIdentifier);
		return f;
	}
	
	
	
	private String parseBundleIdentifier(String requestUri){
		String[] parse = parseBundleDir(requestUri);
		return parse[0];
	}
	
	
	protected String[] parseBundleDir(String requestUri){
		String urlBeginningWithBundle = requestUri.substring(BUNDLES_STANDARD_DIR.length(),requestUri.length());
		String bundleDir = urlBeginningWithBundle.substring(0,urlBeginningWithBundle.indexOf(SLASH));
		String bundleIdentifier = bundleDir;
		if(bundleDir.endsWith(BUNDLE_SUFFIX)){
			bundleIdentifier = bundleDir.substring(0,bundleDir.indexOf(BUNDLE_SUFFIX));
		}
		//String start = BUNDLES_STANDARD_DIR+bundleDir;
		String restUrl =  urlBeginningWithBundle.substring(bundleDir.length(),urlBeginningWithBundle.length());
		
		String[] theReturn = {bundleIdentifier,restUrl};
		
		return theReturn;
		
	}

	private String parseUrlWithinBundle(String requestUri){
		String[] parse = parseBundleDir(requestUri);
		return parse[1];
	}
	
	/* (non-Javadoc)
	 * @see javax.servlet.Filter#destroy()
	 */
	public void destroy() {
		// TODO Auto-generated method stub
	}
}
