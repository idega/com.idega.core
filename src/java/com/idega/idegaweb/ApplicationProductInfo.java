/*
 * $Id: ApplicationProductInfo.java,v 1.3 2005/01/06 18:22:59 tryggvil Exp $
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
 *  Last modified: $Date: 2005/01/06 18:22:59 $ by $Author: tryggvil $
 * 
 * @author <a href="mailto:tryggvil@idega.com">tryggvil</a>
 * @version $Revision: 1.3 $
 */
public class ApplicationProductInfo {
	
	private IWMainApplication iwma;
	
	//This will be swapped out by reading from /WEB-INF/idegaweb/properties/product.properties
	private String inceptionYear="2000";
	private String version="2.0-SNAPSHOT";
	private String buildId="20050101.000000";
	private String vendor="idega software";
	private String name="ePlatform";
	private String family="idegaWeb";
	
	
	public ApplicationProductInfo(IWMainApplication iwma){
		this.iwma=iwma;
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
		return buildId;
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
		return family;
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
		return inceptionYear;
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
		return name;
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
		return vendor;
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
		return version;
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
}
