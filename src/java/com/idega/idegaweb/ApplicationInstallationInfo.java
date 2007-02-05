/*
 * $Id: ApplicationInstallationInfo.java,v 1.3 2007/02/05 06:55:21 tryggvil Exp $
 * Created on 4.1.2005
 *
 * Copyright (C) 2005 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.idegaweb;


/**
 *  This class holds information about the installation (customer instance) of the application.<br>
 * 
 *  Last modified: $Date: 2007/02/05 06:55:21 $ by $Author: tryggvil $
 * 
 * @author <a href="mailto:tryggvil@idega.com">tryggvil</a>
 * @version $Revision: 1.3 $
 */
public class ApplicationInstallationInfo {
	
	private String systemName;
	//String licenceOwner;
	private LicenceOwner licenceOwner;
	private LicencingInformation licencingInformation;
	private String hostingPartyName;
	private String setupPartyName;

	public ApplicationInstallationInfo(IWMainApplication application){
		//loadDummyData();
	}
	
	
	private void loadDummyData() {
		setSystemName("Tryggva kerfi");
		
		getLicenceOwner().setName("Google");
		getLicenceOwner().setLogoUrl("http://www.google.is/intl/en_com/images/logo_plain.png");
	}


	public class LicencingInformation{
		private String licenceKey;

		
		public String getLicenceKey() {
			return licenceKey;
		}

		
		public void setLicenceKey(String licenceKey) {
			this.licenceKey = licenceKey;
		}
	}
	
	public class LicenceOwner{
		private String name;
		private String logoUrl;
		private String mainWebUrl;
		private String address;
		
		public String getAddress() {
			return address;
		}
		
		public void setAddress(String address) {
			this.address = address;
		}
		
		public String getLogoUrl() {
			return logoUrl;
		}
		
		public void setLogoUrl(String logoUrl) {
			this.logoUrl = logoUrl;
		}
		
		public String getMainWebUrl() {
			return mainWebUrl;
		}
		
		public void setMainWebUrl(String mainWebUrl) {
			this.mainWebUrl = mainWebUrl;
		}
		
		public String getName() {
			return name;
		}
		
		public void setName(String name) {
			this.name = name;
		}
	}

	
	public String getHostingPartyName() {
		return hostingPartyName;
	}

	
	public void setHostingPartyName(String hostingPartyName) {
		this.hostingPartyName = hostingPartyName;
	}

	
	public LicenceOwner getLicenceOwner() {
		if(licenceOwner==null){
			licenceOwner=new LicenceOwner();
		}
		return licenceOwner;
	}

	
	public void setLicenceOwner(LicenceOwner licenceOwner) {
		this.licenceOwner = licenceOwner;
	}

	
	public LicencingInformation getLicencingInformation() {
		if(licencingInformation==null){
			licencingInformation=new LicencingInformation();
		}
		return licencingInformation;
	}

	
	public void setLicencingInformation(LicencingInformation licencingInformation) {
		this.licencingInformation = licencingInformation;
	}

	
	public String getSetupPartyName() {
		return setupPartyName;
	}

	
	public void setSetupPartyName(String setupPartyName) {
		this.setupPartyName = setupPartyName;
	}

	
	public String getSystemName() {
		return systemName;
	}

	
	public void setSystemName(String systemName) {
		this.systemName = systemName;
	}
	
}