/*
 * $Id: ApplicationProductInfo.java,v 1.8 2006/04/09 12:13:14 laddi Exp $
 * Created on 4.1.2005
 *
 * Copyright (C) 2005 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.idegaweb;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;
import com.idega.util.FileUtil;
import com.idega.util.IWTimestamp;


/**
 *  This class holds information about the application product installed.<br>
 * 
 *  Last modified: $Date: 2006/04/09 12:13:14 $ by $Author: laddi $
 * 
 * @author <a href="mailto:tryggvil@idega.com">tryggvil</a>
 * @version $Revision: 1.8 $
 */
public class ApplicationProductInfo {
	
	//This will be swapped out by reading from /WEB-INF/idegaweb/properties/product.properties
	private String inceptionYear="2000";
	private String version="3.0-SNAPSHOT";
	private String platformVersion="3.0-SNAPSHOT";
	private String buildId="20050101.000000";
	private String vendor="idega software";
	private String name="ePlatform";
	private String family="idegaWeb";
	
	
	public ApplicationProductInfo(IWMainApplication iwma){
		String filePath = iwma.getPropertiesRealPath()+FileUtil.getFileSeparator()+"product.properties";
		File file = new File(filePath);
		loadFromFile(file);
	}
	
	public void loadFromFile(File file){
		if(file.exists()){
			Properties properties = new Properties();
			try {
				properties.load(new FileInputStream(file));
				//iwma.sendStartupMessage("Loading product.properties from file: "+file.getPath());
				
				String inceptionYear = (String) properties.get("application.product.inceptionyear");
				if(inceptionYear!=null){
					setInceptionYear(inceptionYear);
				}
				String productVersion = (String) properties.get("application.product.version");
				if(productVersion!=null){
					setVersion(productVersion);
				}
				String buildId = (String) properties.get("application.product.build.id");
				if(buildId!=null){
					setBuildId(buildId);
				}
				String vendorName = (String) properties.get("application.product.vendor");
				if(vendorName!=null){
					setVendor(vendorName);
				}
				String productName = (String) properties.get("application.product.name");
				if(productName!=null){
					setName(productName);
				}
				String productFamily = (String) properties.get("application.product.family");
				if(productFamily!=null){
					setFamily(productFamily);
				}	
				
			}
			catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}

	}
	
	
	/**
	 * @return Returns the buildId.
	 */
	public String getBuildId() {
		return this.buildId;
	}
	/**
	 * @param buildId The buildId to set.
	 */
	public void setBuildId(String buildId) {
		this.buildId = buildId;
	}
	/**
	 * @return Returns the family.
	 */
	public String getFamily() {
		return this.family;
	}
	/**
	 * @param family The family to set.
	 */
	public void setFamily(String family) {
		this.family = family;
	}
	/**
	 * @return Returns the inceptionYear.
	 */
	public String getInceptionYear() {
		return this.inceptionYear;
	}
	/**
	 * @param inceptionYear The inceptionYear to set.
	 */
	public void setInceptionYear(String inceptionYear) {
		this.inceptionYear = inceptionYear;
	}
	/**
	 * @return Returns the name.
	 */
	public String getName() {
		return this.name;
	}
	/**
	 * @param name The name to set.
	 */
	public void setName(String name) {
		this.name = name;
	}
	
	/**
	 * Returns the productName with the productFamily
	 * @return
	 */
	public String getFullProductName(){
		return getFamily()+" "+getName();
	}
	/**
	 * @return Returns the vendor.
	 */
	public String getVendor() {
		return this.vendor;
	}
	/**
	 * @param vendor The vendor to set.
	 */
	public void setVendor(String vendor) {
		this.vendor = vendor;
	}
	/**
	 * @return Returns the version.
	 */
	public String getVersion() {
		return this.version;
	}
	/**
	 * @param version The version to set.
	 */
	public void setVersion(String version) {
		this.version = version;
	}
	
	public String getCopyrightText(){
		return "Copyright (c) "+getInceptionYear()+"-"+IWTimestamp.RightNow().getYear()+" "+getVendor()+" All rights reserved";
	}

	/**
	 * Gets the major version (the first integer in the version number)
	 * @return
	 */
	public int getMajorVersion(){
		String version = getVersion();
		int dotIndex = version.indexOf(".");
		String sMVersion = version.substring(0,dotIndex);
		return Integer.parseInt(sMVersion);
	}
	
	public boolean isMajorVersionEqualOrHigherThan(int version){
		int majorVersion = getMajorVersion();
		return (version<=majorVersion);
	}

	
	/**
	 * @return Returns the platformVersion.
	 */
	public String getPlatformVersion() {
		return this.platformVersion;
	}

	
	/**
	 * @param platformVersion The platformVersion to set.
	 */
	public void setPlatformVersion(String platformVersion) {
		this.platformVersion = platformVersion;
	}
	
	/**
	 * Gets the major version (the first integer in the version number)
	 * @return
	 */
	public int getMajorPlatformVersion(){
		String version = getPlatformVersion();
		int dotIndex = version.indexOf(".");
		String sMVersion = version.substring(0,dotIndex);
		return Integer.parseInt(sMVersion);
	}
	
	public boolean isMajorPlatformVersionEqualOrHigherThan(int version){
		int majorVersion = getMajorPlatformVersion();
		return (version<=majorVersion);
	}
	
}
