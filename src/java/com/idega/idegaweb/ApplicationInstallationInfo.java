/*
 * $Id: ApplicationInstallationInfo.java,v 1.1 2005/03/06 13:17:37 tryggvil Exp $
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
 *  Last modified: $Date: 2005/03/06 13:17:37 $ by $Author: tryggvil $
 * 
 * @author <a href="mailto:tryggvil@idega.com">tryggvil</a>
 * @version $Revision: 1.1 $
 */
public class ApplicationInstallationInfo {
	
	private String organizationName="Nacka kommun";
	private String organizationLogoUrl;
	private String customSolutionName="Nacka24";
	private String defaultDomainName="www.nacka24.nacka.se";
	private String serverType="production";// demo/test/production
	private String uniqueId;
}
