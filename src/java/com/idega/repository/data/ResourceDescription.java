/*
 * $Id: ResourceDescription.java,v 1.1 2005/10/03 18:24:19 thomas Exp $
 * Created on Sep 29, 2005
 *
 * Copyright (C) 2005 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 * 
 */
package com.idega.repository.data;

/**
 *  A container that holds metadata for a resource.
 *  <p/>
 *  Last modified: $Date: 2005/10/03 18:24:19 $ by $Author: thomas $
 *   
 * @author <a href="mailto:thomas@idega.com">thomas</a>
 * @version $Revision: 1.1 $
 * 
 * @see com.idega.repository.data.Resource
 */
public class ResourceDescription {
	
	private String source = null;
	private String provider = null;
	private boolean isEjb = false;
	
	public ResourceDescription(String source, String provider, boolean isEjb) {
		this.source = source;
		this.provider = provider;
		this.isEjb = isEjb;
	}

	
	public boolean isEjb() {
		return isEjb;
	}

	
	public String getProvider() {
		return provider;
	}

	
	public String getSource() {
		return source;
	}
}
