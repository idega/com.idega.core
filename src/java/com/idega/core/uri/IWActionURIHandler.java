/*
 * $Id: IWActionURIHandler.java,v 1.1 2005/02/03 11:25:48 eiki Exp $
 * Created on 18.1.2005
 *
 * Copyright (C) 2005 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.core.uri;



/**
 * 
 *  Last modified: $Date: 2005/02/03 11:25:48 $ by $Author: eiki $
 * 
 * @author <a href="mailto:tryggvil@idega.com">tryggvil</a>
 * @version $Revision: 1.1 $
 */
public interface IWActionURIHandler {
	
	public String getRedirectURL(String requestURI);
	
	public String getRedirectURL(IWActionURI uri);	
	
}
