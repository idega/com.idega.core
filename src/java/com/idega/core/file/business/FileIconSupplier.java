/*
 * Created on Jan 7, 2004
 *
 */
package com.idega.core.file.business;

import java.net.FileNameMap;
import java.net.URLConnection;

import com.idega.idegaweb.IWApplicationContext;
import com.idega.idegaweb.IWMainApplication;

/**
 * The <code>FileIconSupplier </code> supplies icon urls from filenames or file mimetypes
 * @author aron 
 * @version 1.0
 */
public class FileIconSupplier {
	
	private static final String bundleIdentifier =IWMainApplication.CORE_BUNDLE_IDENTIFIER;

	private static final String DEFAULT_ICON_PREFIX = "/icfileicons/ui/";
	private static final String DEFAULT_ICON_SUFFIX = ".gif";
	public static final String UI_WIN ="win";
	public static final String UI_MAC ="mac";
	public static final String UI_IW ="iw";
	private static final FileNameMap fileNameMap = URLConnection.getFileNameMap();
	
	private IWApplicationContext iwac =null;
	private String ui = UI_IW;
	private FileIconSupplier(IWApplicationContext  iwac){
		this.iwac = iwac;
	}	
	
	public static FileIconSupplier getInstance(IWApplicationContext  iwac){
		return new FileIconSupplier(iwac);
	}
	
	public static FileIconSupplier getInstance(IWApplicationContext  iwac,String ui){
		return new FileIconSupplier(iwac);
	}
	
	public String getFilenameIconURI(String fileName){
		return getMimetypeIconURI(getMimeType(fileName));
	}
	
	public String getMimetypeIconURI(String mimeType ){
		if(mimeType!=null)
			return getFileNameIconURI(mimeType);
		else
			return getUnknownIconURI();
	}
	
	private String getFileNameIconURI(String iconName){
		StringBuffer uri =new StringBuffer(getBundleResourceURI());
		uri.append(DEFAULT_ICON_PREFIX);
		uri.append(getUI());
		uri.append("/");
		uri.append(strip(iconName));
		uri.append(DEFAULT_ICON_SUFFIX);
		return uri.toString();	
	}
	
	private String strip(String mimeType){
		mimeType = mimeType.replace('\\', '_');
		mimeType = mimeType.replace('/', '_');
		mimeType = mimeType.replace(':', '_');
		mimeType = mimeType.replace('*', '_');
		mimeType = mimeType.replace('?', '_');
		mimeType = mimeType.replace('<', '_');
		mimeType = mimeType.replace('>', '_');
		mimeType = mimeType.replace('|', '_');
		mimeType = mimeType.replace('\"', '_');
		return mimeType;
	}
	
	private String getBundleResourceURI(){
		return iwac.getApplication().getBundle(bundleIdentifier).getResourcesURL();
	}
	
	public String getFolderIconURI(){
		return getFileNameIconURI("application_vnd.iw-folder");
	}
	
	public String getUnknownIconURI(){
		return getFileNameIconURI("unknown");
	}
	
	public String getMimeType(String fileName){
		return  fileNameMap.getContentTypeFor(fileName);
	}
	
	public String getUI(){
		return this.ui;
	}
	
	public void setUI(String ui){
		this.ui =ui;
	}
	
	
}
